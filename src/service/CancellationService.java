package service;

import dto.PassengerSeatIdDto;
import dto.SeatUpdateRequestDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import model.*;
import repository.*;

import java.util.*;

public class CancellationService {
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;
    private final WaitingListRepository waitingListRepository;
    private final StationRepository stationRepository;

    public CancellationService(BookingRepository bookingRepository, SeatRepository seatRepository, WaitingListRepository waitingListRepository, PassengerRepository passengerRepository, StationRepository stationRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.waitingListRepository = waitingListRepository;
        this.passengerRepository = passengerRepository;
        this.stationRepository = stationRepository;
    }

    public boolean cancelFullBooking(String pnr) throws DataBaseException {

        Booking booking = bookingRepository.getBookingByPnr(pnr);
        int mask = calculateMask(stationRepository.getSequenceNo(booking.getFrom()), stationRepository.getSequenceNo(booking.getTo()));
        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);
        if (passengers == null || passengers.isEmpty()) throw new ItemNotFoundException("No Passenger found");

        int cnfReleased = cancelPassengers(passengers, booking.getTrainId(), mask);

        promoteRacToCnfSeats(booking.getTrainId(), cnfReleased);

        bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        return true;
    }

    public boolean partialCancellation(String pnr, Set<Integer> selectedPassengers) throws DataBaseException {
        Booking booking = bookingRepository.getBookingByPnr(pnr);
        if (booking == null) throw new InvalidInputException("No booking found for this PNR");
        int mask = calculateMask(stationRepository.getSequenceNo(booking.getFrom()), stationRepository.getSequenceNo(booking.getTo()));
        if (selectedPassengers == null || selectedPassengers.isEmpty()) { throw new InvalidInputException( "Select at least one passenger" ); }

        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);

        List<Passenger> toCancelPassengers = new ArrayList<>();
        for (Passenger p : passengers) {
            if (selectedPassengers.contains(p.getPassengerId())) {
                if (p.getStatus() == PassengerStatus.CANCELLED) continue;
                toCancelPassengers.add(p);
            }
        }
        int cancelled = cancelPassengers(toCancelPassengers, booking.getTrainId(), mask);
        promoteRacToCnfSeats(booking.getTrainId(), cancelled);
        if (checkAllPassengersAreRemoved(pnr)) {
            bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        }
        return true;
    }

    private int cancelPassengers(List<Passenger> passengers, int trainId, int requiredMask) throws DataBaseException {
        int cnfReleased = 0;
        List<SeatUpdateRequestDto> removeBooked = new ArrayList<>();
        List<PassengerSeatIdDto> dtos = new ArrayList<>();
        List<Integer> racPassenger = new ArrayList<>();
        List<Integer> wlPassenger = new ArrayList<>();

        for (Passenger p : passengers) {
            if (p.getStatus() == PassengerStatus.CNF) {
                int newMask = seatRepository.getSeatById(p.getSeatId()).getMask() | requiredMask;
                removeBooked.add(new SeatUpdateRequestDto(p.getSeatId(), newMask));
                cnfReleased++;
            }
            if (p.getStatus() == PassengerStatus.RAC) {
                racPassenger.add(p.getPassengerId());
            }
            if (p.getStatus() == PassengerStatus.WL) {
                wlPassenger.add(p.getPassengerId());
            }
            dtos.add(new PassengerSeatIdDto(
                    p.getPassengerId(),
                    -1
            ));
        }
        if (!removeBooked.isEmpty()) {
            seatRepository.updateAllSeatStatus(removeBooked, SeatStatus.AVAILABLE);
        }
        if (!dtos.isEmpty()) {
            passengerRepository.updateAllPassengerStatusAndSeatNo(dtos, PassengerStatus.CANCELLED);
        }
        if (!racPassenger.isEmpty()) {
            waitingListRepository.removeAllRacPassenger(racPassenger, trainId);
        }
        if (!wlPassenger.isEmpty()) {
            waitingListRepository.removeAllWlPassenger(wlPassenger, trainId);
        }
        return cnfReleased;
    }

    private boolean checkAllPassengersAreRemoved(String pnr) throws DataBaseException {
        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);
        for (Passenger p : passengers) {
            if (p.getStatus() != PassengerStatus.CANCELLED) {
                return false;
            }
        }
        return true;
    }
    private int calculateMask(int fromSeq, int toSeq) {
        long mask = 0L;
        for (int i=fromSeq; i<toSeq; i++) {
            mask |= (1L << (i - 1));
        }
        return (int) mask;
    }
    private void promoteWlToRac(int trainId) throws DataBaseException {
        int availableRac = waitingListRepository.getAvailableRac(trainId);

        if (availableRac <= 0) return;

        List<Passenger> wlPassengers = waitingListRepository.getWlPassengers(trainId, availableRac);
        List<Integer> wlRemove = new ArrayList<>();
        if (!wlPassengers.isEmpty()) {
            waitingListRepository.addAllRac(trainId, wlPassengers);
        }
        List<PassengerSeatIdDto> dtos = new ArrayList<>();
        for (Passenger p : wlPassengers) {
            wlRemove.add(p.getPassengerId());
            dtos.add(new PassengerSeatIdDto(
                    p.getPassengerId(),
                    -1
            ));
        }
        if (!wlRemove.isEmpty()) {
            waitingListRepository.removeAllWlPassenger(wlRemove, trainId);
        }
        if (!dtos.isEmpty()) {
            passengerRepository.updateAllPassengerStatusAndSeatNo(dtos, PassengerStatus.RAC);
        }
    }

    private void promoteRacToCnfSeats(int trainId, int cnfReleasedCnt) throws DataBaseException {
        if (cnfReleasedCnt <= 0) {
            promoteWlToRac(trainId);
            return;
        }

        List<PassengerSeatIdDto> cnfDtos = new ArrayList<>();
        List<Integer> racRemove = new ArrayList<>();
        List<Integer> wlRemove = new ArrayList<>();

        Map<Integer, Integer> pendingMasks = new LinkedHashMap<>();

        int racCnt = waitingListRepository.getNumberOfRac(trainId);
        if (racCnt > 0) {
            for (Passenger p : waitingListRepository.getRacPassengers(trainId, racCnt)) {
                Integer seatId = allocateSeat(trainId, getPassengerMask(p), pendingMasks);
                if (seatId == null) continue;   // doesn't fit anywhere, try next
                racRemove.add(p.getPassengerId());
                cnfDtos.add(new PassengerSeatIdDto(p.getPassengerId(), seatId));
            }
        }

        // 2. Then ALL WL passengers into whatever gaps remain
        int wlCnt = waitingListRepository.getNumberOfWl(trainId); // add if you don't have it
        if (wlCnt > 0) {
            for (Passenger p : waitingListRepository.getWlPassengers(trainId, wlCnt)) {
                Integer seatId = allocateSeat(trainId, getPassengerMask(p), pendingMasks);
                if (seatId == null) continue;
                wlRemove.add(p.getPassengerId());
                cnfDtos.add(new PassengerSeatIdDto(p.getPassengerId(), seatId));
            }
        }

        if (!racRemove.isEmpty()) {
            waitingListRepository.removeAllRacPassenger(racRemove, trainId);
        }
        if (!wlRemove.isEmpty()) {
            waitingListRepository.removeAllWlPassenger(wlRemove, trainId);
        }
        if (!pendingMasks.isEmpty()) {
            List<SeatUpdateRequestDto> updateSeat = new ArrayList<>();
            pendingMasks.forEach((seatId, mask) -> updateSeat.add(new SeatUpdateRequestDto(seatId, mask)));
            seatRepository.updateAllSeatStatus(updateSeat, SeatStatus.BOOKED);
        }
        if (!cnfDtos.isEmpty()) {
            passengerRepository.updateAllPassengerStatusAndSeatNo(cnfDtos, PassengerStatus.CNF);
        }

        promoteWlToRac(trainId);
    }
    private Integer allocateSeat(int trainId, int requiredMask, Map<Integer, Integer> pendingMasks)
            throws DataBaseException {
        List<Seat> candidates = seatRepository.getAvailableSeats(trainId, requiredMask);
        for (Seat seat : candidates) {
            int currentMask = pendingMasks.getOrDefault(seat.getSeatId(), seat.getMask());
            if ((currentMask & requiredMask) == requiredMask) {   // still free after earlier allocations
                pendingMasks.put(seat.getSeatId(), currentMask & ~requiredMask);
                return seat.getSeatId();
            }
        }
        return null;
    }

    private int getPassengerMask(Passenger p) throws DataBaseException {
        Booking b = bookingRepository.getBookingByPnr(p.getPnr());
        Station from = stationRepository.getStationByCode(b.getFrom());
        Station to = stationRepository.getStationByCode(b.getTo());
        return calculateMask(from.getSequenceNo(), to.getSequenceNo());
    }
}

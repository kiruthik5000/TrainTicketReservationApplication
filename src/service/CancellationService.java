package service;

import dto.PassengerSeatIdDto;
import dto.SeatUpdateRequestDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import model.*;
import repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        cancelPassengers(passengers, booking.getTrainId(), mask);

        promoteRacToCnf(booking.getTrainId(), mask);

        bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        return true;
    }

    private void promoteRacToCnf(int trainId, int requiredMask) throws DataBaseException {

        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId, requiredMask);

        int availableSeatCnt = availableSeats.size();
        if (availableSeatCnt > 0) {
            List<Passenger> racPassengers = waitingListRepository.getRacPassengers(trainId, availableSeats.size());

            List<SeatUpdateRequestDto> seatUpdateRequestDtos = new ArrayList<>();

            int racToCnfSeats = Math.min(availableSeatCnt, racPassengers.size());
            List<PassengerSeatIdDto> cnfDtos = new ArrayList<>();

            List<Integer> racRemove = new ArrayList<>();
            List<Integer> wlRemove = new ArrayList<>();

            for (int i = 0; i < racToCnfSeats; i++) {
                Seat curSeat = availableSeats.get(i);
                int newMask = curSeat.getMask() ^ requiredMask;
                Passenger curPassenger = racPassengers.get(i);
                racRemove.add(curPassenger.getPassengerId());
                cnfDtos.add(new PassengerSeatIdDto(
                        curPassenger.getPassengerId(),
                        curSeat.getSeatId()
                ));
                seatUpdateRequestDtos.add(new SeatUpdateRequestDto(curSeat.getSeatId(), newMask));
            }

            int remainingSeats = availableSeatCnt - racToCnfSeats;
            if (remainingSeats > 0) {
                List<Passenger> wlPassengers = waitingListRepository.getWlPassengers(trainId, (availableSeats.size() - racToCnfSeats));

                for (int i = 0; i < wlPassengers.size(); i++) {
                    Seat curSeat = availableSeats.get(racToCnfSeats + i);
                    Passenger curPassenger = wlPassengers.get(i);
                    wlRemove.add(curPassenger.getPassengerId());
                    cnfDtos.add(new PassengerSeatIdDto(
                            curPassenger.getPassengerId(),
                            curSeat.getSeatId()
                    ));
                    seatUpdateRequestDtos.add(new SeatUpdateRequestDto(curSeat.getSeatId(), requiredMask));
                }
            }

            if (!racRemove.isEmpty()) {
                waitingListRepository.removeAllRacPassenger(racRemove, trainId);
            }

            if (!wlRemove.isEmpty()) {
                waitingListRepository.removeAllWlPassenger(wlRemove, trainId);
            }
            if (!seatUpdateRequestDtos.isEmpty()) {
                seatRepository.updateAllSeatStatus(seatUpdateRequestDtos, SeatStatus.BOOKED);
            }

            if (!cnfDtos.isEmpty()) {
                passengerRepository.updateAllPassengerStatusAndSeatNo(cnfDtos, PassengerStatus.CNF);
            }
        }
        promoteWlToRac(trainId);
     }

     public void promoteWlToRac(int trainId) throws DataBaseException {
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
        cancelPassengers(toCancelPassengers, booking.getTrainId(), mask);
        promoteRacToCnf(booking.getTrainId(), mask);
        if (checkAllPassengersAreRemoved(pnr)) {
            bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        }
        return true;
    }

    private void cancelPassengers(List<Passenger> passengers, int trainId, int requiredMask) throws DataBaseException {
        List<SeatUpdateRequestDto> removeBooked = new ArrayList<>();
        List<PassengerSeatIdDto> dtos = new ArrayList<>();
        List<Integer> racPassenger = new ArrayList<>();
        List<Integer> wlPassenger = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getStatus() == PassengerStatus.CNF) {
                int newMask = seatRepository.getSeatById(p.getSeatId()).getMask() | requiredMask;
                removeBooked.add(new SeatUpdateRequestDto(p.getSeatId(), newMask));
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
}

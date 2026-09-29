package service;

import dto.PassengerSeatIdDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import model.*;
import repository.BookingRepository;
import repository.PassengerRepository;
import repository.SeatRepository;
import repository.WaitingListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CancellationService {
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;
    private final WaitingListRepository waitingListRepository;

    public CancellationService(BookingRepository bookingRepository, SeatRepository seatRepository, WaitingListRepository waitingListRepository, PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.waitingListRepository = waitingListRepository;
        this.passengerRepository = passengerRepository;
    }

    public boolean cancelFullBooking(String pnr) throws DataBaseException {

        Booking booking = bookingRepository.getBookingByPnr(pnr);

        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);
        if (passengers == null || passengers.isEmpty()) throw new ItemNotFoundException("No Passenger found");

//        for (Passenger p : passengers) {
//            cancelPassenger(p, booking.getTrainId());
//        }
        cancelPassengers(passengers, booking.getTrainId());

        promoteWlListToCnf(booking.getTrainId());

        bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        return true;
    }

//    private void cancelPassenger(Passenger p, int trainId) throws DataBaseException {
//        if (p.getStatus() == PassengerStatus.CANCELLED) return;
//
//        if (p.getStatus() == PassengerStatus.CNF) {
//            if (p.getSeatId() != -1) {
//                seatRepository.updateStatus(p.getSeatId(), SeatStatus.AVAILABLE);
//            }
//        }
//        if (p.getStatus() == PassengerStatus.RAC) {
//            waitingListRepository.removeRacPassenger(p.getPassengerId(), trainId);
//        }
//        if (p.getStatus() == PassengerStatus.WL) {
//            waitingListRepository.removeWlPassenger(p.getPassengerId(), trainId);
//        }
//        passengerRepository.updatePassengerStatusAndSeatNo(p.getPassengerId(), PassengerStatus.CANCELLED, -1);
//    }

    private void promoteWlListToCnf(int trainId) throws DataBaseException {
        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);

        if (availableSeats.isEmpty()) return;
        List<Passenger> racPassengers = waitingListRepository.getRacPassengers(trainId, availableSeats.size());

        List<Integer> seatIds = new ArrayList<>();

        int racToCnfSeats = Math.min(availableSeats.size(), racPassengers.size());
        List<PassengerSeatIdDto> cnfDtos = new ArrayList<>();

        for (int i=0; i<racToCnfSeats; i++) {
            Seat curSeat = availableSeats.get(i);
            Passenger curPassenger = racPassengers.get(i);
            cnfDtos.add(new PassengerSeatIdDto(
                    curPassenger.getPassengerId(),
                    curSeat.getSeatId()
            ));
            seatIds.add(curSeat.getSeatId());
        }

        if (racToCnfSeats < availableSeats.size()) {
            List<Passenger> wlPassengers = waitingListRepository.getWlPassengers(trainId, (availableSeats.size() - racToCnfSeats));

            for (int i=0; i<wlPassengers.size(); i++) {
                Seat curSeat = availableSeats.get(racToCnfSeats + i);
                Passenger curPassenger = wlPassengers.get(i);
                cnfDtos.add(new PassengerSeatIdDto(
                        curPassenger.getPassengerId(),
                        curSeat.getSeatId()
                ));
                seatIds.add(curSeat.getSeatId());
            }
        }
        seatRepository.updateAllSeatStatus(seatIds, SeatStatus.BOOKED);
        passengerRepository.updateAllPassengerStatusAndSeatNo(cnfDtos, PassengerStatus.CNF);
//        for (Seat s : availableSeats) {
//            int racPassengerId = waitingListRepository.getFirstRacPassenger(trainId);
//            if (racPassengerId != -1) {
//                passengerRepository.updatePassengerStatusAndSeatNo(racPassengerId, PassengerStatus.CNF, s.getSeatId());
//                seatRepository.updateStatus(s.getSeatId(), SeatStatus.BOOKED);
//                continue;
//            }
//            int wlPassengerId = waitingListRepository.getFirstWlPassenger(trainId);
//            if (wlPassengerId != -1) {
//                passengerRepository.updatePassengerStatusAndSeatNo(wlPassengerId, PassengerStatus.CNF, s.getSeatId());
//                seatRepository.updateStatus(s.getSeatId(), SeatStatus.BOOKED);
//                continue;
//            }
//            break;
//        }
        promoteWlToRac(trainId);
     }

     public void promoteWlToRac(int trainId) throws DataBaseException {
        int availableRac = waitingListRepository.getAvailableRac(trainId);

        if (availableRac <= 0) return;

        List<Passenger> wlPassengers = waitingListRepository.getWlPassengers(trainId, availableRac);

        if (!wlPassengers.isEmpty()) {
            waitingListRepository.addAllRac(trainId, wlPassengers);
        }
        List<PassengerSeatIdDto> dtos = new ArrayList<>();
        for (Passenger p : wlPassengers) {
            dtos.add(new PassengerSeatIdDto(
                    p.getPassengerId(),
                    -1
            ));
        }
        if (!dtos.isEmpty()) {
            passengerRepository.updateAllPassengerStatusAndSeatNo(dtos, PassengerStatus.RAC);
        }
     }

    public boolean partialCancellation(String pnr, Set<Integer> selectedPassengers) throws DataBaseException {
        Booking booking = bookingRepository.getBookingByPnr(pnr);
        if (booking == null) throw new InvalidInputException("No booking found for this PNR");
        if (selectedPassengers == null || selectedPassengers.isEmpty()) { throw new InvalidInputException( "Select at least one passenger" ); }

        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);

        List<Passenger> toCancelPassengers = new ArrayList<>();
        for (Passenger p : passengers) {
            if (selectedPassengers.contains(p.getPassengerId())) {
                if (p.getStatus() == PassengerStatus.CANCELLED) continue;
                toCancelPassengers.add(p);
            }
        }
        cancelPassengers(toCancelPassengers, booking.getTrainId());
        promoteWlListToCnf(booking.getTrainId());
        if (checkAllPassengersAreRemoved(pnr)) {
            bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        }
        return true;
    }

    private void cancelPassengers(List<Passenger> passengers, int trainId) {
        List<Integer> removeBooked = new ArrayList<>();
        List<PassengerSeatIdDto> dtos = new ArrayList<>();
        List<Integer> racPassenger = new ArrayList<>();
        List<Integer> wlPassenger = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getStatus() == PassengerStatus.CNF) {
                removeBooked.add(p.getSeatId());
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
}

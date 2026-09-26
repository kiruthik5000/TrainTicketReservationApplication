package service;

import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.UnAuthorizedAccessException;
import model.*;
import repository.BookingRepository;
import repository.PassengerRepository;
import repository.SeatRepository;
import repository.WaitingListRepository;
import utils.SessionStorage;

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

    public boolean cancelFullBooking(String pnr) {

        Booking booking = bookingRepository.getBookingById(pnr);

        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        if (passengers == null || passengers.isEmpty()) throw new ItemNotFoundException("No Passenger found");

        for (Passenger p : passengers) {
            cancelPassenger(p, booking.getTrainId());
        }

        promoteWlListToCnf(booking.getTrainId());

        bookingRepository.updateStatus(pnr, BookingStatus.CANCELLED);
        return true;
    }

    private void cancelPassenger(Passenger p, int trainId) {
        if (p.getStatus().equals(PassengerStatus.CANCELLED)) return;

        if (p.getStatus().equals(PassengerStatus.CNF)) {
            if (p.getSeatId() != -1) {
                seatRepository.updateStatus(p.getSeatId(), SeatStatus.AVAILABLE);
            }
        }
        if (p.getStatus().equals(PassengerStatus.RAC)) {
            waitingListRepository.removeRacPassenger(p.getPassengerId(), trainId);
        }
        if (p.getStatus().equals(PassengerStatus.WL)) {
            waitingListRepository.removeWlPassenger(p.getPassengerId(), trainId);
        }
        passengerRepository.updatePassengerStatusAndSeatNo(p.getPassengerId(), PassengerStatus.CANCELLED, -1);
    }

    public void promoteWlListToCnf(int trainId) {
        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);
        for (Seat s : availableSeats) {
            int racPassengerId = waitingListRepository.getFirstRacPassenger(trainId);
            if (racPassengerId != -1) {
                passengerRepository.updatePassengerStatusAndSeatNo(racPassengerId, PassengerStatus.CNF, s.getSeatNo());
                seatRepository.updateStatus(s.getSeatId(), SeatStatus.BOOKED);
                continue;
            }
            int wlPassengerId = waitingListRepository.getFirstWlPassenger(trainId);
            if (wlPassengerId != -1) {
                passengerRepository.updatePassengerStatusAndSeatNo(wlPassengerId, PassengerStatus.CNF, s.getSeatNo());
                seatRepository.updateStatus(s.getSeatId(), SeatStatus.BOOKED);
                continue;
            }
            break;
        }

        promoteWlToRac(trainId);
     }

     public void promoteWlToRac(int trainId) {
        int availableRac = waitingListRepository.getAvailableRac(trainId);
        for (int i=0; i<availableRac; i++) {
            int wlPassengerId = waitingListRepository.getFirstWlPassenger(trainId);
            if (wlPassengerId == -1) break;
            passengerRepository.updatePassengerStatusAndSeatNo(wlPassengerId, PassengerStatus.RAC, -1);
            waitingListRepository.addRac(trainId, wlPassengerId);
        }
     }

    public boolean partialCancellation(String pnr, Set<Integer> selectedPassengers) {
        if (SessionStorage.getCurrentUser() == null) throw new UnAuthorizedAccessException("User must logIn to Cancel Booking");
        Booking booking = bookingRepository.getBookingById(pnr);
        if (booking == null) throw new InvalidInputException("No booking found for this PNR");
        if (selectedPassengers == null || selectedPassengers.isEmpty()) { throw new InvalidInputException( "Select at least one passenger" ); }
        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        for (Passenger p : passengers) {
            if (selectedPassengers.contains(p.getPassengerId())) {
                if (p.getStatus().equals(PassengerStatus.CANCELLED)) continue;
                cancelPassenger(p, booking.getTrainId());
            }
        }
        promoteWlListToCnf(booking.getTrainId());
        if (checkAllPassengersAreRemoved(pnr)) {
            bookingRepository.updateStatus(pnr, BookingStatus.CANCELLED);
        }
        return true;
    }

    private boolean checkAllPassengersAreRemoved(String pnr) {
        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        for (Passenger p : passengers) {
            if (!p.getStatus().equals(PassengerStatus.CANCELLED)) {
                return false;
            }
        }
        return true;
    }
}

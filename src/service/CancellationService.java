package service;

import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import model.*;
import repository.BookingRepository;
import repository.PassengerRepository;
import repository.SeatRepository;
import repository.WaitingListRepository;

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

        for (Passenger p : passengers) {
            cancelPassenger(p, booking.getTrainId());
        }

        promoteWlListToCnf(booking.getTrainId());

        bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        return true;
    }

    private void cancelPassenger(Passenger p, int trainId) throws DataBaseException {
        if (p.getStatus() == PassengerStatus.CANCELLED) return;

        if (p.getStatus() == PassengerStatus.CNF) {
            if (p.getSeatId() != -1) {
                seatRepository.updateStatus(p.getSeatId(), SeatStatus.AVAILABLE);
            }
        }
        if (p.getStatus() == PassengerStatus.RAC) {
            waitingListRepository.removeRacPassenger(p.getPassengerId(), trainId);
        }
        if (p.getStatus() == PassengerStatus.WL) {
            waitingListRepository.removeWlPassenger(p.getPassengerId(), trainId);
        }
        passengerRepository.updatePassengerStatusAndSeatNo(p.getPassengerId(), PassengerStatus.CANCELLED, -1);
    }

    public void promoteWlListToCnf(int trainId) throws DataBaseException {
        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);
        for (Seat s : availableSeats) {
            int racPassengerId = waitingListRepository.getFirstRacPassenger(trainId);
            if (racPassengerId != -1) {
                passengerRepository.updatePassengerStatusAndSeatNo(racPassengerId, PassengerStatus.CNF, s.getSeatId());
                seatRepository.updateStatus(s.getSeatId(), SeatStatus.BOOKED);
                continue;
            }
            int wlPassengerId = waitingListRepository.getFirstWlPassenger(trainId);
            if (wlPassengerId != -1) {
                passengerRepository.updatePassengerStatusAndSeatNo(wlPassengerId, PassengerStatus.CNF, s.getSeatId());
                seatRepository.updateStatus(s.getSeatId(), SeatStatus.BOOKED);
                continue;
            }
            break;
        }

        promoteWlToRac(trainId);
     }

     public void promoteWlToRac(int trainId) throws DataBaseException {
        int availableRac = waitingListRepository.getAvailableRac(trainId);
        for (int i=0; i<availableRac; i++) {
            int wlPassengerId = waitingListRepository.getFirstWlPassenger(trainId);
            if (wlPassengerId == -1) break;
            passengerRepository.updatePassengerStatusAndSeatNo(wlPassengerId, PassengerStatus.RAC, -1);
            waitingListRepository.addRac(trainId, wlPassengerId);
        }
     }

    public boolean partialCancellation(String pnr, Set<Integer> selectedPassengers) throws DataBaseException {
        Booking booking = bookingRepository.getBookingByPnr(pnr);
        if (booking == null) throw new InvalidInputException("No booking found for this PNR");
        if (selectedPassengers == null || selectedPassengers.isEmpty()) { throw new InvalidInputException( "Select at least one passenger" ); }

        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);

        for (Passenger p : passengers) {
            if (selectedPassengers.contains(p.getPassengerId())) {
                if (p.getStatus() == PassengerStatus.CANCELLED) continue;
                cancelPassenger(p, booking.getTrainId());
            }
        }
        promoteWlListToCnf(booking.getTrainId());
        if (checkAllPassengersAreRemoved(pnr)) {
            bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);
        }
        return true;
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

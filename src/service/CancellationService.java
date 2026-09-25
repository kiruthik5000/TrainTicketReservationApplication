package service;

import exception.InvalidInputException;
import exception.UnAuthorizedAccessException;
import model.*;
import repository.BookingRepository;
import repository.PassengerRepository;
import repository.SeatRepository;
import repository.WaitingListRepository;
import utils.SessionStorage;

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

    public boolean cancelFullBooking(String pnr) throws NoSuchFieldException {
        if (SessionStorage.getCurrentUser() == null) throw new UnAuthorizedAccessException("User must logIn to Cancel Booking");
        Booking booking = bookingRepository.getBookingById(pnr);
        if (booking == null) throw new InvalidInputException("No booking found for this PNR");
        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        if (passengers == null || passengers.isEmpty()) throw new NoSuchFieldException("No Passenger found");
        List<Passenger> waitingListPassengers = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getSeatId() == -1){
                waitingListPassengers.add(p);
                continue;
            } else {
                seatRepository.updateStatus(p.getSeatId(), SeatStatus.AVAILABLE);
            }
            passengerRepository.updatePassengerStatusAndSeatNo(p.getPassengerId(), PassengerStatus.CANCELLED, -1);
        }
        updateAndReassignWaitingList(waitingListPassengers, booking.getTrainId());
        bookingRepository.updateStatus(pnr, BookingStatus.CANCELLED);
        return true;
    }

    public void updateAndReassignWaitingList(List<Passenger> wlPassengers, int trainId) {
        for (Passenger p : wlPassengers) {
            if (p.getStatus().equals(PassengerStatus.RAC)) {
                waitingListRepository.removeRacPassenger(p.getPassengerId(), trainId);
            } else if (p.getStatus().equals(PassengerStatus.WL)) {
                waitingListRepository.removeWlPassenger(p.getPassengerId(), trainId);
            }
        }
        updateRac(trainId);
        updateWl(trainId);
    }

    public boolean partialCancellation(String pnr, Set<Integer> selectedPassengers) {
        if (SessionStorage.getCurrentUser() == null) throw new UnAuthorizedAccessException("User must logIn to Cancel Booking");
        Booking booking = bookingRepository.getBookingById(pnr);
        if (booking == null) throw new InvalidInputException("No booking found for this PNR");
        if (selectedPassengers == null || selectedPassengers.isEmpty()) { throw new InvalidInputException( "Select at least one passenger" ); }
        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        List<Passenger> waitingListPassengers = new ArrayList<>();
        for (Passenger p : passengers) {
            if (selectedPassengers.contains(p.getPassengerId())) {
                if (p.getStatus().equals(PassengerStatus.CNF)) {
                    passengerRepository.updatePassengerStatusAndSeatNo(p.getPassengerId(), PassengerStatus.CANCELLED, -1);
                } else {
                    waitingListPassengers.add(p);
                }
            }
        }
        updateAndReassignWaitingList(waitingListPassengers, booking.getTrainId());
        return true;
    }

    private void updateRac(int trainId) {
        List<Seat> cnfSeats = seatRepository.getAvailableSeats(trainId);
        for (Seat curSeat : cnfSeats) {
            int curPid = waitingListRepository.getFirstRacPassenger(trainId);
            if (curPid == -1) break;
            passengerRepository.updatePassengerStatusAndSeatNo(curPid, PassengerStatus.CNF, curSeat.getSeatId());
            seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
        }
    }
    private void updateWl(int trainId) {
        int AvailableRac = waitingListRepository.getAvailableRac(trainId);
        for (int i=0; i<AvailableRac; i++) {
            int curPid = waitingListRepository.getFirstWlPassenger(trainId);
            if (curPid == -1) break;
            passengerRepository.updatePassengerStatusAndSeatNo(curPid, PassengerStatus.RAC, -1);
            waitingListRepository.addRac(trainId, curPid);
        }
    }
}

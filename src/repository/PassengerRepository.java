package repository;

import model.Passenger;
import model.PassengerStatus;

import java.util.List;

public interface PassengerRepository {
    List<Passenger> getPassengerByBooking(String bookingId);
    Passenger addPassenger(Passenger passenger);
    void updatePassengerStatusAndSeatNo(int id, PassengerStatus status, int seatNo);
}

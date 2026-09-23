package repository;

import model.Passenger;

import java.util.List;

public interface PassengerRepository {
    List<Passenger> getPassengerByBooking(int bookingId);
    void addPassenger(Passenger passenger);
}

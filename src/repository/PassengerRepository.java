package repository;

import model.Passenger;

import java.util.List;

public interface PassengerRepository {
    List<Passenger> getPassengerByBooking(String bookingId);
    Passenger addPassenger(Passenger passenger);
}

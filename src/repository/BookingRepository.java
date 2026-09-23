package repository;

import model.Booking;

import java.util.List;

public interface BookingRepository {
    List<Booking> getBookingByUser(int userId);
}

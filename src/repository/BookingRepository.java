package repository;

import model.Booking;
import model.BookingStatus;

import java.util.List;

public interface BookingRepository {
    List<Booking> getBookingByUser(int userId);
    void addBooking(Booking booking);
    Booking getBookingById(String pnr);
    void updateStatus(String pnr, BookingStatus status);
    List<Booking> getAllBookingsByTrainId(int trainId);
    List<Booking> getActiveBookings(int userId);
}

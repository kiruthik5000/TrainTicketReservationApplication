package repository;

import model.Booking;
import model.BookingStatus;

import java.util.List;

public interface BookingRepository {
    List<Booking> getBookingByUser(int userId);
    void addBooking(Booking booking);
    Booking getBookingByPnr(String pnr);
    void updateStatus(int bookingId, BookingStatus status);
    List<Booking> getAllBookingsByTrainId(int trainId);
    List<Booking> getActiveBookings(int userId);
}

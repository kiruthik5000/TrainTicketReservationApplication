package repository;

import model.Booking;
import model.BookingStatus;

import java.util.List;

public interface BookingRepository {
    List<Booking> getBookingByUser(int userId);
    Booking addBooking(Booking booking);
    boolean isMatchPnr(String pnr);
    Booking getBookingById(String pnr);
    void updateStatus(String pnr, BookingStatus status);
    List<Booking> getAllBookingsByTrainId(int trainId);
}

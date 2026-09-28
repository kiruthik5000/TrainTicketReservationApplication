package repository;

import exception.DataBaseException;
import model.Booking;
import model.BookingStatus;

import java.util.List;

public interface BookingRepository {
    List<Booking> getBookingByUser(int userId) throws DataBaseException;
    void addBooking(Booking booking) throws DataBaseException;
    Booking getBookingByPnr(String pnr) throws DataBaseException;
    void updateStatus(int bookingId, BookingStatus status) throws DataBaseException;
    List<Booking> getAllBookingsByTrainId(int trainId) throws DataBaseException;
    List<Booking> getActiveBookings(int userId) throws DataBaseException;
    int getTotalNoRows() throws DataBaseException;
}

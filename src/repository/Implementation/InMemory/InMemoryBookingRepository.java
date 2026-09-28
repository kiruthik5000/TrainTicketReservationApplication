package repository.Implementation.InMemory;

import exception.DataBaseException;
import model.Booking;
import model.BookingStatus;
import repository.BookingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBookingRepository implements BookingRepository {
    private int nextBookingId = 1;
    private final Map<Integer, Booking> bookingMap;

    public InMemoryBookingRepository() {
        this.bookingMap = new HashMap<>();
        bookingMap.put(1, new Booking(1, "123456789", "CBE", "TBM", 1, 1, BookingStatus.ACTIVE));
    }

    @Override
    public List<Booking> getBookingByUser(int userId) {
        return bookingMap
                .values()
                .stream()
                .filter(k -> k.getUserId() == userId)
                .toList();
    }

    @Override
    public void addBooking(Booking booking) {
        booking.setBookingId(++nextBookingId);
        bookingMap.put(booking.getBookingId(), booking);
    }

    @Override
    public Booking getBookingByPnr(String pnr){
        return bookingMap.values()
                .stream()
                .filter(k->k.getPnr().equals(pnr))
                .findFirst().orElse(null);
    }

    @Override
    public void updateStatus(int bookingId, BookingStatus status) {
        bookingMap.get(bookingId).setStatus(status);
    }

    @Override
    public List<Booking> getAllBookingsByTrainId(int trainId) {
        return bookingMap.values()
                .stream()
                .filter(booking -> booking.getTrainId() == trainId)
                .toList();
    }

    @Override
    public List<Booking> getActiveBookings(int userId) {
        return bookingMap
                .values()
                .stream()
                .filter(b->b.getUserId()==userId && b.getStatus().equals(BookingStatus.ACTIVE))
                .toList();
    }

    @Override
    public int getTotalNoRows() throws DataBaseException {
        return bookingMap.size();
    }
}

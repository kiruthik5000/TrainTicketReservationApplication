package repository.Implementation;

import model.Booking;
import model.BookingStatus;
import repository.BookingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> bookingMap;

    public InMemoryBookingRepository() {
        this.bookingMap = new HashMap<>();
        bookingMap.put("123456789", new Booking("123456789", "CBE", "TBM", 1, 1, BookingStatus.ACTIVE));
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
        bookingMap.put(booking.getBookingId(), booking);
    }

    @Override
    public Booking getBookingById(String pnr){return bookingMap.getOrDefault(pnr, null);}

    @Override
    public void updateStatus(String pnr, BookingStatus status) {
        bookingMap.get(pnr).setStatus(status);
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
}

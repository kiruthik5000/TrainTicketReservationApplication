package repository.Implementation;

import model.Booking;
import repository.BookingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBookingRepository implements BookingRepository {
    private Map<String, Booking> bookingMap;

    public InMemoryBookingRepository() {
        this.bookingMap = new HashMap<>();
        bookingMap.put("123456789", new Booking("123456789", "CBE", "TBM", 1, 1));
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
    public Booking addBooking(Booking booking) {
        bookingMap.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public boolean isMatchPnr(String pnr) {
        return bookingMap.containsKey(pnr);
    }
}

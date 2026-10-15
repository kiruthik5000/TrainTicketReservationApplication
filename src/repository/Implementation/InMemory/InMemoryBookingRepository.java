package repository.Implementation.InMemory;

import model.Booking;
import model.BookingStatus;
import repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryBookingRepository implements BookingRepository {
    private final AtomicInteger nextBookingId;
    private final Map<Integer, Booking> bookingMap;
    private final Map<String, Integer> pnrBookingMap;
    private final Map<Integer, List<Integer>> userBookingMap;

    public InMemoryBookingRepository() {
        nextBookingId = new AtomicInteger(1);
        this.userBookingMap = new ConcurrentHashMap<>();
        this.pnrBookingMap = new ConcurrentHashMap<>();
        this.bookingMap = new ConcurrentHashMap<>();

        pnrBookingMap.put("123456789", 1);
        bookingMap.put(1, new Booking(1, "123456789", "A", "B", 1, 1, BookingStatus.ACTIVE));
        userBookingMap.put(1, new ArrayList<>(List.of(1)));
    }


    @Override
    public List<Booking> getBookingByUser(int userId) {
        if (!userBookingMap.containsKey(userId)) return null;
        List<Integer> userBookingIds = userBookingMap.get(userId);
        List<Booking> bookingList = new ArrayList<>();
        for (int id : userBookingIds) {
            bookingList.add(bookingMap.get(id));
        }
        return bookingList;
    }

    @Override
    public void addBooking(Booking booking) {
        nextBookingId.addAndGet(1);
        booking.setBookingId(nextBookingId.get());
        bookingMap.put(booking.getBookingId(), booking);
        pnrBookingMap.put(booking.getPnr(), booking.getBookingId());
        userBookingMap.computeIfAbsent(booking.getUserId(), k->new ArrayList<>()).add(booking.getBookingId());
    }

    @Override
    public Booking getBookingByPnr(String pnr){
        if (!pnrBookingMap.containsKey(pnr)) return null;
        return bookingMap.get(pnrBookingMap.get(pnr));
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
    public int getTotalNoRows() {
        return bookingMap.size();
    }
}

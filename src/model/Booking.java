package model;

public class Booking {
    private String bookingId;
    private String from;
    private String to;
    private int trainId;
    private int userId;

    public Booking(String bookingId, String from, String to, int trainId, int userId) {
        this.bookingId = bookingId;
        this.from = from;
        this.to = to;
        this.trainId = trainId;
        this.userId = userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTrainId() {
        return trainId;
    }

    public int getUserId() {
        return userId;
    }
}

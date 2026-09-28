package model;

public class Booking {
    private int bookingId;
    private String pnr;
    private String from;
    private String to;
    private BookingStatus status;
    private int trainId;
    private int userId;

    public Booking(int bookingId,String pnr, String from, String to, int trainId, int userId, BookingStatus status) {
        this.bookingId = bookingId;
        this.pnr = pnr;
        this.from = from;
        this.to = to;
        this.trainId = trainId;
        this.userId = userId;
        this.status = status;
    }
    public int getBookingId() {return bookingId;}
    public String getPnr() {
        return pnr;
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

    public BookingStatus getStatus() {return status;}

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    public void setBookingId(int bookingId) {this.bookingId = bookingId;}
}

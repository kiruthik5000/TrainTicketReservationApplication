package model;

public class Passenger {
    private int passengerId;
    private String name;
    private int age;
    private PassengerStatus status;
    private int sequenceNo;
    private String bookingId;
    private int seatId;

    public Passenger(int passengerId, String name, int age, PassengerStatus status, int sequenceNo, String bookingId, int seatId) {
        this.passengerId = passengerId;
        this.name = name;
        this.age = age;
        this.status = status;
        this.bookingId = bookingId;
        this.sequenceNo = sequenceNo;
        this.seatId = seatId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getSequenceNo(){return sequenceNo;}

    public PassengerStatus getStatus() {
        return status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getSeatId() {
        return seatId;
    }
}

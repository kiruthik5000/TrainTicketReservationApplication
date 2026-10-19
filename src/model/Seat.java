package model;

public class Seat {
    private int seatId;
    private int seatNo;
    private SeatStatus status;
    private boolean[] booked;
    private int trainId;
    private SeatType seatType;

    public Seat(int seatId, int seatNo, int trainId, SeatStatus status, SeatType seatType) {
        this.seatId = seatId;
        this.status = status;
        this.seatNo = seatNo;
        this.trainId = trainId;
        this.booked = new boolean[3];
        this.seatType = seatType;
    }

    public boolean[] getMask() {return booked;}
    public SeatStatus getStatus() {
        return status;
    }

    public int getSeatId() {
        return seatId;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
    public void setMask(boolean[] curBooked) {this.booked = curBooked;}
    public SeatType getSeatType() { return this.seatType;}
}

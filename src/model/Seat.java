package model;

public class Seat {
    private int seatId;
    private int seatNo;
    private SeatStatus status;
    private int mask;
    private int trainId;

    public Seat(int seatId, int seatNo, int trainId, SeatStatus status, int mask) {
        this.seatId = seatId;
        this.status = status;
        this.seatNo = seatNo;
        this.trainId = trainId;
        this.mask = mask;
    }

    public int getMask() {return mask;}
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
    public void setMask(int mask) {this.mask = mask;}

}

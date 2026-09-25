package model;

public class Seat {
    private int seatId;
    private int seatNo;
    private SeatStatus status;
    private int trainId;

    public Seat(int seatId, int seatNo, int trainId, SeatStatus status) {
        this.seatId = seatId;
        this.status = status;
        this.seatNo = seatNo;
        this.trainId = trainId;
    }

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

}

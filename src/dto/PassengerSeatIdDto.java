package dto;

public class PassengerSeatIdDto {
    private int passengerId;
    private int seatId;

    public PassengerSeatIdDto(int passengerId, int seatId) {
        this.passengerId = passengerId;
        this.seatId = seatId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public int getSeatId() {
        return seatId;
    }
}

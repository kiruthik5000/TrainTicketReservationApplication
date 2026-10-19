package dto;

public class SeatUpdateRequestDto {
    private int seatId;
    private boolean[] curBooking;

    public SeatUpdateRequestDto(int seatId, boolean[] curBooking) {
        this.seatId = seatId;
        this.curBooking = curBooking;
    }

    public int getSeatId() {
        return seatId;
    }

    public boolean[] getMask() {
        return curBooking;
    }
}

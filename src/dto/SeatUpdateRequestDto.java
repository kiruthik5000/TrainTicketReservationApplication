package dto;

public class SeatUpdateRequestDto {
    private int seatId;
    private int mask;

    public SeatUpdateRequestDto(int seatId, int mask) {
        this.seatId = seatId;
        this.mask = mask;
    }

    public int getSeatId() {
        return seatId;
    }

    public int getMask() {
        return mask;
    }
}

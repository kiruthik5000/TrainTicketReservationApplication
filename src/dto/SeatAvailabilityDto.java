package dto;

import model.SeatType;

public class SeatAvailabilityDto {
    private SeatType seatType;
    private String availabilityStatus;
    private int availability;

    public SeatAvailabilityDto(SeatType seatType, String seatClass, int availability) {
        this.seatType = seatType;
        this.availabilityStatus = seatClass;
        this.availability = availability;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }
    public SeatType getSeatType() {return seatType;}

    @Override
    public String toString() {
        if (availabilityStatus.equals("REGRET")) return "Regret No more seats left";
        return seatType+" : "+ " Available "+availabilityStatus+" : "+availability;
    }
}

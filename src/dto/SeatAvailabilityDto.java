package dto;

import model.SeatType;

public class SeatAvailabilityDto {
    private SeatType seatType;
    private String availabilityStatus;
    private int availability;
    private double price;

    public SeatAvailabilityDto(SeatType seatType, String seatClass, int availability, double price) {
        this.seatType = seatType;
        this.availabilityStatus = seatClass;
        this.availability = availability;
        this.price = price;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }
    public SeatType getSeatType() {return seatType;}

    @Override
    public String toString() {
        if (availabilityStatus.equals("REGRET")) return "Regret No more seats left";
        return seatType+" : "+ " Available "+availabilityStatus+" : "+availability+"\nPrice: "+price;
    }
}

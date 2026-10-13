package dto;

public class SeatAvailabilityDto {
    private String seatClass;
    private int availability;

    public SeatAvailabilityDto(String seatClass, int availability) {
        this.seatClass = seatClass;
        this.availability = availability;
    }

    public String getSeatClass() {
        return seatClass;
    }

    @Override
    public String toString() {
        if (seatClass.equals("REGRET")) return "Regret No more seats left";
        return "Available "+seatClass+" : "+availability;
    }
}

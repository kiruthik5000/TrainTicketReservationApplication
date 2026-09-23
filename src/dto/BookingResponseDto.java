package dto;

import model.Train;

public class BookingResponseDto {
    private String pnr;
    private String from;
    private String to;
    private Train train;
    private int noOfPassengers;

    public BookingResponseDto(String pnr, String from, String to, Train train, int noOfPassengers) {
        this.pnr = pnr;
        this.from = from;
        this.to = to;
        this.train = train;
        this.noOfPassengers = noOfPassengers;
    }

    @Override
    public String toString() {
        return "pnr: " + pnr + "\n"+ from + " -- "+to + "\n" +
                "train: " + train +
                "noOfPassengers=" + noOfPassengers;
    }
}

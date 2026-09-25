package dto;

import model.BookingStatus;
import model.Train;

import java.util.List;

public class BookingResponseDto {
    private String pnr;
    private String from;
    private String to;
    private Train train;
    private BookingStatus status;
    private int noOfPassengers;
    private List<PassengerResponseDto> passengerResponseDtoList;

    public BookingResponseDto(String pnr, String from, String to, Train train, int noOfPassengers, List<PassengerResponseDto> passengerResponseDtos, BookingStatus status) {
        this.pnr = pnr;
        this.from = from;
        this.to = to;
        this.train = train;
        this.noOfPassengers = noOfPassengers;
        this.passengerResponseDtoList = passengerResponseDtos;
        this.status = status;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pnr: " + pnr +"( "+status+" )"+ "\n"+ from + " -- "+to + "\ntrain: " + train + "\nNumber Of Passengers : " + noOfPassengers);
        for (int i=0; i<passengerResponseDtoList.size(); i++) {
            sb.append("\n").append((i + 1)+". "+passengerResponseDtoList.get(i).toString());
        }
        return sb.toString();
    }
}

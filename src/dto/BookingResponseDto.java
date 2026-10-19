package dto;

import model.BookingStatus;
import model.Seat;
import model.SeatType;
import model.Train;

import java.util.List;

public class BookingResponseDto {
    private String pnr;
    private Train train;
    private BookingStatus status;
    private SeatType seatType;
    private int noOfPassengers;
    private List<PassengerResponseDto> passengerResponseDtoList;

    public BookingResponseDto(String pnr, Train train, int noOfPassengers, List<PassengerResponseDto> passengerResponseDtos, BookingStatus status, SeatType seatType) {
        this.pnr = pnr;
        this.train = train;
        this.noOfPassengers = noOfPassengers;
        this.passengerResponseDtoList = passengerResponseDtos;
        this.status = status;
        this.seatType=seatType;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pnr: " + pnr +"( "+status+" )"+ "\ntrain: " + train +"\n"+"Seat Type : "+seatType+ "\nNumber Of Passengers : " + noOfPassengers);
        for (int i=0; i<passengerResponseDtoList.size(); i++) {
            sb.append("\n").append((i + 1)+". "+passengerResponseDtoList.get(i).toString());
        }
        return sb.toString();
    }
}

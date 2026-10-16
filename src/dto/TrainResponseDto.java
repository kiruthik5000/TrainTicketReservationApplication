package dto;

import model.Station;
import model.Train;

import java.time.Duration;

public class TrainResponseDto {
    private Train train;
    private Station from;
    private Station to;
    private SeatAvailabilityDto seatAvailabilityDto;

    public Train getTrain() {
        return this.train;
    }

    public SeatAvailabilityDto getSeatAvailabilityDto() {
        return seatAvailabilityDto;
    }

    public TrainResponseDto(Train train, SeatAvailabilityDto seatAvailabilityDto, Station from, Station to) {
        this.train = train;
        this.seatAvailabilityDto = seatAvailabilityDto;
        this.from = from;
        this.to = to;
    }
    private String calculateDifference() {
        Duration d = Duration.between(from.getArrivalTime(), to.getArrivalTime());
        if (d.isNegative()) {
            d = d.plusHours(24);   // the journey crossed midnight
        }
        return d.toHours()+" hr "+(d.toMinutes() % 60) + " min";
    }

    @Override
    public String toString() {
        return train+"\n"+
                from.getStationCode()+" - "+from.getArrivalTime().toString()
                +"\n"
                +calculateDifference()+"\n"+to.getStationCode()+" - "+to.getArrivalTime().toString()+
                "\n"+seatAvailabilityDto;
    }
}

package dto;

import model.Station;
import model.Train;

import java.time.Duration;
import java.util.List;

public class TrainResponseDto {
    private Train train;
    private Station from;
    private Station to;
    private List<SeatAvailabilityDto> availabilityDtos;

    public Train getTrain() {
        return this.train;
    }

    public List<SeatAvailabilityDto> getSeatAvailabilityDto() {
        return availabilityDtos;
    }

    public TrainResponseDto(Train train, List<SeatAvailabilityDto> seatAvailabilityDto, Station from, Station to) {
        this.train = train;
        this.availabilityDtos = seatAvailabilityDto;
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

    public String onlyTrain() {
        return train+"\n"+
                from.getStationCode()+" - "+from.getArrivalTime().toString()
                +"\n"
                +calculateDifference()+"\n"+to.getStationCode()+" - "+to.getArrivalTime().toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (SeatAvailabilityDto s : availabilityDtos) {
            if (s == null) {
                sb.append("No Seats left.\n");
                break;
            }
            sb.append(s).append("\n");
        }
        return train+"\n"+
                from.getStationCode()+" - "+from.getArrivalTime().toString()
                +"\n"
                +calculateDifference()+"\n"+to.getStationCode()+" - "+to.getArrivalTime().toString()+
                "\n\n"+sb.toString();
    }
}

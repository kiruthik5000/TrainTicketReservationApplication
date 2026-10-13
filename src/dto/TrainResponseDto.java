package dto;

import model.Train;

public class TrainResponseDto {
    private Train train;
    private SeatAvailabilityDto seatAvailabilityDto;

    public Train getTrain() {
        return this.train;
    }

    public SeatAvailabilityDto getSeatAvailabilityDto() {
        return seatAvailabilityDto;
    }

    public TrainResponseDto(Train train, SeatAvailabilityDto seatAvailabilityDto) {
        this.train = train;
        this.seatAvailabilityDto = seatAvailabilityDto;
    }

    @Override
    public String toString() {
        return train+"\n"+seatAvailabilityDto;
    }
}

package repository.Implementation;

import model.Seat;
import model.SeatStatus;
import repository.SeatRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemorySeatRepository implements SeatRepository {
    private final Map<Integer, Seat> seatMap;

    public InMemorySeatRepository() {
        this.seatMap = new HashMap<>();
        seatMap.put(1, new Seat(1, 1, 1, SeatStatus.BOOKED));
        seatMap.put(2, new Seat(2, 2, 1, SeatStatus.AVAILABLE));
        seatMap.put(3, new Seat(3, 3, 1, SeatStatus.AVAILABLE));
    }

    @Override
    public List<Seat> getAvailableSeats(int trainId) {
        return seatMap
                .values()
                .stream()
                .filter(seat -> seat.getTrainId() == trainId && seat.getStatus().equals(SeatStatus.AVAILABLE))
                .toList();
    }

    @Override
    public void updateStatus(int seatId, SeatStatus status) {
        seatMap.get(seatId).setStatus(status);
    }
}

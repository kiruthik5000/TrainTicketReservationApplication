package repository.Implementation.InMemory;

import model.Seat;
import model.SeatStatus;
import repository.SeatRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySeatRepository implements SeatRepository {
    private final Map<Integer, Seat> seatMap;
    private final Map<Integer, List<Integer>> trainSeatMap;

    public InMemorySeatRepository() {
        this.seatMap = new ConcurrentHashMap<>();
        this.trainSeatMap = new ConcurrentHashMap<>();

        seatMap.put(1, new Seat(1, 1, 1, SeatStatus.BOOKED));
        seatMap.put(2, new Seat(2, 2, 1, SeatStatus.AVAILABLE));
        seatMap.put(3, new Seat(3, 3, 1, SeatStatus.AVAILABLE));

        trainSeatMap.put(1, new ArrayList<>(List.of(1, 2, 3)));
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

    @Override
    public Seat getSeatById(int seatId) {
        return seatMap.getOrDefault(seatId, null);
    }

    @Override
    public void updateAllSeatStatus(List<Integer> seatIds, SeatStatus status) {
        for (int s : seatIds) {
            seatMap.get(s).setStatus(status);
        }
    }
}

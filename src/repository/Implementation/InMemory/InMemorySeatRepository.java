package repository.Implementation.InMemory;

import dto.SeatUpdateRequestDto;
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

        seatMap.put(1, new Seat(1, 1, 1, SeatStatus.BOOKED, 6));
        seatMap.put(2, new Seat(2, 2, 1, SeatStatus.AVAILABLE, 7));
        seatMap.put(3, new Seat(3, 3, 1, SeatStatus.AVAILABLE, 7));

        trainSeatMap.put(1, new ArrayList<>(List.of(1, 2, 3)));
    }

    @Override
    public List<Seat> getAvailableSeats(int trainId, int mask) {
        List<Integer> seatIds = trainSeatMap.getOrDefault(trainId, new ArrayList<>());
        List<Seat> availableSeats = new ArrayList<>();
        for (int i : seatIds) {
            Seat curSeat = seatMap.get(i);
            if (isAvailable(curSeat.getMask(), mask)) {
                availableSeats.add(curSeat);
            }
        }
        return availableSeats;
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
    public void updateAllSeatStatus(List<SeatUpdateRequestDto> seatIds, SeatStatus status) {
        for (SeatUpdateRequestDto s : seatIds) {
            seatMap.get(s.getSeatId()).setStatus(status);
            seatMap.get(s.getSeatId()).setMask(s.getMask());
        }
    }
    public boolean isAvailable(int curMast, int req) {
        return (curMast & req) == req;
    }
}

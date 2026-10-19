package repository.Implementation.InMemory;

import dto.SeatUpdateRequestDto;
import model.Seat;
import model.SeatType;
import model.SeatStatus;
import repository.SeatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySeatRepository implements SeatRepository {
    private final Map<Integer, Seat> seatMap;
    private final Map<Integer, List<Integer>> trainSeatMap;

    public InMemorySeatRepository() {
        this.seatMap = new ConcurrentHashMap<>();
        this.trainSeatMap = new ConcurrentHashMap<>();

        seatMap.put(1, new Seat(1, 1, 1, SeatStatus.BOOKED, SeatType.SLEEPER));
        seatMap.put(2, new Seat(2, 2, 1, SeatStatus.AVAILABLE, SeatType.SLEEPER));
        seatMap.put(3, new Seat(3, 3, 1, SeatStatus.AVAILABLE, SeatType.SLEEPER));
        seatMap.put(4, new Seat(4, 1, 1, SeatStatus.AVAILABLE, SeatType.AC));
        seatMap.put(5, new Seat(5, 2, 1, SeatStatus.AVAILABLE, SeatType.AC));
        seatMap.put(6, new Seat(6, 3, 1, SeatStatus.AVAILABLE, SeatType.AC));

        seatMap.get(1).setMask(new boolean[]{true, false, false});

        trainSeatMap.put(1, new ArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
    }

    @Override
    public List<Seat> getAvailableSeats(int trainId, SeatType sc, int fromSeq, int toSeq) {
        List<Integer> seatIds = trainSeatMap.getOrDefault(trainId, new ArrayList<>());
        List<Seat> availableSeats = new ArrayList<>();
        for (int i : seatIds) {
            Seat curSeat = seatMap.get(i);
            if (curSeat.getSeatType().equals(sc)) {
                if (isAvailable(curSeat.getMask(), fromSeq, toSeq)) {
                    availableSeats.add(curSeat);
                }
            }
        }
        return availableSeats;
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
    public boolean isAvailable(boolean[] curMask, int from, int to) {
        for (int i=from; i<to; i++) {
            if (curMask[i - 1]) return false;
        }
        return true;
    }
}

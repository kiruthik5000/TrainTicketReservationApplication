package repository.Implementation.InMemory;

import dto.SeatUpdateRequestDto;
import model.Seat;
import model.SeatType;
import model.SeatStatus;
import repository.SeatRepository;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySeatRepository implements SeatRepository {
    private final Map<Integer, Seat> seatMap;
    private final Map<Integer, List<Integer>> trainSeatMap;

    public InMemorySeatRepository() {
        this.seatMap = new ConcurrentHashMap<>();
        this.trainSeatMap = new ConcurrentHashMap<>();

        seatMap.put(1, new Seat(1, 1, 1, SeatStatus.BOOKED, SeatType.SLEEPER, new BitSet()));
        seatMap.put(2, new Seat(2, 2, 1, SeatStatus.AVAILABLE, SeatType.SLEEPER, new BitSet()));
        seatMap.put(3, new Seat(3, 3, 1, SeatStatus.AVAILABLE, SeatType.SLEEPER, new BitSet()));
        seatMap.put(4, new Seat(4, 1, 1, SeatStatus.AVAILABLE, SeatType.AC, new BitSet()));
        seatMap.put(5, new Seat(5, 2, 1, SeatStatus.AVAILABLE, SeatType.AC, new BitSet()));
        seatMap.put(6, new Seat(6, 3, 1, SeatStatus.AVAILABLE, SeatType.AC, new BitSet()));

        seatMap.get(1).setMask(new boolean[]{true, false, false});
//        BitSet cur = seatMap.get(1).getBitMask();
//        cur.set(0);
//        seatMap.get(1).setBitMask(cur);

        for (Map.Entry<Integer, Seat> entry : seatMap.entrySet()) {
            trainSeatMap.computeIfAbsent(entry.getValue().getTrainId(), k->new ArrayList<>()).add(entry.getKey());
        }
//        seatMap.get(6).setMask(new boolean[]{false, false, false, false});
    }

    @Override
    public List<Seat> getAvailableSeats(int trainId, SeatType sc, int fromSeq, int toSeq) {
        List<Integer> seatIds = trainSeatMap.getOrDefault(trainId, new ArrayList<>());
        List<Seat> availableSeats = new ArrayList<>();
        for (int i : seatIds) {
            Seat curSeat = seatMap.get(i);
            if (curSeat.getSeatType().equals(sc)) {
                if (isAvailable(curSeat.getMask(), curSeat.getBitMask(), fromSeq, toSeq)) {
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
    public boolean isAvailable(boolean[] curMask, BitSet occupied, int from, int to) {
        for (int i=from; i<to; i++) {
            if (curMask[i - 1]) return false;
        }
        return true;
//        BitSet cur = new BitSet();
//        cur.set(from - 1, to);
//        return !occupied.intersects(cur);
    }
}

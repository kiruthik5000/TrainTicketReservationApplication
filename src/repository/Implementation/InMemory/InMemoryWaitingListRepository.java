package repository.Implementation.InMemory;

import exception.SeatNotFoundException;
import model.Passenger;
import model.SeatType;
import repository.WaitingListRepository;

import java.util.*;

public class InMemoryWaitingListRepository implements WaitingListRepository {
    public int racLimit = 2;
    public int wlLimit = 2;
    private final Map<String, LinkedList<Passenger>> racList; // 2 position
    private final Map<String, LinkedList<Passenger>> wlList; // 2 position

    public InMemoryWaitingListRepository() {
        String k1 = key(1, SeatType.SLEEPER);
        String k2 = key(1, SeatType.AC);
        racList = new HashMap<>();
        racList.put(k1, new LinkedList<>());
        racList.put(k2, new LinkedList<>());

        wlList = new HashMap<>();
        wlList.put(k1, new LinkedList<>());
        wlList.put(k2, new LinkedList<>());
    }
    private String key(int trainId, SeatType st) {
        return trainId+":"+st.name();
    }

    @Override
    public void addAllRac(int trainId, SeatType seatType, List<Passenger> passengers) {
        String key = key(trainId, seatType);
        LinkedList<Passenger> curList = racList.getOrDefault(key, new LinkedList<>());
        for (Passenger p : passengers) {
            if (curList.size() >= racLimit) throw new SeatNotFoundException("RAC limit exceeded");
            curList.add(p);
        }
    }

    @Override
    public void addAllWl(int trainId, SeatType seatType, List<Passenger> passengers) {
        String key = key(trainId, seatType);
        LinkedList<Passenger> curList = wlList.getOrDefault(key, new LinkedList<>());
        for (Passenger p : passengers) {
            if (curList.size() >= wlLimit) throw new SeatNotFoundException("WL limit exceeded");
            curList.add(p);
        }
    }

    @Override
    public int getAvailableRac(int trainId, SeatType seatType) {
        String key = key(trainId, seatType);

        if (racList.get(key) == null) return -1;
        return racLimit - racList.get(key).size();
    }

    @Override
    public int getAvailableWl(int trainId, SeatType seatType) {
        String key = key(trainId, seatType);

        if (wlList.get(key) == null) return -1;
        return wlLimit - wlList.get(key).size();
    }

    @Override
    public void removeAllRacPassenger(List<Integer> pIds, int trainId, SeatType seatType) {
        String key = key(trainId, seatType);
        if (racList.containsKey(key)) {
            for (int pId : pIds) {
                racList.get(key).removeIf(k -> k.getPassengerId() == pId);
            }
        }
    }

    @Override
    public void removeAllWlPassenger(List<Integer> pIds, int trainId, SeatType seatType) {
        String key = key(trainId, seatType);
        if (wlList.containsKey(key)) {
            for (int pId : pIds) {
                wlList.get(key).removeIf(k -> k.getPassengerId() == pId);
            }
        }
    }

    @Override
    public List<Passenger> getRacPassengers(int trainId, SeatType seatType, int limit) {
        String key = key(trainId, seatType);
        LinkedList<Passenger> list = racList.getOrDefault(key, new LinkedList<>());
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(list.subList(0, Math.min(limit, list.size())));
    }

    @Override
    public List<Passenger> getWlPassengers (int trainId, SeatType seatType, int limit) {
        String key = key(trainId, seatType);
        LinkedList<Passenger> list = wlList.getOrDefault(key, new LinkedList<>());
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(list.subList(0, Math.min(limit, list.size())));
    }

    @Override
    public int getRacNo(int trainId, SeatType seatType, int pId) {
        return getSequenceNo(trainId, pId, racList, seatType);
    }

    @Override
    public int getWlNo(int trainId, SeatType seatType, int pId) {
        return getSequenceNo(trainId, pId, wlList, seatType);
    }

    @Override
    public int getNumberOfRac(int trainId, SeatType seatType) {
        return racLimit;
    }

    @Override
    public int getNumberOfWl(int trainId, SeatType seatType) {
        return wlLimit;
    }

    private int getSequenceNo(int trainId, int pId, Map<String, LinkedList<Passenger>> wlList, SeatType seatType) {
        String key = key(trainId, seatType);
        LinkedList<Passenger> curList = wlList.get(key);
        if (curList == null) return -1;
        for (int i = 0; i < curList.size(); i++) {
            if (curList.get(i).getPassengerId() == pId) {
                return i + 1;
            }
        }
        return -1;
    }
}

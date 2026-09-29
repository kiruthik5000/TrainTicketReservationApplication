package repository.Implementation.InMemory;

import exception.InvalidInputException;
import model.Passenger;
import repository.WaitingListRepository;

import java.util.*;

public class InMemoryWaitingListRepository implements WaitingListRepository {
    public int racLimit = 2;
    public int wlLimit = 2;
    private final Map<Integer, LinkedList<Passenger>> racList; // 2 position
    private final Map<Integer, LinkedList<Passenger>> wlList; // 2 position

    public InMemoryWaitingListRepository() {
        racList = new HashMap<>();
        racList.put(1, new LinkedList<>());
        wlList = new HashMap<>();
        wlList.put(1, new LinkedList<>());
    }

    public void addRac(int trainId, Passenger passenger) {
        LinkedList<Passenger> curList = racList.getOrDefault(trainId, new LinkedList<>());
        if (curList.size() >= racLimit) throw new InvalidInputException("RAC is Full");
        curList.offerLast(passenger);
    }

    public void addWl(int trainId, Passenger passenger) {
       LinkedList<Passenger> curList = wlList.getOrDefault(trainId, new LinkedList<>());
       if (curList.size() >= wlLimit) throw new InvalidInputException("WL is Full");
       curList.offerLast(passenger);
    }

    @Override
    public void addAllRac(int trainId, List<Passenger> passengers) {
        LinkedList<Passenger> curList = racList.getOrDefault(trainId, new LinkedList<>());
        for (Passenger p : passengers) {
            if (curList.size() >= racLimit) break;
            curList.add(p);
        }
    }

    @Override
    public void addAllWl(int trainId, List<Passenger> passengers) {
        LinkedList<Passenger> curList = wlList.getOrDefault(trainId, new LinkedList<>());
        for (Passenger p : passengers) {
            if (curList.size() >= wlLimit) break;
            curList.add(p);
        }
    }

    @Override
    public int getAvailableRac(int trainId) {
        return racLimit - racList.get(trainId).size();
    }

    @Override
    public int getAvailableWl(int trainId) {
        return wlLimit - wlList.get(trainId).size();
    }

    @Override
    public void removeAllRacPassenger(List<Integer> pIds, int trainId) {
        if (racList.containsKey(trainId)) {
            for (int pId : pIds) {
                racList.get(trainId).removeIf(k -> k.getPassengerId() == pId);
            }
        }
    }

    @Override
    public void removeAllWlPassenger(List<Integer> pIds, int trainId) {
        if (wlList.containsKey(trainId)) {
            for (int pId : pIds) {
                wlList.get(trainId).removeIf(k -> k.getPassengerId() == pId);
            }
        }
    }

    @Override
    public List<Passenger> getRacPassengers(int trainId, int limit) {
        LinkedList<Passenger> list = racList.getOrDefault(trainId, new LinkedList<>());
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(list.subList(0, Math.min(limit, list.size())));
    }

    @Override
    public List<Passenger> getWlPassengers (int trainId, int limit) {
        LinkedList<Passenger> list = wlList.getOrDefault(trainId, new LinkedList<>());
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(list.subList(0, Math.min(limit, list.size())));
    }

    @Override
    public int getRacNo(int trainId, int pId) {
        return getSequenceNo(trainId, pId, racList);
    }

    @Override
    public int getWlNo(int trainId, int pId) {
        return getSequenceNo(trainId, pId, wlList);
    }

    private int getSequenceNo(int trainId, int pId, Map<Integer, LinkedList<Passenger>> wlList) {
        LinkedList<Passenger> curList = wlList.get(trainId);
        if (curList == null) return -1;
        for (int i = 0; i < curList.size(); i++) {
            if (curList.get(i).getPassengerId() == pId) {
                return i + 1;
            }
        }
        return -1;
    }
}

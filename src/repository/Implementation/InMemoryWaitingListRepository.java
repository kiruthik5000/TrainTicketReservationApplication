package repository.Implementation;

import repository.WaitingListRepository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryWaitingListRepository implements WaitingListRepository {
    public int racLimit = 2;
    public int wlLimit = 2;
    private final Map<Integer, LinkedList<Integer>> racList; // 2 position
    private final Map<Integer, LinkedList<Integer>> wlList; // 2 position

    public InMemoryWaitingListRepository() {
        racList = new HashMap<>();
        racList.put(1, new LinkedList<>());
        wlList = new HashMap<>();
        wlList.put(1, new LinkedList<>());
    }


    @Override
    public int getRACAvailabilityNo(int trainId) {
        int curSize = racList
                .get(trainId)
                .size();
        if (curSize >= racLimit) return -1;
        return curSize + 1;
    }

    @Override
    public int getWLNAvailability(int trainId) {
        int curSize = wlList
                .get(trainId)
                .size();
        if (curSize >= wlLimit) return -1;
        return curSize + 1;
    }

    @Override
    public void addRac(int trainId, int passengerId) {
        racList.putIfAbsent(trainId, new LinkedList<>());
        racList.get(trainId).add(passengerId);
    }

    @Override
    public void addWl(int trainId, int passengerId) {
        wlList.put(trainId, new LinkedList<>());
        wlList.get(trainId).add(passengerId);
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
    public void removeRacPassenger(int pId, int trainId) {
        if (racList.containsKey(trainId)) {
            racList.get(trainId).remove(pId);
        }
    }

    @Override
    public void removeWlPassenger(int pId, int trainId) {
        if (wlList.containsKey(trainId)) {
            wlList.get(trainId).remove(pId);
        }
    }

    @Override
    public int getFirstRacPassenger(int trainId) {
        if (racList.containsKey(trainId) && !racList.get(trainId).isEmpty()) {
            return racList.get(trainId).pollFirst();
        }
        return -1;
    }

    @Override
    public int getFirstWlPassenger(int trainId) {
        if (wlList.containsKey(trainId) && !wlList.get(trainId).isEmpty()) {
            return wlList.get(trainId).pollFirst();
        }
        return -1;
    }

    @Override
    public int getRacNo(int trainId, int pId) {
        LinkedList<Integer> curList = racList.get(trainId);
        for (int i=0; i<curList.size(); i++) {
            if (curList.get(i) == pId) return i + 1;
        }
        return -1;
    }

    @Override
    public int getWlNo(int trainId, int pId) {
        LinkedList<Integer> curList = wlList.get(trainId);
        for (int i=0; i<curList.size(); i++) {
            if (curList.get(i) == pId) return i + 1;
        }
        return -1;
    }
}

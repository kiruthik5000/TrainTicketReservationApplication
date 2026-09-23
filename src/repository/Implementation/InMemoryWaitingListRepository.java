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
    public int getRACNo(int trainId) {
        int curSize = racList
                .get(trainId)
                .size();
        if (curSize >= racLimit) return -1;
        return curSize + 1;
    }

    @Override
    public int getWLNo(int trainId) {
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
}

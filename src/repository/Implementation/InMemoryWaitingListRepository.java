package repository.Implementation;

import repository.WaitingListRepository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryWaitingListRepository implements WaitingListRepository {
    public final int racLimit = 2;
    public final int wlLimit = 2;
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
        return racList
                .get(trainId)
                .size();
    }

    @Override
    public int getWLNo(int trainId) {
        return wlList
                .get(trainId)
                .size();
    }
}

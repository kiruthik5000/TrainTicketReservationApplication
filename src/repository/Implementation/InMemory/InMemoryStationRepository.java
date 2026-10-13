package repository.Implementation.InMemory;

import model.Station;
import repository.StationRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStationRepository implements StationRepository {
    private final Map<Integer, Station> stationMap;
    private final Map<String, Integer> codeStationMap;

    public InMemoryStationRepository() {
        stationMap = new ConcurrentHashMap<>();
        codeStationMap = new ConcurrentHashMap<>();

        stationMap.put(1, new Station(1, "Tambaram", "A", 1));
        stationMap.put(2, new Station(2, "Central", "B", 2));
        stationMap.put(3, new Station(3, "Trichy", "C", 3));
        stationMap.put(4, new Station(4, "Coimbatore", "D", 4));

        for (Map.Entry<Integer, Station> cur : stationMap.entrySet()) {
            codeStationMap.put(cur.getValue().getStationCode(), cur.getKey());
        }
    }

    @Override
    public int getSequenceNo(String stationCode) {
        return stationMap.get(codeStationMap.get(stationCode)).getSequenceNo();
    }

    @Override
    public Station getStationByCode(String stationCode) {
        if (codeStationMap.containsKey(stationCode)) return stationMap.get(codeStationMap.get(stationCode));
        return null;
    }
}
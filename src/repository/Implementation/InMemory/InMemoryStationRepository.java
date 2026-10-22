package repository.Implementation.InMemory;

import model.Station;
import repository.StationRepository;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStationRepository implements StationRepository {
    private final Map<Integer, Station> stationMap;
    private final Map<String, Integer> codeStationMap;
    private final Map<Integer, List<String>> trainStationMap;
    private final Map<String, Map<Integer, Integer>> stationTrainSequenceMap;

    public InMemoryStationRepository() {
        stationMap = new ConcurrentHashMap<>();
        codeStationMap = new ConcurrentHashMap<>();
        stationTrainSequenceMap = new ConcurrentHashMap<>();
        trainStationMap = new ConcurrentHashMap<>();

        stationMap.put(1, new Station(1, "Tambaram", "A", 1, 1, LocalTime.of(10, 0), 50));
        stationMap.put(2, new Station(2, "Central", "B", 2, 1, LocalTime.of(11, 32), 100));
        stationMap.put(3, new Station(3, "Trichy", "C", 3, 1, LocalTime.of(13, 42), 150));
        stationMap.put(4, new Station(4, "Coimbatore", "D", 4, 1, LocalTime.of(15, 17), 200));
        stationMap.put(5, new Station(5, "Tambaram", "X", 1, 2, LocalTime.of(10, 23), 50));
        stationMap.put(6, new Station(6, "Central", "Y", 2, 2, LocalTime.of(11, 12), 100));
        stationMap.put(7, new Station(7, "Trichy", "C", 3, 2, LocalTime.of(13, 16), 150));
        stationMap.put(8, new Station(8, "Coimbatore", "D", 4, 2, LocalTime.of(15, 13), 200));
        stationMap.put(9, new Station(9, "Madurai", "Z", 5, 2, LocalTime.of(17, 20), 250));

        for (Map.Entry<Integer, Station> cur : stationMap.entrySet()) {
            codeStationMap.put(cur.getValue().getStationCode(), cur.getKey());
            stationTrainSequenceMap.
                    computeIfAbsent(cur.getValue().getStationCode(), k -> new ConcurrentHashMap<>())
                    .put(cur.getValue().getTrainId(), cur.getValue().getSequenceNo());
            trainStationMap.computeIfAbsent(cur.getValue().getTrainId(), k->new ArrayList<>()).add(cur.getValue().getStationCode());
        }
    }

    @Override
    public Map<Integer, List<String>> getTrainStationMap() {
        return new ConcurrentHashMap<>(trainStationMap);
    }

    @Override
    public Map<String, Map<Integer, Integer>> getStationTrainSequenceMap() {
        return new HashMap<>(this.stationTrainSequenceMap);
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

    @Override
    public Map<Integer, Integer> getTrainSequences(Station station) {
        if (station == null) return new ConcurrentHashMap<>();
        return stationTrainSequenceMap.get(station.getStationCode());
    }
}
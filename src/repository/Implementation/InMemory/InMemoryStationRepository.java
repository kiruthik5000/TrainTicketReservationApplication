package repository.Implementation.InMemory;

import model.Station;
import repository.StationRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStationRepository implements StationRepository {
    private final Map<Integer, Station> stationMap;
    private final Map<String, Integer> codeStationMap;
    private final Map<Integer, List<Integer>> trainStationMap;
    private final Map<Integer, Map<Integer, Integer>> stationTrainSequenceMap;

    public InMemoryStationRepository() {
        stationMap = new ConcurrentHashMap<>();
        codeStationMap = new ConcurrentHashMap<>();
        trainStationMap = new ConcurrentHashMap<>();
        stationTrainSequenceMap = new ConcurrentHashMap<>();

        stationMap.put(1, new Station(1, "Tambaram", "A", 1, 1));
        stationMap.put(2, new Station(2, "Central", "B", 2, 1));
        stationMap.put(3, new Station(3, "Trichy", "C", 3, 1));
        stationMap.put(4, new Station(4, "Coimbatore", "D", 4, 1));
        stationMap.put(5, new Station(5, "Tambaram", "A", 1, 2));
        stationMap.put(6, new Station(6, "Central", "B", 2, 2));
        stationMap.put(7, new Station(7, "Trichy", "C", 3, 2));
        stationMap.put(8, new Station(8, "Coimbatore", "D", 4, 2));
        stationMap.put(9, new Station(9, "Tabmaram", "F", 5, 2));

        for (Map.Entry<Integer, Station> cur : stationMap.entrySet()) {
            codeStationMap.put(cur.getValue().getStationCode(), cur.getKey());
            trainStationMap.computeIfAbsent(cur.getValue().getStationId(),k->new ArrayList<>()).add(cur.getValue().getTrainId());
            stationTrainSequenceMap.
                    computeIfAbsent(cur.getValue().getStationId(), k -> new ConcurrentHashMap<>())
                    .put(cur.getValue().getTrainId(), cur.getValue().getSequenceNo());
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

    @Override
    public List<Integer> findTrains(Station from, Station to) {
        List<Integer> result = new ArrayList<>();
        if (from == null || to == null) return result;

        List<Integer> fromTrains = trainStationMap.get(from.getStationId());
        List<Integer> toTrains = trainStationMap.get(to.getStationId());
        if (fromTrains == null || toTrains == null) return result;

        // Sequence numbers for each train at both stations
        Map<Integer, Integer> fromSeq = stationTrainSequenceMap.get(from.getStationId());
        Map<Integer, Integer> toSeq = stationTrainSequenceMap.get(to.getStationId());

        // Set gives O(1) lookups instead of nested loops
        Set<Integer> toTrainSet = new HashSet<>(toTrains);

        for (Integer trainId : fromTrains) {
            if (!toTrainSet.contains(trainId)) continue;      // train must stop at both

            Integer fromNo = fromSeq.get(trainId);
            Integer toNo = toSeq.get(trainId);

            if (fromNo != null && toNo != null && fromNo < toNo) {  // correct travel direction
                result.add(trainId);
            }
        }
        return result;
    }
}
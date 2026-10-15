package repository;

import model.Station;

import java.util.Map;

public interface StationRepository {
    int getSequenceNo(String stationCode);
    Station getStationByCode(String stationCode);
    Map<Integer, Integer> getTrainSequences(Station station);
}

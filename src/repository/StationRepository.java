package repository;

import model.Station;

import java.util.List;

public interface StationRepository {
    int getSequenceNo(String stationCode);
    Station getStationByCode(String stationCode);
    List<Integer> findTrains(Station from, Station to);
}

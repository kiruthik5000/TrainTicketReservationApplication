package repository;

import model.Station;

public interface StationRepository {
    int getSequenceNo(String stationCode);
    Station getStationByCode(String stationCode);
}

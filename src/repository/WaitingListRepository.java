package repository;

import exception.DataBaseException;
import model.Passenger;

import java.util.List;

public interface WaitingListRepository {

    void addAllRac(int trainId, List<Passenger> passengers);
    void addAllWl(int trainId, List<Passenger> passengers);

    int getAvailableRac(int trainId) throws DataBaseException;
    int getAvailableWl(int trainId) throws DataBaseException;

    void removeAllRacPassenger(List<Integer> pIds, int trainId);
    void removeAllWlPassenger(List<Integer> pIds, int trainId);

    List<Passenger> getRacPassengers(int trainId, int limit) throws DataBaseException;
    List<Passenger> getWlPassengers(int trainId, int limit) throws DataBaseException;

    int getRacNo(int trainId, int pId) throws DataBaseException;
    int getWlNo(int trainId, int pId) throws DataBaseException;
}

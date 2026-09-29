package repository;

import exception.DataBaseException;

public interface WaitingListRepository {
    int getRACAvailabilityNo(int trainId) throws DataBaseException;
    int getWLNAvailability(int trainId) throws DataBaseException;
    void addRac(int trainId, int passengerId);
    void addWl(int trainId, int passengerId);
    int getAvailableRac(int trainId) throws DataBaseException;
    int getAvailableWl(int trainId) throws DataBaseException;
    void removeRacPassenger(int pId, int trainId);
    void removeWlPassenger(int pId, int trainId);
    int getFirstRacPassenger(int trainId) throws DataBaseException;
    int getFirstWlPassenger(int trainId) throws DataBaseException;
    int getRacNo(int trainId, int pId) throws DataBaseException;
    int getWlNo(int trainId, int pId) throws DataBaseException;
}

package repository;

public interface WaitingListRepository {
    int getRACAvailabilityNo(int trainId);
    int getWLNAvailability(int trainId);
    void addRac(int trainId, int passengerId);
    void addWl(int trainId, int passengerId);
    int getAvailableRac(int trainId);
    int getAvailableWl(int trainId);
    void removeRacPassenger(int pId, int trainId);
    void removeWlPassenger(int pId, int trainId);
    int getFirstRacPassenger(int trainId);
    int getFirstWlPassenger(int trainId);
    int getRacNo(int trainId, int pId);
    int getWlNo(int trainId, int pId);
}

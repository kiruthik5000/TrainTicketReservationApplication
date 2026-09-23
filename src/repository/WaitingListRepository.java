package repository;

public interface WaitingListRepository {
    int getRACNo(int trainId);
    int getWLNo(int trainId);
    void addRac(int trainId, int passengerId);
    void addWl(int trainId, int passengerId);
    int getAvailableRac(int trainId);
    int getAvailableWl(int trainId);
}

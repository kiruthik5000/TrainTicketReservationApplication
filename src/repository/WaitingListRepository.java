package repository;

public interface WaitingListRepository {
    int getRACNo(int trainId);
    int getWLNo(int trainId);
}

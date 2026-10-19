package repository;

import exception.DataBaseException;
import model.Passenger;
import model.SeatType;

import java.util.List;

public interface WaitingListRepository {

    void addAllRac(int trainId, SeatType seatType, List<Passenger> passengers);
    void addAllWl(int trainId,SeatType seatType, List<Passenger> passengers);

    int getAvailableRac(int trainId, SeatType seatType) throws DataBaseException;
    int getAvailableWl(int trainId, SeatType seatType) throws DataBaseException;

    void removeAllRacPassenger(List<Integer> pIds, int trainId, SeatType seatType);
    void removeAllWlPassenger(List<Integer> pIds, int trainId, SeatType seatType);

    List<Passenger> getRacPassengers(int trainId, SeatType seatType, int limit) throws DataBaseException;
    List<Passenger> getWlPassengers(int trainId,  SeatType seatType, int limit) throws DataBaseException;

    int getRacNo(int trainId, SeatType seatType, int pId) throws DataBaseException;
    int getWlNo(int trainId, SeatType seatType, int pId) throws DataBaseException;

    int getNumberOfRac(int trainId, SeatType seatType);
    int getNumberOfWl(int trainId, SeatType seatType);
}

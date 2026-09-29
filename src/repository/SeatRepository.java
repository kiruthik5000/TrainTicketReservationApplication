package repository;

import exception.DataBaseException;
import model.Seat;
import model.SeatStatus;

import java.util.List;

public interface SeatRepository {
    List<Seat> getAvailableSeats(int trainId) throws DataBaseException;
    void updateStatus(int seatId, SeatStatus status) throws DataBaseException;
    Seat getSeatById(int seatId) throws DataBaseException;
    void updateAllSeatStatus(List<Integer> seatIds, SeatStatus status);
}

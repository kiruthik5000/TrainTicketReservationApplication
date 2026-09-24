package repository;

import model.Seat;
import model.SeatStatus;

import java.util.List;

public interface SeatRepository {
    List<Seat> getAvailableSeats(int trainId);
    void updateStatus(int seatId, SeatStatus status);
    Seat getSeatById(int seatId);
}

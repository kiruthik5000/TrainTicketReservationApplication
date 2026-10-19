package repository;

import dto.SeatUpdateRequestDto;
import exception.DataBaseException;
import model.Seat;
import model.SeatType;
import model.SeatStatus;

import java.util.List;

public interface SeatRepository {
    List<Seat> getAvailableSeats(int trainId, SeatType sc, int from, int to) throws DataBaseException;
    Seat getSeatById(int seatId) throws DataBaseException;
    void updateAllSeatStatus(List<SeatUpdateRequestDto> updateRequestDtos, SeatStatus status);
}

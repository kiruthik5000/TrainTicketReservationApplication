package repository;

import exception.DataBaseException;
import model.Passenger;
import model.PassengerStatus;

import java.sql.SQLException;
import java.util.List;

public interface PassengerRepository {
    List<Passenger> getPassengerByBookingPnr(String pnr) throws DataBaseException;

    Passenger addPassenger(Passenger passenger) throws DataBaseException;
    void updatePassengerStatusAndSeatNo(int id, PassengerStatus status, int seatNo) throws DataBaseException;
}

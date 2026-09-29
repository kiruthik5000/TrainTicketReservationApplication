package repository;

import dto.PassengerSeatIdDto;
import exception.DataBaseException;
import model.Passenger;
import model.PassengerStatus;

import java.util.List;

public interface PassengerRepository {
    List<Passenger> getPassengerByBookingPnr(String pnr) throws DataBaseException;
    Passenger addPassenger(Passenger passenger) throws DataBaseException;
    void updatePassengerStatusAndSeatNo(int id, PassengerStatus status, int seatNo) throws DataBaseException;
    void addAllPassenger(List<Passenger> passengers) throws DataBaseException;
    void updateAllPassengerStatusAndSeatNo(List<PassengerSeatIdDto> passengerSeatIdDtos, PassengerStatus status);
}

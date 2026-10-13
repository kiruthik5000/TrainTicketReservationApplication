package repository.Implementation.DataBase;

import dto.PassengerSeatIdDto;
import exception.DataBaseException;
import model.Passenger;
import model.PassengerStatus;
import repository.PassengerRepository;
import utils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbPassengerRepository implements PassengerRepository {
    @Override
    public List<Passenger> getPassengerByBookingPnr(String pnr) throws DataBaseException {
        String query = "SELECT * FROM passenger WHERE pnr = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, pnr);
            ResultSet rs = preparedStatement.executeQuery();
            List<Passenger> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new Passenger(
                        rs.getInt("passengerId"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        PassengerStatus.valueOf(rs.getString("status")),
                        rs.getString("pnr"),
                        rs.getInt("seatId")));
            }
            return result;

        } catch (Exception e) {
            throw new DataBaseException("DB error Occurs in Passenger Table");
        }
    }

    private Passenger getPassengerById(int pId) throws DataBaseException {
        String query = "SELECT * FROM passenger WHERE passengerId = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, pId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return new Passenger(
                        rs.getInt("passengerId"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        PassengerStatus.valueOf(rs.getString("status")),
                        rs.getString("pnr"),
                        rs.getInt("seatId")
                );
            }
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
        return null;
    }

    @Override
    public void addAllPassenger(List<Passenger> passengers) throws DataBaseException {
        String query = "INSERT INTO passenger(name, age, status, pnr, seatId) values (?,?,?,?,?);";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
                ) {
            for (Passenger p : passengers) {

                Integer seatId = p.getSeatId() != -1 ? p.getSeatId() : null;

                preparedStatement.setObject(1, p.getName());
                preparedStatement.setObject(2, p.getAge());
                preparedStatement.setObject(3, p.getStatus().name());
                preparedStatement.setObject(4, p.getPnr());
                preparedStatement.setObject(5, seatId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void updateAllPassengerStatusAndSeatNo(List<PassengerSeatIdDto> passengerSeatIdDtos, PassengerStatus status) {
        String query = "UPDATE passenger SET seatId = ?, status = ? WHERE passengerId = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
            for(PassengerSeatIdDto dto : passengerSeatIdDtos) {
                Integer seatId = dto.getSeatId() == -1 ? null : dto.getSeatId();

                preparedStatement.setObject(1, seatId);
                preparedStatement.setObject(2, status.name());
                preparedStatement.setObject(3, dto.getPassengerId());

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}

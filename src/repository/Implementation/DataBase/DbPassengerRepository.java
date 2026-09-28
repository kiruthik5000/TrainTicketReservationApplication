package repository.Implementation.DataBase;

import exception.DataBaseException;
import model.Passenger;
import model.PassengerStatus;
import repository.PassengerRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbPassengerRepository implements PassengerRepository {
    @Override
    public List<Passenger> getPassengerByBookingPnr(String pnr) throws DataBaseException {
        String query = "SELECT * FROM passenger WHERE pnr = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
                ) {

            preparedStatement.setString(1, pnr);

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

    @Override
    public Passenger addPassenger(Passenger passenger) throws DataBaseException {
        String query = "INSERT INTO passenger(name, age, status, pnr, seatId) values (?,?,?,?,?);";

        try (
             Connection connection = DbUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
                ) {

            preparedStatement.setString(1, passenger.getName());
            preparedStatement.setInt(2, passenger.getAge());
            preparedStatement.setString(3, passenger.getStatus().name());
            preparedStatement.setString(4, passenger.getPnr());
            preparedStatement.setInt(5, passenger.getSeatId());

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int pId = rs.getInt(1);
                    return getPassengerById(pId);
                }
            }
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
        return null;
    }

    private Passenger getPassengerById(int pId) throws DataBaseException {
        String query = "SELECT * FROM passenger WHERE passengerId = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, pId);
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
    public void updatePassengerStatusAndSeatNo(int id, PassengerStatus status, int seatNo) throws DataBaseException {
        String query = "UPDATE passenger SET status = ?, seatId = ? WHERE passengerId = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
                preparedStatement.setString(1, status.name());
                preparedStatement.setInt(2, seatNo);
                preparedStatement.setInt(3, id);

                preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}

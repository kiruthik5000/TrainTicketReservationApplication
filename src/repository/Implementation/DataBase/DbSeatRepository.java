package repository.Implementation.DataBase;

import dto.SeatUpdateRequestDto;
import exception.DataBaseException;
import model.Seat;
import model.SeatType;
import model.SeatStatus;
import repository.SeatRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class DbSeatRepository implements SeatRepository {
    @Override
    public List<Seat> getAvailableSeats(int trainId, SeatType sc, int from, int to) throws DataBaseException {
        String query = "SELECT * FROM seat WHERE trainId = ? AND status = 'AVAILABLE';";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();
            List<Seat> seats = new ArrayList<>();
            while (rs.next()) {
                seats.add(
                        new Seat(
                                rs.getInt("seatId"),
                                rs.getInt("seatNo"),
                                rs.getInt("trainId"),
                                SeatStatus.valueOf(rs.getString("status")),
                                SeatType.SLEEPER,
                                new BitSet(3)
                        )
                );
            }
            return seats;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
    @Override
    public Seat getSeatById(int seatId) throws DataBaseException {
        String query = "SELECT * FROM seat WHERE seatId = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, seatId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Seat(
                        rs.getInt("seatId"),
                        rs.getInt("seatNo"),
                        rs.getInt("trainId"),
                        SeatStatus.valueOf(rs.getString("status")),
                        SeatType.SLEEPER,
                        new BitSet(3)
                );
            }
            return null;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void updateAllSeatStatus(List<SeatUpdateRequestDto> seatIds, SeatStatus status) {
        String query = "UPDATE seat SET status = ? WHERE seatId = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            for (SeatUpdateRequestDto seat : seatIds) {
                preparedStatement.setObject(1, status.name());
                preparedStatement.setObject(2, seat.getSeatId());

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

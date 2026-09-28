package repository.Implementation.DataBase;

import exception.DataBaseException;
import model.Booking;
import model.BookingStatus;
import repository.BookingRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DbBookingRepository implements BookingRepository {
    @Override
    public List<Booking> getBookingByUser(int userId) throws DataBaseException {
        String query = "SELECT * FROM booking WHERE userId = ?;";
        return getBookingList(userId, query);
    }

    @Override
    public void addBooking(Booking booking) throws DataBaseException {
        String query = "INSERT INTO booking(pnr, fromStation, toStation, status, trainId, userId) VALUES (?,?,?,?,?,?);";

        DbUtils.executeInsertQuery(query, List.of(
                booking.getPnr(),
                booking.getFrom(),
                booking.getTo(),
                booking.getStatus().name(),
                booking.getTrainId(),
                booking.getUserId()
        ));
    }

    @Override
    public Booking getBookingByPnr(String pnr) throws DataBaseException {
        String query = "SELECT * FROM booking WHERE pnr = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
            preparedStatement.setObject(1, pnr);
        ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("bookingId"),
                        rs.getString("pnr"),
                        rs.getString("fromStation"),
                        rs.getString("toStation"),
                        rs.getInt("trainId"),
                        rs.getInt("userId"),
                        BookingStatus.valueOf(rs.getString("status")
                        ));
            }
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
        return null;
    }

    @Override
    public void updateStatus(int bookingId, BookingStatus status) throws DataBaseException {
        String query = "UPDATE booking SET status = ? WHERE bookingId = ?;";
       DbUtils.executeUpdateQuery(query, List.of(
               status.name(),
               bookingId
       ));
    }

    @Override
    public List<Booking> getAllBookingsByTrainId(int trainId) throws DataBaseException {
        String query = "SELECT * FROM booking WHERE trainId = ?;";
        return getBookingList(trainId, query);
    }

    private List<Booking> getBookingList(int trainId, String query) throws DataBaseException {
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();

            List<Booking> bookingList = new ArrayList<>();
            while (rs.next()) {
                bookingList.add(new Booking(
                        rs.getInt("bookingId"),
                        rs.getString("pnr"),
                        rs.getString("fromStation"),
                        rs.getString("toStation"),
                        rs.getInt("trainId"),
                        rs.getInt("userId"),
                        BookingStatus.valueOf(rs.getString("status"))
                ));
            }
            return bookingList;

        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getActiveBookings(int userId) throws DataBaseException {
        String query = "SELECT * FROM booking WHERE status = 'ACTIVE' AND userId = ?;";
        return getBookingList(userId, query);
    }

    @Override
    public int getTotalNoRows() throws DataBaseException {
        String query = "SELECT COUNT(*) as cnt FROM booking;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return -1;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}

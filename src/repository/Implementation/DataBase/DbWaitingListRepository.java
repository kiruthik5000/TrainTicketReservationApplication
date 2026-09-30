package repository.Implementation.DataBase;

import exception.DataBaseException;
import model.Passenger;
import model.PassengerStatus;
import repository.WaitingListRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class DbWaitingListRepository implements WaitingListRepository {
    public int racLimit = 2;
    public int wlLimit = 2;


    public int getRACAvailabilityNo(int trainId) throws DataBaseException {
        String query = "SELECT count(*) as cnt\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'RAC' and b.trainId = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return -1;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public int getWLNAvailabilityNo(int trainId) throws DataBaseException {
        String query = "SELECT count(*) as cnt\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'WL' and b.trainId = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return -1;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void addAllRac(int trainId, List<Passenger> passengers) {

    }

    @Override
    public void addAllWl(int trainId, List<Passenger> passengers) {

    }

    @Override
    public int getAvailableRac(int trainId) throws DataBaseException {
        int curRac = getRACAvailabilityNo(trainId);
//        System.out.println("Available RAC"+curRac);
        return Math.max(0, racLimit - curRac);
    }

    @Override
    public int getAvailableWl(int trainId) throws DataBaseException {
        int curRac = getWLNAvailabilityNo(trainId);
//        System.out.println("Available WL"+curRac);
        return Math.max(0, wlLimit - curRac);
    }

    @Override
    public void removeAllRacPassenger(List<Integer> pId, int trainId) {

    }

    @Override
    public void removeAllWlPassenger(List<Integer> pId, int trainId) {

    }

    @Override
    public List<Passenger> getRacPassengers(int trainId, int limit) throws DataBaseException {
        String query = "SELECT a.passengerId, a.name, a.age, a.status, a.pnr, a.seatId " +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'RAC' and b.trainId = ?\n" +
                "ORDER BY a.passengerId "+
                "LIMIT ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            preparedStatement.setObject(2, limit);
            ResultSet rs = preparedStatement.executeQuery();
            List<Passenger> passengers = new ArrayList<>();
            while (rs.next()) {
                passengers.add(new Passenger(
                        rs.getInt("passengerId"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        PassengerStatus.valueOf(rs.getString("status")),
                        rs.getString("pnr"),
                        rs.getInt("seatId")
                ));
            }
            return passengers;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Passenger> getWlPassengers(int trainId, int limit) throws DataBaseException {
        String query = "SELECT a.passengerId, a.name, a.age, a.status, a.pnr, a.seatId\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'WL' and b.trainId = ?\n" +
                "ORDER BY a.passengerId "+
                "LIMIT ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            preparedStatement.setObject(2, limit);
            ResultSet rs = preparedStatement.executeQuery();
            List<Passenger> passengers = new ArrayList<>();
            while (rs.next()) {
                passengers.add(new Passenger(
                        rs.getInt("passengerId"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        PassengerStatus.valueOf(rs.getString("status")),
                        rs.getString("pnr"),
                        rs.getInt("seatId")
                ));
            }
            return passengers;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public int getRacNo(int trainId, int pId) throws DataBaseException {
        String query = "select roll from (\n" +
                "SELECT a.passengerId as id, row_number() over(ORDER BY a.passengerId) as roll\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'RAC' and b.trainId = ?) as temp\n" +
                "where id = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            preparedStatement.setObject(2, pId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int curCnt = rs.getInt("roll");
                if (curCnt == 0) return -1;
                return curCnt;
            }
            return -1;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public int getWlNo(int trainId, int pId) throws DataBaseException {
        String query = "select roll from (\n" +
                "SELECT a.passengerId as id, row_number() over(ORDER BY a.passengerId) as roll\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'WL' and b.trainId = ?) as temp\n" +
                "where id = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            preparedStatement.setObject(2, pId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int curCnt = rs.getInt("roll");
                if (curCnt == 0) return -1;
                return curCnt;
            }
            return -1;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}

package repository.Implementation.DataBase;

import exception.DataBaseException;
import repository.WaitingListRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DbWaitingListRepository implements WaitingListRepository {
    public int racLimit = 2;
    public int wlLimit = 2;
    @Override
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

    @Override
    public int getWLNAvailability(int trainId) throws DataBaseException {
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
    public void addRac(int trainId, int passengerId) {

    }

    @Override
    public void addWl(int trainId, int passengerId) {

    }

    @Override
    public int getAvailableRac(int trainId) throws DataBaseException {
        int curRac = getRACAvailabilityNo(trainId);
//        System.out.println("Available RAC"+curRac);
        return Math.max(0, racLimit - curRac);
    }

    @Override
    public int getAvailableWl(int trainId) throws DataBaseException {
        int curRac = getWLNAvailability(trainId);
//        System.out.println("Available WL"+curRac);
        return Math.max(0, wlLimit - curRac);
    }

    @Override
    public void removeRacPassenger(int pId, int trainId) {

    }

    @Override
    public void removeWlPassenger(int pId, int trainId) {

    }

    @Override
    public int getFirstRacPassenger(int trainId) throws DataBaseException {
        String query = "SELECT a.passengerId as pId, row_number() over() as roll\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'RAC' and b.trainId = ?\n" +
                "ORDER BY a.passengerId "+
                "LIMIT 1;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int curCnt = rs.getInt("pId");
                if (curCnt == 0) return -1;
                return curCnt;
            }
            return -1;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public int getFirstWlPassenger(int trainId) throws DataBaseException {
        String query = "SELECT a.passengerId as pId, row_number() over() as roll\n" +
                "from passenger a\n" +
                "join booking b\n" +
                "on a.pnr = b.pnr\n" +
                "WHERE a.status = 'WL' and b.trainId = ?\n" +
                "ORDER BY a.passengerId "+
                "LIMIT 1;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int curCnt = rs.getInt("pId");
                if (curCnt == 0) return -1;
                return curCnt;
            }
            return -1;
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

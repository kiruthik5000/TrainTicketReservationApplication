package repository.Implementation.DataBase;

import exception.DataBaseException;
import model.Train;
import model.TrainType;
import repository.TrainRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DbTrainRepository implements TrainRepository {
    @Override
    public List<Train> getAllTrains() throws DataBaseException {
        String query = "SELECT * FROM train;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            ResultSet rs = preparedStatement.executeQuery();
            List<Train> trains = new ArrayList<>();
            while (rs.next()) {
                trains.add(
                        new Train(
                                rs.getInt("trainId"),
                                rs.getInt("trainNo"),
                                rs.getString("trainName"),
                                TrainType.valueOf(rs.getString("trainType"))
                        )
                );
            }
            return trains;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public Train getTrainById(int trainId) throws DataBaseException {
        String query = "SELECT * FROM train WHERE trainId = ?;";

        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, trainId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return  new Train(
                        rs.getInt("trainId"),
                        rs.getInt("trainNo"),
                        rs.getString("trainName"),
                        TrainType.valueOf(rs.getString("trainType"))
                );
            }
            return null;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}

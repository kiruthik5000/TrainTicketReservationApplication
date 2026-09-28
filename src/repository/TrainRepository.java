package repository;

import exception.DataBaseException;
import model.Train;

import java.sql.SQLException;
import java.util.List;

public interface TrainRepository {
    List<Train> getAllTrains() throws DataBaseException;
    Train getTrainById(int trainId) throws DataBaseException;
}

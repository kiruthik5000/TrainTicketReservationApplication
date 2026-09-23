package repository;

import model.Train;

import java.util.List;

public interface TrainRepository {
    List<Train> getAllTrains();
    Train getTrainById(int trainId);
}

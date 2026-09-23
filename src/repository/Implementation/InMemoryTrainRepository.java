package repository.Implementation;

import model.Train;
import model.TrainType;
import repository.TrainRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTrainRepository implements TrainRepository {
    private final Map<Integer, Train> trainMap;

    public InMemoryTrainRepository() {
        trainMap = new HashMap<>();
        trainMap.put(1, new Train(1, 12345, "Chemmozi EXP", TrainType.EXPRESS));
        trainMap.put(2, new Train(2, 54321, "Antyodaya EXP", TrainType.SUPERFAST_EXPRESS));
    }

    @Override
    public List<Train> getAllTrains() {
        return trainMap.values()
                .stream()
                .toList();
    }

    @Override
    public Train getTrainById(int trainId) {
        return trainMap.get(trainId);
    }
}

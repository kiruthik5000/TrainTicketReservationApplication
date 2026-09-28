package model;

public class Train {
    private int trainId;
    private int trainNo;
    private String trainName;
    private TrainType trainType;

    public Train(int trainId, int trainNo, String trainName, TrainType trainType) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.trainName = trainName;
        this.trainType = trainType;
    }

    public int getTrainId() {return trainId;}
    @Override
    public String toString() {
        return "Train "+trainName+" ( "+trainNo+" ) : "+trainType.name();
    }
}

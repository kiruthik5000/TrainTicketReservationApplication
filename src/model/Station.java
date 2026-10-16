package model;

import java.time.LocalTime;

public class Station {
    private int stationId;
    private String stationName;
    private String stationCode;
    private int sequenceNo;
    private int trainId;
    private LocalTime arrivalTime;

    public LocalTime getArrivalTime(){return arrivalTime;}
    public int getTrainId() {return trainId;}
    public String getStationCode() {
        return stationCode;
    }
    public int getSequenceNo() {return sequenceNo;}
    private String getStationName() {return stationName;}
    public Station(int stationId, String stationName, String stationCode, int sequenceNo, int trainId, LocalTime arrivalTime) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.sequenceNo = sequenceNo;
        this.trainId = trainId;
        this.arrivalTime = arrivalTime;
    }
}

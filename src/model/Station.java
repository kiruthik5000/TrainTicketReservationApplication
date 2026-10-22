package model;

import java.time.LocalTime;

public class Station {
    private int stationId;
    private String stationName;
    private String stationCode;
    private int sequenceNo;
    private int trainId;
    private LocalTime arrivalTime;
    private int distance;

    public LocalTime getArrivalTime(){return arrivalTime;}
    public int getTrainId() {return trainId;}
    public String getStationCode() {
        return stationCode;
    }
    public int getSequenceNo() {return sequenceNo;}
    public int getDistance() {return distance;}
    public int getStationId() {return stationId;}
    public Station(int stationId, String stationName, String stationCode, int sequenceNo, int trainId, LocalTime arrivalTime, int distance) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.sequenceNo = sequenceNo;
        this.trainId = trainId;
        this.arrivalTime = arrivalTime;
        this.distance = distance;
    }
}

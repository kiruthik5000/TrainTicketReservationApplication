package model;

public class Station {
    private int stationId;
    private String stationName;
    private String stationCode;
    private int sequenceNo;

    public String getStationCode() {
        return stationCode;
    }

    public int getSequenceNo() {return sequenceNo;}
    private String getStationName() {return stationName;}
    public Station(int stationId, String stationName, String stationCode, int sequenceNo) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.sequenceNo = sequenceNo;
    }
}

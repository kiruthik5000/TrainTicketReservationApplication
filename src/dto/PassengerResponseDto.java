package dto;

import model.PassengerStatus;
import model.SeatStatus;

public class PassengerResponseDto {
    private int pId;
    private String name;
    private int age;
    private PassengerStatus status;
    private String from;
    private String to;
    private int seatNo;

    public PassengerResponseDto(int pId, String name, int age, PassengerStatus status, int seatNo, String from, String to) {
        this.pId = pId;
        this.name = name;
        this.age = age;
        this.status = status;
        this.seatNo = seatNo;
        this.from = from;
        this.to = to;
    }
    public int getpId() {
        return pId;
    }
    @Override
    public String toString() {
        return "name: "+name+", age:"+age+"\t"+status.name()+" "+(seatNo != -1 ? seatNo : "null")+" "+from+" - "+to;
    }
}

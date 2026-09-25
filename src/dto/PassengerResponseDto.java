package dto;

import model.PassengerStatus;
import model.SeatStatus;

public class PassengerResponseDto {
    private int pId;
    private String name;
    private int age;
    private PassengerStatus status;
    private int seatNo;

    public PassengerResponseDto(int pId, String name, int age, PassengerStatus status, int seatNo) {
        this.pId = pId;
        this.name = name;
        this.age = age;
        this.status = status;
        this.seatNo = seatNo;
    }
    public int getpId() {
        return pId;
    }
    @Override
    public String toString() {
        return "name: "+name+", age:"+age+"\t"+status.name()+" "+(seatNo != -1 ? seatNo : "null");
    }
}

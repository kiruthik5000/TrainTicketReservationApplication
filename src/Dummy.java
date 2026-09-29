import model.SeatStatus;
import repository.Implementation.DataBase.DbSeatRepository;
import repository.SeatRepository;

public class Dummy {
    public static void main(String[] args) {
        SeatRepository sr = new DbSeatRepository();
//        sr.updateAllSeatStatus(new String[]{"2", "3"}, SeatStatus.BOOKED);
    }
}

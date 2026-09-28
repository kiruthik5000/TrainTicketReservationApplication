import repository.*;
import repository.Implementation.DataBase.*;
import repository.Implementation.InMemory.*;
import service.AdminService;
import service.BookingService;
import service.CancellationService;
import service.UserService;
import ui.*;

public class Main {
    public static void main(String[] args) {

        UserRepository userRepository = new DbUserRepository();
        BookingRepository bookingRepository = new DbBookingRepository();
        PassengerRepository passengerRepository = new DbPassengerRepository();
        SeatRepository seatRepository = new DbSeatRepository();
        TrainRepository trainRepository = new DbTrainRepository();
        WaitingListRepository waitingListRepository = new InMemoryWaitingListRepository();

        UserService userService = new UserService(userRepository);
        BookingService bookingService = new BookingService(trainRepository, seatRepository, bookingRepository, passengerRepository, waitingListRepository);
        CancellationService cancellationService = new CancellationService(bookingRepository, seatRepository, waitingListRepository, passengerRepository);
        AdminService adminService = new AdminService(trainRepository, bookingRepository,passengerRepository,seatRepository, waitingListRepository);

        UserUi userUi = new UserUi(userService);
        BookingUi bookingUi = new BookingUi(bookingService);
        CancellationUi cancellationUi = new CancellationUi(cancellationService, bookingService);
        AdminUI adminUI = new AdminUI(adminService);

        MainMenuUi mainMenuUI = new MainMenuUi(userUi, bookingUi, cancellationUi, adminUI);
        mainMenuUI.start();
    }
}
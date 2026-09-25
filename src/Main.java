import repository.*;
import repository.Implementation.*;
import service.AdminService;
import service.BookingService;
import service.CancellationService;
import service.UserService;
import ui.*;

public class Main {
    public static void main(String[] args) throws Exception {

        UserRepository userRepository = new InMemoryUserRepository();
        BookingRepository bookingRepository = new InMemoryBookingRepository();
        PassengerRepository passengerRepository = new InMemoryPassengerRepository();
        SeatRepository seatRepository = new InMemorySeatRepository();
        TrainRepository trainRepository = new InMemoryTrainRepository();
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
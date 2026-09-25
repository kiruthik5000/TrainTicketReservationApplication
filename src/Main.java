import repository.*;
import repository.Implementation.*;
import service.BookingService;
import service.CancellationService;
import service.UserService;
import ui.BookingUi;
import ui.CancellationUi;
import ui.MainMenuUi;
import ui.UserUi;

import java.util.Random;

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

        UserUi userUi = new UserUi(userService);
        BookingUi bookingUi = new BookingUi(bookingService);
        CancellationUi cancellationUi = new CancellationUi(cancellationService, bookingService);

        MainMenuUi mainMenuUI = new MainMenuUi(userUi, bookingUi, cancellationUi);
        mainMenuUI.start();
    }
}
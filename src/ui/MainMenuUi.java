package ui;

import utils.InputHandler;
import utils.SessionStorage;

public class MainMenuUi {
    private final UserUi userUi;
    private final BookingUi bookingUi;
    private final CancellationUi cancellationUi;
    private final AdminUI adminUI;
    public MainMenuUi(UserUi userUi, BookingUi bookingUi, CancellationUi cancellationUi, AdminUI adminUI) {
        this.userUi = userUi;
        this.bookingUi = bookingUi;
        this.cancellationUi = cancellationUi;
        this.adminUI = adminUI;
    }

    public void start() throws Exception {
        while (true) {
            System.out.println("---- Welcome To Train Ticket Reservation System ----\n");
            if (SessionStorage.getCurrentUser() == null) {
                System.out.println("1. Login");
                System.out.println("2. Register");
            }
            System.out.println("3. Book Tickets");
            System.out.println("4. Cancel Tickets");
            System.out.println("5. Show Bookings");
            System.out.println("6. Show All Passengers in Train");
            if (SessionStorage.getCurrentUser() != null) {
                System.out.println("7. Logout");
            } else {
                System.out.println("7. Exit");
            }

            int choice = InputHandler.getNumericValue("choice", 7);

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    bookTickets();
                    break;
                case 4:
                    cancelTickets();
                    break;
                case 5:
                    showBookings();
                    break;
                case 6:
                    showAllPassengersInTrain();
                    break;
                case 7:
                    if (exit()) break;
                    return;
                default:
                    System.out.println("Invalid Option Entered");
                    break;
            }
        }
    }

    private void login() {
        try {
            userUi.login();
        } catch (Exception e) {
            System.out.println("Error Occurred in login "+e.getClass().getSimpleName()+e.getMessage());
        }
    }

    private void register() {
        try {
            userUi.register();
        } catch (Exception e) {
            System.out.println("Error Occurred in register "+e.getClass().getSimpleName()+e.getMessage());
        }
    }

    private void bookTickets() {
        try {
            bookingUi.bookTickets();
        } catch (Exception e) {
            System.out.println("Error Occurred in booking "+e.getClass().getSimpleName()+e.getMessage());
        }
    }

    private void cancelTickets() {
        try {
            cancellationUi.cancelTickets();
        } catch (Exception e) {
            System.out.println("Error Occurred in cancellation "+e.getClass().getSimpleName()+e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean exit() {
        try {
            if (SessionStorage.getCurrentUser() != null) {
                userUi.logout();
                return true;
            } else {
                System.out.println("Thank You !");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error Occurred in Logout "+e.getClass().getSimpleName()+e.getMessage());
        }
        return true;
    }

    private void showBookings() {
        try {
            bookingUi.showBookings();
        } catch (Exception e) {
            System.out.println("Error Occurred in Showing Booking "+e.getClass().getSimpleName()+e.getMessage());
        }
    }

    private void showAllPassengersInTrain() {
        try {
            adminUI.showAllPassengersInTrain();
        } catch (Exception e) {
            System.out.println("Error Occurred in Showing All passengers"+e.getClass().getSimpleName()+e.getMessage());
        }
    }
}

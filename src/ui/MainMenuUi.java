package ui;

import utils.InputHandler;
import utils.SessionStorage;

public class MainMenuUi {
    private final UserUi userUi;
    private final BookingUi bookingUi;

    public MainMenuUi(UserUi userUi, BookingUi bookingUi) {
        this.userUi = userUi;
        this.bookingUi = bookingUi;
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
            if (SessionStorage.getCurrentUser() != null) {
                System.out.println("6. Logout");
            } else {
                System.out.println("6. Exit");
            }

            int choice = InputHandler.getNumericValue("choice", 6);

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
                case 5:
                    showBookings();
                    break;
                case 6:
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
            System.out.println("Error Occurred in login "+e.getMessage());
        }
    }

    private void register() {
        try {
            userUi.register();
        } catch (Exception e) {
            System.out.println("Error Occurred in register "+e.getMessage());
        }
    }

    private void bookTickets() {
        try {
            bookingUi.bookTickets();
        } catch (Exception e) {
            System.out.println("Error Occurred in booking "+e.getMessage());
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
            System.out.println("Error Occurred in Logout "+e.getMessage());
        }
        return true;
    }

    private void showBookings() {
        try {
            bookingUi.showBookings();
        } catch (Exception e) {
            System.out.println("Error Occurred in Showing Booking "+e.getMessage());
        }
    }
}

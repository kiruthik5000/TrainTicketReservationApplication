package ui;

import utils.InputHandler;

public class MainMenuUi {
    private final UserUi userUi;

    public MainMenuUi(UserUi userUi) {
        this.userUi = userUi;
    }

    public void start() throws Exception {
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Book Tickets");
            System.out.println("4. Cancel Tickets");
            System.out.println("5. Show Bookings");
            System.out.println("6. Exit");

            int choice = InputHandler.getNumericValue("choice");

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 5:
                    showBookings();
                    break;
                case 6:
                    System.out.println("Thank You!");
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

    private void showBookings() {

    }
}

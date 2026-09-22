package ui;

import utils.InputHandler;

public class MainMenuUI {
    private final UserUi userUi;

    public MainMenuUI(UserUi userUi) {
        this.userUi = userUi;
    }

    public void start() throws Exception {
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Book Tickets");
            System.out.println("4. Cancel Tickets");
            System.out.println("5. Show Bookings");

            int choice = InputHandler.getNumericValue("Enter your choice");

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    return;
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
}

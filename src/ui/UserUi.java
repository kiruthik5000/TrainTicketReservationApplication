package ui;

import service.UserService;
import utils.InputHandler;
import utils.SessionStorage;

public class UserUi {

    public final UserService userService;

    public UserUi(UserService userService) {
        this.userService = userService;
    }

    public void login() throws Exception{
        String email = InputHandler.getStringValue("Enter your email");
        String password = InputHandler.getStringValue("Enter your password");
        if (userService.login(email, password)) {
            System.out.println("Successfully LoggedIn!");
            System.out.println("---- Welcome "+ SessionStorage.getUserName()+" ----");
        }
    }
}

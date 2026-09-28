package ui;

import dto.UserRequestDto;
import exception.DataBaseException;
import service.UserService;
import utils.InputHandler;

public class UserUi {

    private final UserService userService;

    public UserUi(UserService userService) {
        this.userService = userService;
    }

    public void login() throws DataBaseException {
        String email = InputHandler.getStringValue("email");
        String password = InputHandler.getStringValue("password");
        if (userService.login(email, password)) {
            System.out.println("Successfully LoggedIn!");
        }
    }

    public void register() throws DataBaseException {
        String username = InputHandler.getStringValue("username");
        String email = InputHandler.getStringValue("email");
        String password = InputHandler.getStringValue("password");
        UserRequestDto dto = new UserRequestDto(username, email, password);
        userService.saveUser(dto);
        System.out.println("Successfully Registered!");
    }
    public void logout() {
        userService.logout();
    }
 }

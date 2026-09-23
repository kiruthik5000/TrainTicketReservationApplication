import repository.Implementation.InMemoryUserRepository;
import repository.UserRepository;
import service.UserService;
import ui.MainMenuUi;
import ui.UserUi;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {

        UserRepository userRepository = new InMemoryUserRepository();
        UserService userService = new UserService(userRepository);
        UserUi userUi = new UserUi(userService);
        MainMenuUi mainMenuUI = new MainMenuUi(userUi);
        mainMenuUI.start();
//        System.out.println(generatePNR());
    }
}
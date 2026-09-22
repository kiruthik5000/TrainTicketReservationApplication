import repository.Implementation.InMemoryUserRepository;
import repository.UserRepository;
import service.UserService;
import ui.MainMenuUI;
import ui.UserUi;

public class Main {
    public static void main(String[] args) throws Exception {

        UserRepository userRepository = new InMemoryUserRepository();
        UserService userService = new UserService(userRepository);
        UserUi userUi = new UserUi(userService);
        MainMenuUI mainMenuUI = new MainMenuUI(userUi);
        mainMenuUI.start();
    }
}
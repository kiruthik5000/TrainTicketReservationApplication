package service;

import dto.UserRequestDto;
import exception.IncorrectPasswordException;
import exception.InvalidInputException;
import exception.UserAlreadyExistException;
import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;
import utils.SessionStorage;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean saveUser(UserRequestDto dto) {
        if (!validateEmail(dto.getEmail()) || !validatePassword(dto.getPassword())) return false;
        User newUser = new User(0,
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword());
        return userRepository.save(newUser) != null;
    }

    public boolean login(String email, String password) {
        if (SessionStorage.getCurrentUser() != null) throw new InvalidInputException("User Already loggedIn");
        User user = userRepository.getUserByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found for this email");
        if (!user.getPassword().equals(password)) throw new IncorrectPasswordException("password Incorrect");
        return SessionStorage.storeUser(user);
    }

    private boolean validateEmail(String email) {
        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) throw new UserAlreadyExistException("User Already Exists with this email");
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) throw new InvalidInputException("Invalid Email Provided");
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) throw new InvalidInputException("Password Must be 6 letters long");
        return true;
    }

    public void logout() {
        SessionStorage.removeUser();
    }
}

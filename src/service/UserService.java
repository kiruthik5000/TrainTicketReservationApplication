package service;

import dto.UserRequestDto;
import exception.*;
import model.User;
import repository.UserRepository;
import utils.SessionStorage;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(UserRequestDto dto) {
        if (dto == null || dto.getEmail() == null || dto.getPassword() == null || dto.getUsername() == null) throw new InvalidInputException("Entered User data is Invalid");
        if (!validateEmail(dto.getEmail()) || !validatePassword(dto.getPassword())) return;
        User newUser = new User(0,
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword());
        userRepository.save(newUser);
    }

    public boolean login(String email, String password) {
        if (SessionStorage.getCurrentUser() != null) throw new InvalidInputException("User Already loggedIn");
        User user = userRepository.getUserByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found for this email");
        if (!user.getPassword().equals(password)) throw new IncorrectPasswordException("password Incorrect");
        return SessionStorage.storeUser(user);
    }

    private boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) throw new InvalidInputException("Email is Invalid");
        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) throw new UserAlreadyExistException("User Already Exists with this email");
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) throw new InvalidInputException("Invalid Email Provided");
        return true;
    }

    private boolean validatePassword(String password) {
        if (password ==  null || password.trim().isEmpty()) throw new InvalidInputException("Password is Invalid");
        if (password.length() < 6) throw new InvalidInputException("Password must contain at least 6 characters");
        return true;
    }

    public void logout() {
        SessionStorage.removeUser();
    }
}

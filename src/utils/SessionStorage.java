package utils;

import exception.UnAuthorizedAccessException;
import model.User;

public class SessionStorage {
    private static User currentUser;

    public static boolean storeUser(User user) {
        if (currentUser != null) return false;
        currentUser = user;
        return true;
    }

    public static void removeUser() {
        if (currentUser == null) return;
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean userIsLoggedIn() {
        User currentUser = getCurrentUser();
        if (currentUser == null) throw new UnAuthorizedAccessException("User must login to perform Operation");
        return true;
    }

    public static boolean isAdmin() {
        User currentUser = getCurrentUser();
        if (currentUser == null) throw new UnAuthorizedAccessException("User must login to perform Operation");
        return currentUser.getEmail().equals("admin@gmail.com");
    }
}

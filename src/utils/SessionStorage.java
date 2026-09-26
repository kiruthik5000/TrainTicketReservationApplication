package utils;

import exception.UnAuthorizedAccessException;
import model.User;

public class SessionStorage {
    private static User currentUser;

    public static String getUserName() {
        return currentUser.getUsername();
    }

    public static boolean storeUser(User user) {
        if (currentUser != null) return false;
        currentUser = user;
        return true;
    }

    public static boolean removeUser() {
        if (currentUser == null) return false;
        currentUser = null;
        return true;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean userIsLogin() {
        User currentUser = SessionStorage.getCurrentUser();
        if (currentUser == null) throw new UnAuthorizedAccessException("User must login to perform Operation");
        return true;
    }
}

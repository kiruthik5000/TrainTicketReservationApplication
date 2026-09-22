package utils;

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
}

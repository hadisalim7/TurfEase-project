package com.turfease.frontend;

public class LoggedInUser {
    private static int userId;
    private static String role;

    public static void setUser(int id, String userRole) {
        userId = id;
        role = userRole;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getRole() {
        return role;
    }

    public static void logout() {
        userId = 0;
        role = null;
    }
}

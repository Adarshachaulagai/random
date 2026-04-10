package com.example.grocery_management_system;
import com.example.grocery_management_system.model.User;
public class Session {
    private static User currentUser;
    public static User getCurrentUser()        { return currentUser; }
    public static void  setCurrentUser(User u) { currentUser = u; }
    public static void  clear()                { currentUser = null; }
}

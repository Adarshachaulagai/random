package com.example.grocery_management_system.model;

public class User {
    private int userID;
    private String username, password, role;

    public User() {}
    public User(int id, String u, String p, String r) { userID=id; username=u; password=p; role=r; }

    public int    getUserID()         { return userID; }
    public String getUsername()       { return username; }
    public String getPassword()       { return password; }
    public String getRole()           { return role; }
    public void setUserID(int v)      { userID = v; }
    public void setUsername(String v) { username = v; }
    public void setPassword(String v) { password = v; }
    public void setRole(String v)     { role = v; }
}

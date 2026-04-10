package com.example.grocery_management_system.model;

public class Sale {
    private int saleID;
    private double totalAmount;
    private String saleDate, username;

    public int    getSaleID()          { return saleID; }
    public double getTotalAmount()     { return totalAmount; }
    public String getSaleDate()        { return saleDate; }
    public String getUsername()        { return username; }

    public void setSaleID(int v)         { saleID = v; }
    public void setTotalAmount(double v) { totalAmount = v; }
    public void setSaleDate(String v)    { saleDate = v; }
    public void setUsername(String v)    { username = v; }
}

package com.example.grocery_management_system.model;

public class StockAlert {
    private int alertID, productID;
    private String alertMessage, alertDate, productName;
    private boolean resolved;

    public int     getAlertID()          { return alertID; }
    public int     getProductID()        { return productID; }
    public String  getAlertMessage()     { return alertMessage; }
    public String  getAlertDate()        { return alertDate; }
    public String  getProductName()      { return productName; }
    public boolean isResolved()          { return resolved; }

    public void setAlertID(int v)          { alertID = v; }
    public void setProductID(int v)        { productID = v; }
    public void setAlertMessage(String v)  { alertMessage = v; }
    public void setAlertDate(String v)     { alertDate = v; }
    public void setProductName(String v)   { productName = v; }
    public void setResolved(boolean v)     { resolved = v; }
}

package com.example.grocery_management_system.model;

public class Product {
    private int productID, quantity, minimumStock;
    private String name, category, createdAt, updatedAt;
    private double price;

    public int    getProductID()       { return productID; }
    public int    getQuantity()        { return quantity; }
    public int    getMinimumStock()    { return minimumStock; }
    public String getName()            { return name; } // getter
    public String getCategory()        { return category; }
    public double getPrice()           { return price; }
    public String getCreatedAt()       { return createdAt; }
    public String getUpdatedAt()       { return updatedAt; }

    public void setProductID(int v)    { productID = v; }
    public void setQuantity(int v)     { quantity = v; }
    public void setMinimumStock(int v) { minimumStock = v; }
    public void setName(String v)      { name = v; }
    public void setCategory(String v)  { category = v; }
    public void setPrice(double v)     { price = v; }
    public void setCreatedAt(String v) { createdAt = v; }
    public void setUpdatedAt(String v) { updatedAt = v; }

    public boolean isLowStock()        { return quantity <= minimumStock; }
    public void    updateStock(int q)  { quantity += q; }

    @Override
    public String toString() {
        return name + "  [Rs. " + String.format("%.0f", price) + "]  Stock: " + quantity;
    }
}
package com.example.grocery_management_system.model;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product p, int q) { product = p; quantity = q; }

    public Product getProduct()       { return product; }
    public int     getQuantity()      { return quantity; }
    public String  getProductName()   { return product.getName(); }
    public double  getUnitPrice()     { return product.getPrice(); }
    public double  getSubtotal()      { return product.getPrice() * quantity; }
    public void    setQuantity(int v) { quantity = v; }
}

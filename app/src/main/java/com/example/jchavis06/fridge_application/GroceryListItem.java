package com.example.jchavis06.fridge_application;

public class GroceryListItem {
    private String itemName;
    private int quantity;
    private String brand;
    private String quantityType;

    public GroceryListItem(String itemName, int quantity, String quantityType, String brand) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.quantityType = quantityType;
        this.brand = brand;
    }
    public String getItemName(){
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getQuantityType() {
        return quantityType;
    }

    public String getBrand() {
        return brand;
    }
}

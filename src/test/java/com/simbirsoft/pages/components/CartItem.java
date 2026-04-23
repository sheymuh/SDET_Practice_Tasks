package com.simbirsoft.pages.components;

import org.openqa.selenium.WebElement;

public class CartItem {
    private String name;
    private double unitPrice;
    private int quantity;
    private double totalPrice;
    private WebElement removeButton;
    private WebElement quantityInput;
    private WebElement rowElement;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public WebElement getRemoveButton() {
        return removeButton;
    }

    public void setRemoveButton(WebElement removeButton) {
        this.removeButton = removeButton;
    }

    public WebElement getQuantityInput() {
        return quantityInput;
    }

    public void setQuantityInput(WebElement quantityInput) {
        this.quantityInput = quantityInput;
    }

    public WebElement getRowElement() {
        return rowElement;
    }

    public void setRowElement(WebElement rowElement) {
        this.rowElement = rowElement;
    }
}

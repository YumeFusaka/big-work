package com.yumefusaka.bigwork.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Sale {
    private IntegerProperty id;
    private IntegerProperty customerId;
    private StringProperty customerName;
    private StringProperty itemType;
    private IntegerProperty itemId;
    private StringProperty itemTitle;
    private IntegerProperty quantity;
    private DoubleProperty totalPrice;
    private ObjectProperty<LocalDate> saleDate;
    
    public Sale() {
        this.id = new SimpleIntegerProperty();
        this.customerId = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
        this.itemType = new SimpleStringProperty();
        this.itemId = new SimpleIntegerProperty();
        this.itemTitle = new SimpleStringProperty();
        this.quantity = new SimpleIntegerProperty();
        this.totalPrice = new SimpleDoubleProperty();
        this.saleDate = new SimpleObjectProperty<>();
    }
    
    // Getters and setters
    public int getId() {
        return id.get();
    }
    
    public void setId(int id) {
        this.id.set(id);
    }
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    public int getCustomerId() {
        return customerId.get();
    }
    
    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }
    
    public IntegerProperty customerIdProperty() {
        return customerId;
    }
    
    public String getCustomerName() {
        return customerName.get();
    }
    
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    
    public StringProperty customerNameProperty() {
        return customerName;
    }
    
    public String getItemType() {
        return itemType.get();
    }
    
    public void setItemType(String itemType) {
        this.itemType.set(itemType);
    }
    
    public StringProperty itemTypeProperty() {
        return itemType;
    }
    
    public int getItemId() {
        return itemId.get();
    }
    
    public void setItemId(int itemId) {
        this.itemId.set(itemId);
    }
    
    public IntegerProperty itemIdProperty() {
        return itemId;
    }
    
    public String getItemTitle() {
        return itemTitle.get();
    }
    
    public void setItemTitle(String itemTitle) {
        this.itemTitle.set(itemTitle);
    }
    
    public StringProperty itemTitleProperty() {
        return itemTitle;
    }
    
    public int getQuantity() {
        return quantity.get();
    }
    
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
    
    public IntegerProperty quantityProperty() {
        return quantity;
    }
    
    public double getTotalPrice() {
        return totalPrice.get();
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice.set(totalPrice);
    }
    
    public DoubleProperty totalPriceProperty() {
        return totalPrice;
    }
    
    public LocalDate getSaleDate() {
        return saleDate.get();
    }
    
    public void setSaleDate(LocalDate saleDate) {
        this.saleDate.set(saleDate);
    }
    
    public ObjectProperty<LocalDate> saleDateProperty() {
        return saleDate;
    }
} 
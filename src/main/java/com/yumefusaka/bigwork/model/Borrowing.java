package com.yumefusaka.bigwork.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Borrowing {
    private IntegerProperty id;
    private IntegerProperty customerId;
    private StringProperty customerName;
    private StringProperty itemType;
    private IntegerProperty itemId;
    private StringProperty itemTitle;
    private ObjectProperty<LocalDate> borrowDate;
    private ObjectProperty<LocalDate> returnDate;
    private StringProperty status;
    
    public Borrowing() {
        this.id = new SimpleIntegerProperty();
        this.customerId = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
        this.itemType = new SimpleStringProperty();
        this.itemId = new SimpleIntegerProperty();
        this.itemTitle = new SimpleStringProperty();
        this.borrowDate = new SimpleObjectProperty<>();
        this.returnDate = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
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
    
    public LocalDate getBorrowDate() {
        return borrowDate.get();
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate.set(borrowDate);
    }
    
    public ObjectProperty<LocalDate> borrowDateProperty() {
        return borrowDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate.get();
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate.set(returnDate);
    }
    
    public ObjectProperty<LocalDate> returnDateProperty() {
        return returnDate;
    }
    
    public String getStatus() {
        return status.get();
    }
    
    public void setStatus(String status) {
        this.status.set(status);
    }
    
    public StringProperty statusProperty() {
        return status;
    }
} 
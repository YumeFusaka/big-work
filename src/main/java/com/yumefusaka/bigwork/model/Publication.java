package com.yumefusaka.bigwork.model;

import javafx.beans.property.*;

public abstract class Publication {
    protected IntegerProperty id;
    protected StringProperty title;
    protected IntegerProperty publisherId;
    protected IntegerProperty categoryId;
    protected IntegerProperty totalQuantity;
    protected IntegerProperty availableQuantity;
    protected DoubleProperty price;
    
    public Publication() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.publisherId = new SimpleIntegerProperty();
        this.categoryId = new SimpleIntegerProperty();
        this.totalQuantity = new SimpleIntegerProperty();
        this.availableQuantity = new SimpleIntegerProperty();
        this.price = new SimpleDoubleProperty();
    }
    
    public int getId() {
        return id.get();
    }
    
    public void setId(int id) {
        this.id.set(id);
    }
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    public String getTitle() {
        return title.get();
    }
    
    public void setTitle(String title) {
        this.title.set(title);
    }
    
    public StringProperty titleProperty() {
        return title;
    }
    
    public int getPublisherId() {
        return publisherId.get();
    }
    
    public void setPublisherId(int publisherId) {
        this.publisherId.set(publisherId);
    }
    
    public IntegerProperty publisherIdProperty() {
        return publisherId;
    }
    
    public int getCategoryId() {
        return categoryId.get();
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }
    
    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }
    
    public int getTotalQuantity() {
        return totalQuantity.get();
    }
    
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity.set(totalQuantity);
    }
    
    public IntegerProperty totalQuantityProperty() {
        return totalQuantity;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity.get();
    }
    
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity.set(availableQuantity);
    }
    
    public IntegerProperty availableQuantityProperty() {
        return availableQuantity;
    }
    
    public double getPrice() {
        return price.get();
    }
    
    public void setPrice(double price) {
        this.price.set(price);
    }
    
    public DoubleProperty priceProperty() {
        return price;
    }
} 
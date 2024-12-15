package com.yumefusaka.bigwork.model;

import javafx.beans.property.*;

public class BookCategory {
    private IntegerProperty id;
    private StringProperty name;
    
    public BookCategory() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
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
    
    public String getName() {
        return name.get();
    }
    
    public void setName(String name) {
        this.name.set(name);
    }
    
    public StringProperty nameProperty() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
} 
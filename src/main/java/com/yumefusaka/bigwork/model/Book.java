package com.yumefusaka.bigwork.model;

import javafx.beans.property.*;

public class Book extends Publication {
    private String isbn;
    private String author;
    private StringProperty publisherName;
    private StringProperty categoryName;
    
    public Book() {
        super();
        this.publisherName = new SimpleStringProperty();
        this.categoryName = new SimpleStringProperty();
    }
    
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisherName() {
        return publisherName.get();
    }

    public void setPublisherName(String publisherName) {
        this.publisherName.set(publisherName);
    }

    public StringProperty publisherNameProperty() {
        return publisherName;
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }

    public StringProperty categoryNameProperty() {
        return categoryName;
    }
}
package com.yumefusaka.bigwork.model;

import javafx.beans.property.*;

public class Magazine extends Publication {
    private StringProperty issn;
    private StringProperty issueNumber;
    private StringProperty publisherName;
    private StringProperty categoryName;
    
    public Magazine() {
        super();
        this.issn = new SimpleStringProperty();
        this.issueNumber = new SimpleStringProperty();
        this.publisherName = new SimpleStringProperty();
        this.categoryName = new SimpleStringProperty();
    }
    
    public String getIssn() {
        return issn.get();
    }
    
    public void setIssn(String issn) {
        this.issn.set(issn);
    }
    
    public StringProperty issnProperty() {
        return issn;
    }
    
    public String getIssueNumber() {
        return issueNumber.get();
    }
    
    public void setIssueNumber(String issueNumber) {
        this.issueNumber.set(issueNumber);
    }
    
    public StringProperty issueNumberProperty() {
        return issueNumber;
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
    
    @Override
    public String toString() {
        return getTitle();
    }
} 
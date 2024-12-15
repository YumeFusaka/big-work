package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.PublisherDAO;
import com.yumefusaka.bigwork.dao.BookCategoryDAO;
import com.yumefusaka.bigwork.model.Book;
import com.yumefusaka.bigwork.model.Publisher;
import com.yumefusaka.bigwork.model.BookCategory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class BookEditDialogController {
    @FXML
    private TextField titleField;
    
    @FXML
    private ComboBox<Publisher> publisherComboBox;
    
    @FXML
    private ComboBox<BookCategory> categoryComboBox;
    
    @FXML
    private TextField totalQuantityField;
    
    @FXML
    private TextField availableQuantityField;
    
    @FXML
    private TextField priceField;

    private Stage dialogStage;
    private Book book;
    private boolean saveClicked = false;
    private PublisherDAO publisherDAO = new PublisherDAO();
    private BookCategoryDAO categoryDAO = new BookCategoryDAO();

    @FXML
    private void initialize() {
        loadPublishersAndCategories();
    }

    private void loadPublishersAndCategories() {
        try {
            List<Publisher> publishers = publisherDAO.findAll();
            List<BookCategory> categories = categoryDAO.findAll();
            
            publisherComboBox.setItems(FXCollections.observableArrayList(publishers));
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        } catch (SQLException e) {
            showError("加载数据失败", e.getMessage());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBook(Book book) {
        this.book = book;
        
        titleField.setText(book.getTitle());
        
        // 设置出版社和类别的选中项
        publisherComboBox.getItems().forEach(publisher -> {
            if (publisher.getId() == book.getPublisherId()) {
                publisherComboBox.setValue(publisher);
            }
        });
        
        categoryComboBox.getItems().forEach(category -> {
            if (category.getId() == book.getCategoryId()) {
                categoryComboBox.setValue(category);
            }
        });
        
        totalQuantityField.setText(String.valueOf(book.getTotalQuantity()));
        availableQuantityField.setText(String.valueOf(book.getAvailableQuantity()));
        priceField.setText(String.valueOf(book.getPrice()));
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            book.setTitle(titleField.getText());
            book.setPublisherId(publisherComboBox.getValue().getId());
            book.setCategoryId(categoryComboBox.getValue().getId());
            book.setTotalQuantity(Integer.parseInt(totalQuantityField.getText()));
            book.setAvailableQuantity(Integer.parseInt(availableQuantityField.getText()));
            book.setPrice(Double.parseDouble(priceField.getText()));

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage.append("书名不能为空！\n");
        }

        if (publisherComboBox.getValue() == null) {
            errorMessage.append("请选择出版社！\n");
        }

        if (categoryComboBox.getValue() == null) {
            errorMessage.append("请选择类别！\n");
        }

        try {
            int totalQuantity = Integer.parseInt(totalQuantityField.getText());
            if (totalQuantity < 0) {
                errorMessage.append("总数量不能为负数！\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("总数量必须是整数！\n");
        }

        try {
            int availableQuantity = Integer.parseInt(availableQuantityField.getText());
            if (availableQuantity < 0) {
                errorMessage.append("可用数量不能为负数！\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("可用数量必须是整数！\n");
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                errorMessage.append("价格不能为负数！\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("价格必须是数字！\n");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showError("输入错误", errorMessage.toString());
            return false;
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
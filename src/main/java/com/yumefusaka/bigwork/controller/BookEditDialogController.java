package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BookCategoryDAO;
import com.yumefusaka.bigwork.dao.PublisherDAO;
import com.yumefusaka.bigwork.model.Book;
import com.yumefusaka.bigwork.model.BookCategory;
import com.yumefusaka.bigwork.model.Publisher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

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
    private boolean okClicked = false;
    private PublisherDAO publisherDAO = new PublisherDAO();
    private BookCategoryDAO categoryDAO = new BookCategoryDAO();

    @FXML
    private void initialize() {
        try {
            // 加载出版社和类别数据
            publisherComboBox.getItems().addAll(publisherDAO.findAll());
            categoryComboBox.getItems().addAll(categoryDAO.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBook(Book book) {
        this.book = book;
        titleField.setText(book.getTitle());
        totalQuantityField.setText(String.valueOf(book.getTotalQuantity()));
        availableQuantityField.setText(String.valueOf(book.getAvailableQuantity()));
        priceField.setText(String.valueOf(book.getPrice()));

        // 设置选中的出版社和类别
        for (Publisher publisher : publisherComboBox.getItems()) {
            if (publisher.getId() == book.getPublisherId()) {
                publisherComboBox.setValue(publisher);
                break;
            }
        }
        for (BookCategory category : categoryComboBox.getItems()) {
            if (category.getId() == book.getCategoryId()) {
                categoryComboBox.setValue(category);
                break;
            }
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            book.setTitle(titleField.getText());
            book.setPublisherId(publisherComboBox.getValue().getId());
            book.setCategoryId(categoryComboBox.getValue().getId());
            book.setTotalQuantity(Integer.parseInt(totalQuantityField.getText()));
            book.setAvailableQuantity(Integer.parseInt(availableQuantityField.getText()));
            book.setPrice(Double.parseDouble(priceField.getText()));
            
            // 设置显示名称
            book.setPublisherName(publisherComboBox.getValue().getName());
            book.setCategoryName(categoryComboBox.getValue().getName());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage += "书名不能为空！\n";
        }
        if (publisherComboBox.getValue() == null) {
            errorMessage += "请选择出版社！\n";
        }
        if (categoryComboBox.getValue() == null) {
            errorMessage += "请选择类别！\n";
        }

        try {
            int totalQuantity = Integer.parseInt(totalQuantityField.getText());
            if (totalQuantity < 0) {
                errorMessage += "总数量必须大于等于0！\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "总数量必须是有效的整数！\n";
        }

        try {
            int availableQuantity = Integer.parseInt(availableQuantityField.getText());
            if (availableQuantity < 0) {
                errorMessage += "可用数量必须大于等于0！\n";
            }
            if (availableQuantity > Integer.parseInt(totalQuantityField.getText())) {
                errorMessage += "可用数量不能大于总数量！\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "可用数量必须是有效的整数！\n";
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                errorMessage += "价格必须大于等于0！\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "价格必须是有效的数字！\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("输入错误");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
} 
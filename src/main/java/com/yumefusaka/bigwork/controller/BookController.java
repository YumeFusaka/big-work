package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BookDAO;
import com.yumefusaka.bigwork.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class BookController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Book> bookTable;
    
    @FXML
    private TableColumn<Book, Integer> idColumn;
    
    @FXML
    private TableColumn<Book, String> titleColumn;
    
    @FXML
    private TableColumn<Book, String> publisherColumn;
    
    @FXML
    private TableColumn<Book, String> categoryColumn;
    
    @FXML
    private TableColumn<Book, Integer> totalQuantityColumn;
    
    @FXML
    private TableColumn<Book, Integer> availableQuantityColumn;
    
    @FXML
    private TableColumn<Book, Double> priceColumn;

    private BookDAO bookDAO;
    private ObservableList<Book> bookList;

    @FXML
    private void initialize() {
        bookDAO = new BookDAO();
        bookList = FXCollections.observableArrayList();

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisherName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        totalQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        availableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // 设置表格数据
        bookTable.setItems(bookList);
        
        // 加载数据
        loadBooks();
    }

    private void loadBooks() {
        try {
            bookList.clear();
            bookList.addAll(bookDAO.findAll());
        } catch (SQLException e) {
            showError("加载图书数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadBooks();
            } else {
                bookList.clear();
                bookList.addAll(bookDAO.search(keyword));
            }
        } catch (SQLException e) {
            showError("搜索失败", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        // TODO: 实现添加图书对话框
    }

    @FXML
    private void handleEdit() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("错误", "请选择要编辑的图书");
            return;
        }
        // TODO: 实现编辑图书对话框
    }

    @FXML
    private void handleDelete() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("错误", "请选择要删除的图书");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
            "确认删除",
            "确定要删除图书 \"" + selectedBook.getTitle() + "\" 吗？"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookDAO.delete(selectedBook.getId());
                bookList.remove(selectedBook);
            } catch (SQLException e) {
                showError("删除失败", e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
} 
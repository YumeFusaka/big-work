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
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
        Book newBook = new Book();
        // 设置默认值
        newBook.setTotalQuantity(0);
        newBook.setAvailableQuantity(0);
        newBook.setPrice(0.0);
        
        if (showEditDialog(newBook, "新增图书")) {
            try {
                bookDAO.insert(newBook);
                bookList.add(newBook);
            } catch (SQLException e) {
                showError("添加失败", e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("错误", "请选择要编辑的图书");
            return;
        }

        if (showEditDialog(selectedBook, "编辑图书")) {
            try {
                bookDAO.update(selectedBook);
                bookTable.refresh();
            } catch (SQLException e) {
                showError("更新失败", e.getMessage());
            }
        }
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

    private boolean showEditDialog(Book book, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/book-edit-dialog.fxml"));
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(bookTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            BookEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setBook(book);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            showError("对话框错误", "无法打开编辑对话框");
            return false;
        }
    }
} 
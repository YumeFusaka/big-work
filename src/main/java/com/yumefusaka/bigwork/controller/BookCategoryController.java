package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BookCategoryDAO;
import com.yumefusaka.bigwork.model.BookCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.Optional;

public class BookCategoryController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<BookCategory> categoryTable;
    
    @FXML
    private TableColumn<BookCategory, Number> idColumn;
    
    @FXML
    private TableColumn<BookCategory, String> nameColumn;
    
    private BookCategoryDAO categoryDAO = new BookCategoryDAO();
    private ObservableList<BookCategory> categoryList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // 初始化表格列
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        // 设置表格数据
        categoryTable.setItems(categoryList);
        
        // 加载所有类别
        loadCategories();
    }
    
    private void loadCategories() {
        try {
            categoryList.clear();
            categoryList.addAll(categoryDAO.findAll());
        } catch (SQLException e) {
            showError("加载类别失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        try {
            categoryList.clear();
            if (keyword.isEmpty()) {
                categoryList.addAll(categoryDAO.findAll());
            } else {
                categoryList.addAll(categoryDAO.search(keyword));
            }
        } catch (SQLException e) {
            showError("搜索失败", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<BookCategory> dialog = createCategoryDialog(new BookCategory(), "新增类别");
        Optional<BookCategory> result = dialog.showAndWait();
        
        result.ifPresent(category -> {
            try {
                categoryDAO.insert(category);
                loadCategories();
            } catch (SQLException e) {
                showError("添加类别失败", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        BookCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showError("编辑错误", "请先选择要编辑的类别");
            return;
        }
        
        Dialog<BookCategory> dialog = createCategoryDialog(selectedCategory, "编辑类别");
        Optional<BookCategory> result = dialog.showAndWait();
        
        result.ifPresent(category -> {
            try {
                categoryDAO.update(category);
                loadCategories();
            } catch (SQLException e) {
                showError("更新类别失败", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        BookCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showError("删除错误", "请先选择要删除的类别");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除类别 \"" + selectedCategory.getName() + "\" 吗？");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryDAO.delete(selectedCategory.getId());
                loadCategories();
            } catch (SQLException e) {
                showError("删除类别失败", e.getMessage());
            }
        }
    }
    
    private Dialog<BookCategory> createCategoryDialog(BookCategory category, String title) {
        Dialog<BookCategory> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        
        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nameField = new TextField(category.getName());
        
        grid.add(new Label("名称:"), 0, 0);
        grid.add(nameField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                category.setName(nameField.getText());
                return category;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
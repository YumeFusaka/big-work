package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.MagazineCategoryDAO;
import com.yumefusaka.bigwork.model.MagazineCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.Optional;

public class MagazineCategoryController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<MagazineCategory> categoryTable;
    
    @FXML
    private TableColumn<MagazineCategory, Integer> idColumn;
    
    @FXML
    private TableColumn<MagazineCategory, String> nameColumn;
    
    @FXML
    private TableColumn<MagazineCategory, String> descriptionColumn;

    private MagazineCategoryDAO categoryDAO;
    private ObservableList<MagazineCategory> categoryList;

    @FXML
    private void initialize() {
        categoryDAO = new MagazineCategoryDAO();
        categoryList = FXCollections.observableArrayList();

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // 设置表格数据
        categoryTable.setItems(categoryList);
        
        // 加载数据
        loadCategories();
    }

    private void loadCategories() {
        try {
            categoryList.clear();
            categoryList.addAll(categoryDAO.findAll());
        } catch (SQLException e) {
            showError("加载数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadCategories();
            } else {
                categoryList.clear();
                categoryList.addAll(categoryDAO.search(keyword));
            }
        } catch (SQLException e) {
            showError("搜索失败", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        // TODO: 实现添加功能
    }

    @FXML
    private void handleEdit() {
        MagazineCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showError("错误", "请选择要编辑的类别");
            return;
        }
        // TODO: 实现编辑功能
    }

    @FXML
    private void handleDelete() {
        MagazineCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showError("错误", "请选择要删除的类别");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
            "确认删除",
            "确定要删除类别 \"" + selectedCategory.getName() + "\" 吗？"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryDAO.delete(selectedCategory.getId());
                categoryList.remove(selectedCategory);
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
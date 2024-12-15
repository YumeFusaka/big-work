package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.MagazineCategoryDAO;
import com.yumefusaka.bigwork.model.MagazineCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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

    private MagazineCategoryDAO categoryDAO;
    private ObservableList<MagazineCategory> categoryList;

    @FXML
    private void initialize() {
        categoryDAO = new MagazineCategoryDAO();
        categoryList = FXCollections.observableArrayList();

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

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
        MagazineCategory newCategory = new MagazineCategory();
        boolean okClicked = showEditDialog(newCategory, "新增类别");
        
        if (okClicked) {
            try {
                categoryDAO.insert(newCategory);
                categoryList.add(newCategory);
            } catch (SQLException e) {
                showError("添加失败", e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        MagazineCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showError("错误", "请选择要编辑的类别");
            return;
        }
        
        boolean okClicked = showEditDialog(selectedCategory, "编辑类别");
        
        if (okClicked) {
            try {
                categoryDAO.update(selectedCategory);
                categoryTable.refresh();
            } catch (SQLException e) {
                showError("更新失败", e.getMessage());
            }
        }
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

    private boolean showEditDialog(MagazineCategory category, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/magazine-category-edit-dialog.fxml"));
            Parent page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(categoryTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            MagazineCategoryEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCategory(category);
            
            dialogStage.showAndWait();
            return controller.isOkClicked();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("对话框错误", "无法打开编辑对话框");
            return false;
        }
    }
} 
package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.MagazineDAO;
import com.yumefusaka.bigwork.model.Magazine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.Optional;

public class MagazineController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Magazine> magazineTable;
    
    @FXML
    private TableColumn<Magazine, Integer> idColumn;
    
    @FXML
    private TableColumn<Magazine, String> titleColumn;
    
    @FXML
    private TableColumn<Magazine, String> publisherColumn;
    
    @FXML
    private TableColumn<Magazine, String> categoryColumn;
    
    @FXML
    private TableColumn<Magazine, Integer> totalQuantityColumn;
    
    @FXML
    private TableColumn<Magazine, Integer> availableQuantityColumn;
    
    @FXML
    private TableColumn<Magazine, Double> priceColumn;

    private MagazineDAO magazineDAO;
    private ObservableList<Magazine> magazineList;

    @FXML
    private void initialize() {
        magazineDAO = new MagazineDAO();
        magazineList = FXCollections.observableArrayList();

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisherName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        totalQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        availableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // 设置表格数据
        magazineTable.setItems(magazineList);
        
        // 加载数据
        loadMagazines();
    }

    private void loadMagazines() {
        try {
            magazineList.clear();
            magazineList.addAll(magazineDAO.findAll());
        } catch (SQLException e) {
            showError("加载数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadMagazines();
            } else {
                magazineList.clear();
                magazineList.addAll(magazineDAO.search(keyword));
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
        // TODO: 实现编辑功能
    }

    @FXML
    private void handleDelete() {
        Magazine selectedMagazine = magazineTable.getSelectionModel().getSelectedItem();
        if (selectedMagazine == null) {
            showError("错误", "请选择要删除的期刊");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除期刊 \"" + selectedMagazine.getTitle() + "\" 吗？");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                magazineDAO.delete(selectedMagazine.getId());
                magazineList.remove(selectedMagazine);
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
} 
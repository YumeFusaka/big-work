package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.MagazineDAO;
import com.yumefusaka.bigwork.model.Magazine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.IOException;
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
        Magazine newMagazine = new Magazine();
        // 设置默认值
        newMagazine.setTotalQuantity(0);
        newMagazine.setAvailableQuantity(0);
        newMagazine.setPrice(0.0);
        
        if (showEditDialog(newMagazine, "新增期刊")) {
            try {
                magazineDAO.insert(newMagazine);
                magazineList.add(newMagazine);
            } catch (SQLException e) {
                showError("添加失败", e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        Magazine selectedMagazine = magazineTable.getSelectionModel().getSelectedItem();
        if (selectedMagazine == null) {
            showError("错误", "请选择要编辑的期刊");
            return;
        }

        if (showEditDialog(selectedMagazine, "编辑期刊")) {
            try {
                magazineDAO.update(selectedMagazine);
                magazineTable.refresh();
            } catch (SQLException e) {
                showError("更新失败", e.getMessage());
            }
        }
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

    private boolean showEditDialog(Magazine magazine, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/magazine-edit-dialog.fxml"));
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(magazineTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            MagazineEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMagazine(magazine);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            showError("对话框错误", "无法打开编辑对话框");
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
package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.PublisherDAO;
import com.yumefusaka.bigwork.model.Publisher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.Optional;

public class PublisherController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Publisher> publisherTable;
    
    @FXML
    private TableColumn<Publisher, Integer> idColumn;
    
    @FXML
    private TableColumn<Publisher, String> nameColumn;
    
    @FXML
    private TableColumn<Publisher, String> addressColumn;
    
    @FXML
    private TableColumn<Publisher, String> contactColumn;

    private PublisherDAO publisherDAO;
    private ObservableList<Publisher> publisherList;

    @FXML
    private void initialize() {
        publisherDAO = new PublisherDAO();
        publisherList = FXCollections.observableArrayList();

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // 设置表格数据
        publisherTable.setItems(publisherList);
        
        // 加载数据
        loadPublishers();
    }

    private void loadPublishers() {
        try {
            publisherList.clear();
            publisherList.addAll(publisherDAO.findAll());
        } catch (SQLException e) {
            showError("加载数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadPublishers();
            } else {
                publisherList.clear();
                publisherList.addAll(publisherDAO.search(keyword));
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
        Publisher selectedPublisher = publisherTable.getSelectionModel().getSelectedItem();
        if (selectedPublisher == null) {
            showError("错误", "请选择要编辑的出版社");
            return;
        }
        // TODO: 实现编辑功能
    }

    @FXML
    private void handleDelete() {
        Publisher selectedPublisher = publisherTable.getSelectionModel().getSelectedItem();
        if (selectedPublisher == null) {
            showError("错误", "请选择要删除的出版社");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
            "确认删除",
            "确定要删除出版社 \"" + selectedPublisher.getName() + "\" 吗？"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                publisherDAO.delete(selectedPublisher.getId());
                publisherList.remove(selectedPublisher);
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
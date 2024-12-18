package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.CustomerDAO;
import com.yumefusaka.bigwork.model.Customer;
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

public class CustomerController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Customer> customerTable;
    
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    
    @FXML
    private TableColumn<Customer, String> nameColumn;
    
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    
    @FXML
    private TableColumn<Customer, String> emailColumn;

    private CustomerDAO customerDAO;
    private ObservableList<Customer> customerList;

    @FXML
    private void initialize() {
        customerDAO = new CustomerDAO();
        customerList = FXCollections.observableArrayList();

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 设置表格数据
        customerTable.setItems(customerList);
        
        // 加载数据
        loadCustomers();
    }

    private void loadCustomers() {
        try {
            customerList.clear();
            customerList.addAll(customerDAO.findAll());
        } catch (SQLException e) {
            showError("加载数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadCustomers();
            } else {
                customerList.clear();
                customerList.addAll(customerDAO.search(keyword));
            }
        } catch (SQLException e) {
            showError("搜索失败", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        Customer newCustomer = new Customer();
        boolean okClicked = showEditDialog(newCustomer, "新增客户");
        
        if (okClicked) {
            try {
                customerDAO.insert(newCustomer);
                customerList.add(newCustomer);
            } catch (SQLException e) {
                showError("保存失败", e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showError("错误", "请选择要编辑的客户");
            return;
        }

        boolean okClicked = showEditDialog(selectedCustomer, "编辑客户");
        
        if (okClicked) {
            try {
                customerDAO.update(selectedCustomer);
                customerTable.refresh();
            } catch (SQLException e) {
                showError("保存失败", e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showError("错误", "请选择要删除的客户");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
            "确认删除",
            "确定要删除客户 \"" + selectedCustomer.getName() + "\" 吗？"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                customerDAO.delete(selectedCustomer.getId());
                customerList.remove(selectedCustomer);
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

    private boolean showEditDialog(Customer customer, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/customer-edit-dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(customerTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CustomerEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCustomer(customer);

            dialogStage.showAndWait();
            return controller.isOkClicked();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("对话框错误", "无法打开编辑对话框");
            return false;
        }
    }
} 
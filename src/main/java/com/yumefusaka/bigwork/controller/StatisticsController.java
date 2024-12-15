package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.CustomerDAO;
import com.yumefusaka.bigwork.model.Customer;
import com.yumefusaka.bigwork.utils.DatabaseConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class StatisticsController {
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox<Customer> customerComboBox;
    
    @FXML
    private TableView<PeriodStatistics> periodTable;
    
    @FXML
    private TableColumn<PeriodStatistics, String> itemTypeColumn;
    
    @FXML
    private TableColumn<PeriodStatistics, String> titleColumn;
    
    @FXML
    private TableColumn<PeriodStatistics, Integer> borrowCountColumn;
    
    @FXML
    private TableColumn<PeriodStatistics, Integer> saleCountColumn;
    
    @FXML
    private TableView<CustomerStatistics> customerTable;
    
    @FXML
    private TableColumn<CustomerStatistics, String> operationTypeColumn;
    
    @FXML
    private TableColumn<CustomerStatistics, String> customerItemTypeColumn;
    
    @FXML
    private TableColumn<CustomerStatistics, String> customerTitleColumn;
    
    @FXML
    private TableColumn<CustomerStatistics, Integer> quantityColumn;
    
    @FXML
    private TableColumn<CustomerStatistics, Date> operationDateColumn;

    private CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<PeriodStatistics> periodStatsList = FXCollections.observableArrayList();
    private ObservableList<CustomerStatistics> customerStatsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 初始化日期选择器
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
        
        // 初始化表格列
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        borrowCountColumn.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));
        saleCountColumn.setCellValueFactory(new PropertyValueFactory<>("saleCount"));
        
        operationTypeColumn.setCellValueFactory(new PropertyValueFactory<>("operationType"));
        customerItemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        customerTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        operationDateColumn.setCellValueFactory(new PropertyValueFactory<>("operationDate"));
        
        // 设置表格数据
        periodTable.setItems(periodStatsList);
        customerTable.setItems(customerStatsList);
        
        // 加载客户数据
        loadCustomers();
    }

    private void loadCustomers() {
        try {
            customerComboBox.setItems(FXCollections.observableArrayList(customerDAO.findAll()));
        } catch (SQLException e) {
            showError("加载客户数据失败", e.getMessage());
        }
    }

    @FXML
    private void handlePeriodSearch() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showError("错误", "请选择开始和结束日期");
            return;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_statistics_by_period(?, ?)}")) {
            
            stmt.setDate(1, Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(endDatePicker.getValue()));
            
            ResultSet rs = stmt.executeQuery();
            periodStatsList.clear();
            
            while (rs.next()) {
                periodStatsList.add(new PeriodStatistics(
                    rs.getString("item_type"),
                    rs.getString("title"),
                    rs.getInt("borrow_count"),
                    rs.getInt("sale_count")
                ));
            }
        } catch (SQLException e) {
            showError("查询失败", e.getMessage());
        }
    }

    @FXML
    private void handleCustomerSearch() {
        if (customerComboBox.getValue() == null) {
            showError("错误", "请选择客户");
            return;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_statistics_by_customer(?)}")) {
            
            stmt.setInt(1, customerComboBox.getValue().getId());
            
            ResultSet rs = stmt.executeQuery();
            customerStatsList.clear();
            
            while (rs.next()) {
                customerStatsList.add(new CustomerStatistics(
                    rs.getString("operation_type"),
                    rs.getString("item_type"),
                    rs.getString("title"),
                    rs.getInt("quantity"),
                    rs.getDate("operation_date")
                ));
            }
        } catch (SQLException e) {
            showError("查询失败", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 内部类用于表格数据绑定
    public static class PeriodStatistics {
        private final SimpleStringProperty itemType;
        private final SimpleStringProperty title;
        private final SimpleIntegerProperty borrowCount;
        private final SimpleIntegerProperty saleCount;

        public PeriodStatistics(String itemType, String title, int borrowCount, int saleCount) {
            this.itemType = new SimpleStringProperty(itemType);
            this.title = new SimpleStringProperty(title);
            this.borrowCount = new SimpleIntegerProperty(borrowCount);
            this.saleCount = new SimpleIntegerProperty(saleCount);
        }

        // Getters
        public String getItemType() { return itemType.get(); }
        public String getTitle() { return title.get(); }
        public int getBorrowCount() { return borrowCount.get(); }
        public int getSaleCount() { return saleCount.get(); }
    }

    public static class CustomerStatistics {
        private final SimpleStringProperty operationType;
        private final SimpleStringProperty itemType;
        private final SimpleStringProperty title;
        private final SimpleIntegerProperty quantity;
        private final SimpleObjectProperty<Date> operationDate;

        public CustomerStatistics(String operationType, String itemType, String title, 
                                int quantity, Date operationDate) {
            this.operationType = new SimpleStringProperty(operationType);
            this.itemType = new SimpleStringProperty(itemType);
            this.title = new SimpleStringProperty(title);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.operationDate = new SimpleObjectProperty<>(operationDate);
        }

        // Getters
        public String getOperationType() { return operationType.get(); }
        public String getItemType() { return itemType.get(); }
        public String getTitle() { return title.get(); }
        public int getQuantity() { return quantity.get(); }
        public Date getOperationDate() { return operationDate.get(); }
    }
} 
package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.SaleDAO;
import com.yumefusaka.bigwork.model.Sale;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesController {
    @FXML
    private ComboBox<String> searchTypeComboBox;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Sale> salesTable;
    
    @FXML
    private TableColumn<Sale, Integer> idColumn;
    
    @FXML
    private TableColumn<Sale, String> customerNameColumn;
    
    @FXML
    private TableColumn<Sale, String> itemTypeColumn;
    
    @FXML
    private TableColumn<Sale, String> itemTitleColumn;
    
    @FXML
    private TableColumn<Sale, Integer> quantityColumn;
    
    @FXML
    private TableColumn<Sale, Double> totalPriceColumn;
    
    @FXML
    private TableColumn<Sale, LocalDate> saleDateColumn;

    private SaleDAO saleDAO;
    private ObservableList<Sale> saleList;

    @FXML
    private void initialize() {
        saleDAO = new SaleDAO();
        saleList = FXCollections.observableArrayList();
        
        // 初始化搜索类型
        searchTypeComboBox.setItems(FXCollections.observableArrayList(
            "全部", "客户姓名", "图书/期刊名"
        ));
        searchTypeComboBox.setValue("全部");

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        itemTitleColumn.setCellValueFactory(new PropertyValueFactory<>("itemTitle"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        // 设置日期格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        saleDateColumn.setCellFactory(column -> new TableCell<Sale, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });

        // 设置金额格式
        totalPriceColumn.setCellFactory(column -> new TableCell<Sale, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", price));
                }
            }
        });

        // 设置表格数据
        salesTable.setItems(saleList);
        
        // 加载数据
        loadSales();
    }

    private void loadSales() {
        try {
            saleList.clear();
            saleList.addAll(saleDAO.findAll());
        } catch (SQLException e) {
            showError("加载销售数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        // TODO: 实现搜索功能
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sales-dialog.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("新增销售");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(salesTable.getScene().getWindow());
            dialogStage.setScene(scene);
            
            SalesDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isSaveClicked()) {
                loadSales();
            }
        } catch (Exception e) {
            showError("打开销售对话框失败", e.getMessage());
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
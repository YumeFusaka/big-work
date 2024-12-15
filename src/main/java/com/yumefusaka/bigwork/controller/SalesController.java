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
import java.util.List;
import java.util.stream.Collectors;

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
        String searchType = searchTypeComboBox.getValue();
        String keyword = searchField.getText().trim();
        
        try {
            List<Sale> searchResults;
            
            if (keyword.isEmpty()) {
                // 如果搜索关键词为空，显示所有记录
                searchResults = saleDAO.findAll();
            } else {
                searchResults = saleList.stream()
                    .filter(sale -> {
                        switch (searchType) {
                            case "客户姓名":
                                return sale.getCustomerName().toLowerCase()
                                    .contains(keyword.toLowerCase());
                            case "图书/期刊名":
                                return sale.getItemTitle().toLowerCase()
                                    .contains(keyword.toLowerCase());
                            default: // "全部"
                                return sale.getCustomerName().toLowerCase()
                                        .contains(keyword.toLowerCase()) ||
                                    sale.getItemTitle().toLowerCase()
                                        .contains(keyword.toLowerCase());
                        }
                    })
                    .collect(Collectors.toList());
            }
            
            saleList.clear();
            saleList.addAll(searchResults);
        } catch (SQLException e) {
            showError("搜索失败", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            // 加载销售对话框FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/sales-dialog.fxml"));
            if (loader.getLocation() == null) {
                throw new IllegalStateException("无法找到FXML文件: /fxml/sales-dialog.fxml");
            }
            
            Scene scene = new Scene(loader.load());
            
            // 创建对话框窗口
            Stage dialogStage = new Stage();
            dialogStage.setTitle("新增销售");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(salesTable.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.setResizable(false); // 禁止调整窗口大小
            
            // 获取对话框控制器
            SalesDialogController controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("无法获取对话框控制器");
            }
            controller.setDialogStage(dialogStage);
            
            // 显示对话框并等待用户响应
            dialogStage.showAndWait();
            
            // 如果用户点击了保存按钮，刷新销售列表
            if (controller.isSaveClicked()) {
                loadSales();
                
                // 显示成功消息
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("销售记录已成功添加！");
                alert.showAndWait();
            }
        } catch (Exception e) {
            showError("打开销售对话框失败", "错误详情: " + e.getMessage());
            e.printStackTrace(); // 在控制台打印详细错误信息
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
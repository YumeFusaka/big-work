package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.SaleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

public class SalesStatsController {
    @FXML
    private DatePicker startDate;
    
    @FXML
    private DatePicker endDate;
    
    @FXML
    private LineChart<String, Number> trendChart;
    
    @FXML
    private PieChart categoryChart;
    
    @FXML
    private TableView<SalesStats> statsTable;
    
    @FXML
    private TableColumn<SalesStats, String> categoryColumn;
    
    @FXML
    private TableColumn<SalesStats, Double> totalSalesColumn;
    
    @FXML
    private TableColumn<SalesStats, Integer> quantityColumn;
    
    @FXML
    private TableColumn<SalesStats, Double> averagePriceColumn;

    private SaleDAO saleDAO;

    @FXML
    private void initialize() {
        saleDAO = new SaleDAO();

        // 设置默认日期范围（最近一个月）
        endDate.setValue(LocalDate.now());
        startDate.setValue(LocalDate.now().minusMonths(1));

        // 初始化表格列
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalSalesColumn.setCellValueFactory(new PropertyValueFactory<>("totalSales"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));

        // 设置金额格式
        totalSalesColumn.setCellFactory(column -> new TableCell<SalesStats, Double>() {
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

        averagePriceColumn.setCellFactory(column -> new TableCell<SalesStats, Double>() {
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

        // 加载统计数据
        handleQuery();
    }

    @FXML
    private void handleQuery() {
        try {
            // 清空旧数据
            trendChart.getData().clear();
            categoryChart.getData().clear();
            statsTable.getItems().clear();

            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            if (start == null || end == null) {
                showError("错误", "请选择开始和结束日期");
                return;
            }

            if (start.isAfter(end)) {
                showError("错误", "开始日期不能晚于结束日期");
                return;
            }

            // TODO: 从数据库加载统计数据并更新图表和表格
            loadTrendChart(start, end);
            loadCategoryChart(start, end);
            loadStatsTable(start, end);

        } catch (Exception e) {
            showError("查询失败", e.getMessage());
        }
    }

    @FXML
    private void handleExport() {
        // TODO: 实现导出报表功能
    }

    private void loadTrendChart(LocalDate start, LocalDate end) {
        // TODO: 加载销售趋势图表数据
    }

    private void loadCategoryChart(LocalDate start, LocalDate end) {
        // TODO: 加载类别统计饼图数据
    }

    private void loadStatsTable(LocalDate start, LocalDate end) {
        // TODO: 加载统计表格数据
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 内部类用于表格数据绑定
    public static class SalesStats {
        private String category;
        private double totalSales;
        private int quantity;
        private double averagePrice;

        public SalesStats(String category, double totalSales, int quantity) {
            this.category = category;
            this.totalSales = totalSales;
            this.quantity = quantity;
            this.averagePrice = quantity > 0 ? totalSales / quantity : 0;
        }

        public String getCategory() {
            return category;
        }

        public double getTotalSales() {
            return totalSales;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getAveragePrice() {
            return averagePrice;
        }
    }
} 
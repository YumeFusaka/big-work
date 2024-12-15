package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.SaleDAO;
import com.yumefusaka.bigwork.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;

import javafx.stage.FileChooser;

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
        if (startDate.getValue() == null || endDate.getValue() == null) {
            showError("错误", "请先选择日期范围并查询数据");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("销售统计报表\n");
        report.append("统计期间: ").append(startDate.getValue()).append(" 至 ").append(endDate.getValue()).append("\n\n");
        
        // 添加表格数据
        report.append("类别\t销售总额\t销售数量\t平均单价\n");
        for (SalesStats stats : statsTable.getItems()) {
            report.append(String.format("%s\t%.2f\t%d\t%.2f\n",
                stats.getCategory(),
                stats.getTotalSales(),
                stats.getQuantity(),
                stats.getAveragePrice()));
        }

        // 保存文件
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存报表");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("文本文件", "*.txt"));
        File file = fileChooser.showSaveDialog(statsTable.getScene().getWindow());
        
        if (file != null) {
            try {
                Files.write(file.toPath(), report.toString().getBytes());
                showSuccess("导出成功", "报表已保存至: " + file.getPath());
            } catch (IOException e) {
                showError("导出失败", e.getMessage());
            }
        }
    }

    @FXML
    private void loadTrendChart(LocalDate start, LocalDate end) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT DATE(sale_date) as sale_date, SUM(total_price) as daily_sales " +
                 "FROM Sales WHERE sale_date BETWEEN ? AND ? " +
                 "GROUP BY DATE(sale_date) ORDER BY sale_date")) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("日销售额");

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                    rs.getDate("sale_date").toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    rs.getDouble("daily_sales")
                ));
            }

            trendChart.getData().add(series);
        } catch (SQLException e) {
            showError("加载趋势图失败", e.getMessage());
        }
    }

    @FXML
    private void loadCategoryChart(LocalDate start, LocalDate end) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT item_type, SUM(total_price) as total_sales " +
                 "FROM Sales WHERE sale_date BETWEEN ? AND ? " +
                 "GROUP BY item_type")) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = "BOOK".equals(rs.getString("item_type")) ? "图书" : "期刊";
                categoryChart.getData().add(new PieChart.Data(
                    type, rs.getDouble("total_sales")
                ));
            }
        } catch (SQLException e) {
            showError("加载类别统计失败", e.getMessage());
        }
    }

    @FXML
    private void loadStatsTable(LocalDate start, LocalDate end) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT item_type, COUNT(*) as sale_count, " +
                 "SUM(quantity) as total_quantity, " +
                 "SUM(total_price) as total_sales " +
                 "FROM Sales WHERE sale_date BETWEEN ? AND ? " +
                 "GROUP BY item_type")) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            ObservableList<SalesStats> statsList = FXCollections.observableArrayList();
            while (rs.next()) {
                String type = "BOOK".equals(rs.getString("item_type")) ? "图书" : "期刊";
                double totalSales = rs.getDouble("total_sales");
                int quantity = rs.getInt("total_quantity");
                
                statsList.add(new SalesStats(type, totalSales, quantity));
            }
            
            statsTable.setItems(statsList);
        } catch (SQLException e) {
            showError("加载统计表格失败", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
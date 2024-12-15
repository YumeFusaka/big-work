package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BorrowingDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

public class BorrowingStatsController {
    @FXML
    private DatePicker startDate;
    
    @FXML
    private DatePicker endDate;
    
    @FXML
    private LineChart<String, Number> trendChart;
    
    @FXML
    private PieChart categoryChart;
    
    @FXML
    private TableView<BorrowingStats> statsTable;
    
    @FXML
    private TableColumn<BorrowingStats, String> categoryColumn;
    
    @FXML
    private TableColumn<BorrowingStats, Integer> totalBorrowingsColumn;
    
    @FXML
    private TableColumn<BorrowingStats, Integer> activeColumn;
    
    @FXML
    private TableColumn<BorrowingStats, Integer> overdueColumn;

    private BorrowingDAO borrowingDAO;

    @FXML
    private void initialize() {
        borrowingDAO = new BorrowingDAO();

        // 设置默认日期范围（最近一个月）
        endDate.setValue(LocalDate.now());
        startDate.setValue(LocalDate.now().minusMonths(1));

        // 初始化表格列
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalBorrowingsColumn.setCellValueFactory(new PropertyValueFactory<>("totalBorrowings"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        overdueColumn.setCellValueFactory(new PropertyValueFactory<>("overdue"));

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
        // TODO: 加载借阅趋势图表数据
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
    public static class BorrowingStats {
        private String category;
        private int totalBorrowings;
        private int active;
        private int overdue;

        public BorrowingStats(String category, int totalBorrowings, int active, int overdue) {
            this.category = category;
            this.totalBorrowings = totalBorrowings;
            this.active = active;
            this.overdue = overdue;
        }

        public String getCategory() {
            return category;
        }

        public int getTotalBorrowings() {
            return totalBorrowings;
        }

        public int getActive() {
            return active;
        }

        public int getOverdue() {
            return overdue;
        }
    }
} 
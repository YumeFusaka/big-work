package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BorrowingDAO;
import com.yumefusaka.bigwork.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        if (startDate.getValue() == null || endDate.getValue() == null) {
            showError("错误", "请先选择日期范围并查询数据");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("借阅统计报表\n");
        report.append("统计期间: ").append(startDate.getValue()).append(" 至 ").append(endDate.getValue()).append("\n\n");
        
        // 添加表格数据
        report.append("类别\t借阅总数\t当前借出\t逾期数量\n");
        for (BorrowingStats stats : statsTable.getItems()) {
            report.append(String.format("%s\t%d\t%d\t%d\n",
                stats.getCategory(),
                stats.getTotalBorrowings(),
                stats.getActive(),
                stats.getOverdue()));
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
                 "SELECT DATE(borrow_date) as borrow_date, COUNT(*) as daily_count " +
                 "FROM Borrowing WHERE borrow_date BETWEEN ? AND ? " +
                 "GROUP BY DATE(borrow_date) ORDER BY borrow_date")) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("日借阅量");

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                    rs.getDate("borrow_date").toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    rs.getInt("daily_count")
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
                 "SELECT item_type, COUNT(*) as borrow_count " +
                 "FROM Borrowing WHERE borrow_date BETWEEN ? AND ? " +
                 "GROUP BY item_type")) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = "BOOK".equals(rs.getString("item_type")) ? "图书" : "期刊";
                categoryChart.getData().add(new PieChart.Data(
                    type, rs.getInt("borrow_count")
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
                 "SELECT item_type, " +
                 "COUNT(*) as total_count, " +
                 "SUM(CASE WHEN status = 'BORROWED' THEN 1 ELSE 0 END) as active_count, " +
                 "SUM(CASE WHEN status = 'BORROWED' AND return_date < CURRENT_DATE THEN 1 ELSE 0 END) as overdue_count " +
                 "FROM Borrowing WHERE borrow_date BETWEEN ? AND ? " +
                 "GROUP BY item_type")) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            ObservableList<BorrowingStats> statsList = FXCollections.observableArrayList();
            while (rs.next()) {
                String type = "BOOK".equals(rs.getString("item_type")) ? "图书" : "期刊";
                statsList.add(new BorrowingStats(
                    type,
                    rs.getInt("total_count"),
                    rs.getInt("active_count"),
                    rs.getInt("overdue_count")
                ));
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
package com.yumefusaka.bigwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.application.Platform;

import java.io.IOException;

public class MainController {
    @FXML
    private TabPane mainTabPane;

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleBookCategory() {
        openTab("图书类别管理", "book-category-view.fxml");
    }

    @FXML
    private void handleMagazineCategory() {
        openTab("期刊类别管理", "magazine-category-view.fxml");
    }

    @FXML
    private void handlePublisher() {
        openTab("出版社管理", "publisher-view.fxml");
    }

    @FXML
    private void handleBook() {
        openTab("图书信息", "book-view.fxml");
    }

    @FXML
    private void handleMagazine() {
        openTab("期刊信息", "magazine-view.fxml");
    }

    @FXML
    private void handleCustomer() {
        openTab("客户信息", "customer-view.fxml");
    }

    @FXML
    private void handleBorrowing() {
        openTab("借阅管理", "borrowing-view.fxml");
    }

    @FXML
    private void handleSales() {
        openTab("销售管理", "sales-view.fxml");
    }

    @FXML
    private void handleBorrowingStats() {
        openTab("借阅统计", "borrowing-stats-view.fxml");
    }

    @FXML
    private void handleSalesStats() {
        openTab("销售统计", "sales-stats-view.fxml");
    }

    private void openTab(String title, String fxmlFile) {
        try {
            // 检查是否已存在相同标题的标签页
            for (Tab tab : mainTabPane.getTabs()) {
                if (tab.getText().equals(title)) {
                    mainTabPane.getSelectionModel().select(tab);
                    return;
                }
            }

            // 创建新标签页
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }
            
            Tab tab = new Tab(title);
            tab.setContent(loader.load());
            mainTabPane.getTabs().add(tab);
            mainTabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            e.printStackTrace();
            // 可以在这里添加错误提示对话框
            showError("加载失败", "无法加载界面: " + fxmlFile + "\n" + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
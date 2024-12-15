package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BorrowingDAO;
import com.yumefusaka.bigwork.model.Borrowing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BorrowingController {
    @FXML
    private ComboBox<String> searchTypeComboBox;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Borrowing> borrowingTable;
    
    @FXML
    private TableColumn<Borrowing, Integer> idColumn;
    
    @FXML
    private TableColumn<Borrowing, String> customerNameColumn;
    
    @FXML
    private TableColumn<Borrowing, String> itemTypeColumn;
    
    @FXML
    private TableColumn<Borrowing, String> itemTitleColumn;
    
    @FXML
    private TableColumn<Borrowing, LocalDate> borrowDateColumn;
    
    @FXML
    private TableColumn<Borrowing, LocalDate> returnDateColumn;
    
    @FXML
    private TableColumn<Borrowing, String> statusColumn;

    private BorrowingDAO borrowingDAO;
    private ObservableList<Borrowing> borrowingList;

    @FXML
    private void initialize() {
        borrowingDAO = new BorrowingDAO();
        borrowingList = FXCollections.observableArrayList();
        
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
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 设置日期格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        borrowDateColumn.setCellFactory(column -> new TableCell<Borrowing, LocalDate>() {
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
        returnDateColumn.setCellFactory(column -> new TableCell<Borrowing, LocalDate>() {
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

        // 设置表格数据
        borrowingTable.setItems(borrowingList);
        
        // 加载数据
        loadBorrowings();
    }

    private void loadBorrowings() {
        try {
            borrowingList.clear();
            borrowingList.addAll(borrowingDAO.findAll());
        } catch (SQLException e) {
            showError("加载借阅数据失败", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchType = searchTypeComboBox.getValue();
        String keyword = searchField.getText().trim();
        
        try {
            List<Borrowing> searchResults;
            
            if (keyword.isEmpty()) {
                // 如果搜索关键词为空，显示所有记录
                searchResults = borrowingDAO.findAll();
            } else {
                searchResults = borrowingList.stream()
                    .filter(borrowing -> {
                        switch (searchType) {
                            case "客户姓名":
                                return borrowing.getCustomerName().toLowerCase()
                                    .contains(keyword.toLowerCase());
                            case "图书/期刊名":
                                return borrowing.getItemTitle().toLowerCase()
                                    .contains(keyword.toLowerCase());
                            default: // "全部"
                                return borrowing.getCustomerName().toLowerCase()
                                        .contains(keyword.toLowerCase()) ||
                                    borrowing.getItemTitle().toLowerCase()
                                        .contains(keyword.toLowerCase());
                        }
                    })
                    .collect(Collectors.toList());
            }
            
            borrowingList.clear();
            borrowingList.addAll(searchResults);
        } catch (SQLException e) {
            showError("搜索失败", e.getMessage());
        }
    }

    @FXML
    private void handleBorrow() {
        try {
            // 加载借阅对话框FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/borrowing-dialog.fxml"));
            if (loader.getLocation() == null) {
                throw new IllegalStateException("无法找到FXML文件: /fxml/borrowing-dialog.fxml");
            }
            
            Scene scene = new Scene(loader.load());
            
            // 创建对话框窗口
            Stage dialogStage = new Stage();
            dialogStage.setTitle("新增借阅");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(borrowingTable.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.setResizable(false); // 禁止调整窗口大小
            
            // 获取对话框控制器
            BorrowingDialogController controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("无法获取对话框控制器");
            }
            controller.setDialogStage(dialogStage);
            
            // 显示对话框并等待用户响应
            dialogStage.showAndWait();
            
            // 如果用户点击了保存按钮，刷新借阅列表
            if (controller.isSaveClicked()) {
                loadBorrowings();
                
                // 显示成功消息
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("借阅记录已成功添加！");
                alert.showAndWait();
            }
        } catch (Exception e) {
            showError("打开借阅对话框失败", "错误详情: " + e.getMessage());
            e.printStackTrace(); // 在控制台打印详细错误信息
        }
    }

    @FXML
    private void handleReturn() {
        Borrowing selectedBorrowing = borrowingTable.getSelectionModel().getSelectedItem();
        if (selectedBorrowing == null) {
            showError("错误", "请选择要归还的记录");
            return;
        }

        if (!"BORROWED".equals(selectedBorrowing.getStatus())) {
            showError("错误", "该记录已归还");
            return;
        }

        // 显示确认对话框
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认归还");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText(String.format("确认归还以下项目？\n\n客户：%s\n项目：%s",
                selectedBorrowing.getCustomerName(),
                selectedBorrowing.getItemTitle()));

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                selectedBorrowing.setReturnDate(LocalDate.now());
                selectedBorrowing.setStatus("RETURNED");
                borrowingDAO.update(selectedBorrowing);
                loadBorrowings();
                
                // 显示成功消息
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("成功");
                successAlert.setHeaderText(null);
                successAlert.setContentText("归还成功！");
                successAlert.showAndWait();
            } catch (SQLException e) {
                showError("归还失败", e.getMessage());
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
} 
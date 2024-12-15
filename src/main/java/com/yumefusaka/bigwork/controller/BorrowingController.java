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
        // TODO: 实现搜索功能
    }

    @FXML
    private void handleBorrow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowing-dialog.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("新增借阅");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(borrowingTable.getScene().getWindow());
            dialogStage.setScene(scene);
            
            BorrowingDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isSaveClicked()) {
                loadBorrowings();
            }
        } catch (Exception e) {
            showError("打开借阅对话框失败", e.getMessage());
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

        try {
            selectedBorrowing.setReturnDate(LocalDate.now());
            selectedBorrowing.setStatus("RETURNED");
            borrowingDAO.update(selectedBorrowing);
            loadBorrowings();
        } catch (SQLException e) {
            showError("归还失败", e.getMessage());
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
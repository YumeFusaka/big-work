package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BookDAO;
import com.yumefusaka.bigwork.dao.CustomerDAO;
import com.yumefusaka.bigwork.dao.MagazineDAO;
import com.yumefusaka.bigwork.dao.BorrowingDAO;
import com.yumefusaka.bigwork.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class BorrowingDialogController {
    @FXML
    private ComboBox<Customer> customerComboBox;
    
    @FXML
    private ComboBox<String> itemTypeComboBox;
    
    @FXML
    private ComboBox<Publication> itemComboBox;
    
    @FXML
    private DatePicker borrowDatePicker;

    private Stage dialogStage;
    private boolean saveClicked = false;
    private CustomerDAO customerDAO = new CustomerDAO();
    private BookDAO bookDAO = new BookDAO();
    private MagazineDAO magazineDAO = new MagazineDAO();
    private BorrowingDAO borrowingDAO = new BorrowingDAO();

    @FXML
    private void initialize() {
        // 初始化类型选项
        itemTypeComboBox.setItems(FXCollections.observableArrayList("图书", "期刊"));
        itemTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadItems();
        });
        
        // 设置默认日期为今天
        borrowDatePicker.setValue(LocalDate.now());
        
        // 设置借阅项目的显示格式
        itemComboBox.setButtonCell(new ListCell<Publication>() {
            @Override
            protected void updateItem(Publication item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });
        
        itemComboBox.setCellFactory(param -> new ListCell<Publication>() {
            @Override
            protected void updateItem(Publication item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });
        
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

    private void loadItems() {
        try {
            if ("图书".equals(itemTypeComboBox.getValue())) {
                itemComboBox.setItems(FXCollections.observableArrayList(bookDAO.findAll()));
            } else if ("期刊".equals(itemTypeComboBox.getValue())) {
                itemComboBox.setItems(FXCollections.observableArrayList(magazineDAO.findAll()));
            }
        } catch (SQLException e) {
            showError("加载数据失败", e.getMessage());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                Borrowing borrowing = new Borrowing();
                borrowing.setCustomerId(customerComboBox.getValue().getId());
                borrowing.setItemType("图书".equals(itemTypeComboBox.getValue()) ? "BOOK" : "MAGAZINE");
                borrowing.setItemId(((Publication)itemComboBox.getValue()).getId());
                borrowing.setBorrowDate(borrowDatePicker.getValue());
                borrowing.setStatus("BORROWED");
                
                borrowingDAO.insert(borrowing);
                
                saveClicked = true;
                dialogStage.close();
            } catch (SQLException e) {
                showError("保存失败", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (customerComboBox.getValue() == null) {
            errorMessage.append("请选择客户！\n");
        }

        if (itemTypeComboBox.getValue() == null) {
            errorMessage.append("请选择类型！\n");
        }

        if (itemComboBox.getValue() == null) {
            errorMessage.append("请选择图书/期刊！\n");
        }

        if (borrowDatePicker.getValue() == null) {
            errorMessage.append("请选择借阅日期！\n");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showError("输入错误", errorMessage.toString());
            return false;
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
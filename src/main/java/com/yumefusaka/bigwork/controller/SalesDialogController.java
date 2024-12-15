package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.BookDAO;
import com.yumefusaka.bigwork.dao.CustomerDAO;
import com.yumefusaka.bigwork.dao.MagazineDAO;
import com.yumefusaka.bigwork.dao.SaleDAO;
import com.yumefusaka.bigwork.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class SalesDialogController {
    @FXML
    private ComboBox<Customer> customerComboBox;
    
    @FXML
    private ComboBox<String> itemTypeComboBox;
    
    @FXML
    private ComboBox<Publication> itemComboBox;
    
    @FXML
    private TextField quantityField;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private TextField totalPriceField;
    
    @FXML
    private DatePicker saleDatePicker;

    private Stage dialogStage;
    private boolean saveClicked = false;
    private CustomerDAO customerDAO = new CustomerDAO();
    private BookDAO bookDAO = new BookDAO();
    private MagazineDAO magazineDAO = new MagazineDAO();
    private SaleDAO saleDAO = new SaleDAO();

    @FXML
    private void initialize() {
        // 初始化类型选项
        itemTypeComboBox.setItems(FXCollections.observableArrayList("图书", "期刊"));
        itemTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadItems();
        });
        
        // 设置默认日期为今天
        saleDatePicker.setValue(LocalDate.now());
        
        // 加载客户数据
        loadCustomers();
        
        // 监听商品选择变化
        itemComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                priceField.setText(String.format("%.2f", newVal.getPrice()));
                updateTotalPrice();
            }
        });
        
        // 监听数量变化
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateTotalPrice();
        });

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

        // 设置数量输入限制
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                quantityField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // 设置价格和总价格式化
        priceField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*\\.?\\d{0,2}")) {
                return change;
            }
            return null;
        }));

        totalPriceField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*\\.?\\d{0,2}")) {
                return change;
            }
            return null;
        }));
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

    private void updateTotalPrice() {
        try {
            if (itemComboBox.getValue() != null && !quantityField.getText().isEmpty()) {
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                
                // 检查数量是否超过库存
                Publication selectedItem = itemComboBox.getValue();
                if (quantity > selectedItem.getAvailableQuantity()) {
                    quantityField.setStyle("-fx-text-fill: red;");
                } else {
                    quantityField.setStyle("");
                }
                
                // 计算并显示总价
                totalPriceField.setText(String.format("%.2f", price * quantity));
            } else {
                totalPriceField.setText("");
            }
        } catch (NumberFormatException e) {
            totalPriceField.setText("");
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
                // 获取选中的商品和数量
                Publication selectedItem = itemComboBox.getValue();
                int quantity = Integer.parseInt(quantityField.getText());
                
                // 检查库存
                if (quantity > selectedItem.getAvailableQuantity()) {
                    showError("库存不足", String.format("当前库存：%d，无法销售%d个",
                        selectedItem.getAvailableQuantity(), quantity));
                    return;
                }
                
                // 创建销售记录
                Sale sale = new Sale();
                sale.setCustomerId(customerComboBox.getValue().getId());
                sale.setItemType("图书".equals(itemTypeComboBox.getValue()) ? "BOOK" : "MAGAZINE");
                sale.setItemId(selectedItem.getId());
                sale.setQuantity(quantity);
                sale.setTotalPrice(Double.parseDouble(totalPriceField.getText()));
                sale.setSaleDate(saleDatePicker.getValue());
                
                // 更新库存
                selectedItem.setTotalQuantity(selectedItem.getTotalQuantity() - quantity);
                selectedItem.setAvailableQuantity(selectedItem.getAvailableQuantity() - quantity);
                
                // 保存销售记录和更新库存
                if ("图书".equals(itemTypeComboBox.getValue())) {
                    bookDAO.update((Book)selectedItem);
                } else {
                    magazineDAO.update((Magazine)selectedItem);
                }
                saleDAO.insert(sale);
                
                saveClicked = true;
                dialogStage.close();
            } catch (SQLException e) {
                showError("保存失败", e.getMessage());
                e.printStackTrace();
            } catch (NumberFormatException e) {
                showError("输入错误", "请输入有效的数字");
                e.printStackTrace();
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
            errorMessage.append("请选择商品类型！\n");
        }

        if (itemComboBox.getValue() == null) {
            errorMessage.append("请选择商品！\n");
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                errorMessage.append("数量必须大于0！\n");
            }
            
            Publication selectedItem = itemComboBox.getValue();
            if (selectedItem != null && quantity > selectedItem.getAvailableQuantity()) {
                errorMessage.append(String.format("库存不足！当前库存：%d\n", 
                    selectedItem.getAvailableQuantity()));
            }
        } catch (NumberFormatException e) {
            errorMessage.append("请输入有效的数量！\n");
        }

        if (saleDatePicker.getValue() == null) {
            errorMessage.append("请选择销售日期！\n");
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

    // 添加一个方法来显示成功消息
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
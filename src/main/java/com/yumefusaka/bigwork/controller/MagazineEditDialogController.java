package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.dao.MagazineCategoryDAO;
import com.yumefusaka.bigwork.dao.PublisherDAO;
import com.yumefusaka.bigwork.model.Magazine;
import com.yumefusaka.bigwork.model.MagazineCategory;
import com.yumefusaka.bigwork.model.Publisher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MagazineEditDialogController {
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<Publisher> publisherComboBox;
    @FXML
    private ComboBox<MagazineCategory> categoryComboBox;
    @FXML
    private TextField totalQuantityField;
    @FXML
    private TextField availableQuantityField;
    @FXML
    private TextField priceField;

    private Stage dialogStage;
    private Magazine magazine;
    private boolean okClicked = false;
    private PublisherDAO publisherDAO = new PublisherDAO();
    private MagazineCategoryDAO categoryDAO = new MagazineCategoryDAO();

    @FXML
    private void initialize() {
        try {
            // 加载出版社和类别数据
            publisherComboBox.getItems().addAll(publisherDAO.findAll());
            categoryComboBox.getItems().addAll(categoryDAO.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMagazine(Magazine magazine) {
        this.magazine = magazine;
        titleField.setText(magazine.getTitle());
        totalQuantityField.setText(String.valueOf(magazine.getTotalQuantity()));
        availableQuantityField.setText(String.valueOf(magazine.getAvailableQuantity()));
        priceField.setText(String.valueOf(magazine.getPrice()));

        // 设置选中的出版社和类别
        for (Publisher publisher : publisherComboBox.getItems()) {
            if (publisher.getId() == magazine.getPublisherId()) {
                publisherComboBox.setValue(publisher);
                break;
            }
        }
        for (MagazineCategory category : categoryComboBox.getItems()) {
            if (category.getId() == magazine.getCategoryId()) {
                categoryComboBox.setValue(category);
                break;
            }
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            magazine.setTitle(titleField.getText());
            magazine.setPublisherId(publisherComboBox.getValue().getId());
            magazine.setCategoryId(categoryComboBox.getValue().getId());
            magazine.setTotalQuantity(Integer.parseInt(totalQuantityField.getText()));
            magazine.setAvailableQuantity(Integer.parseInt(availableQuantityField.getText()));
            magazine.setPrice(Double.parseDouble(priceField.getText()));
            
            // 设置显示名称
            magazine.setPublisherName(publisherComboBox.getValue().getName());
            magazine.setCategoryName(categoryComboBox.getValue().getName());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage += "期刊名不能为空！\n";
        }
        if (publisherComboBox.getValue() == null) {
            errorMessage += "请选择出版社！\n";
        }
        if (categoryComboBox.getValue() == null) {
            errorMessage += "请选择类别！\n";
        }

        try {
            int totalQuantity = Integer.parseInt(totalQuantityField.getText());
            if (totalQuantity < 0) {
                errorMessage += "总数量必须大于等于0！\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "总数量必须是有效的整数！\n";
        }

        try {
            int availableQuantity = Integer.parseInt(availableQuantityField.getText());
            if (availableQuantity < 0) {
                errorMessage += "可用数量必须大于等于0！\n";
            }
            if (availableQuantity > Integer.parseInt(totalQuantityField.getText())) {
                errorMessage += "可用数量不能大于总数量！\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "可用数量必须是有效的整数！\n";
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                errorMessage += "价格必须大于等于0！\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "价格必须是有效的数字！\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("输入错误");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
} 
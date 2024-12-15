package com.yumefusaka.bigwork.controller;

import com.yumefusaka.bigwork.model.MagazineCategory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MagazineCategoryEditDialogController {
    @FXML
    private TextField nameField;
    
    private Stage dialogStage;
    private MagazineCategory category;
    private boolean okClicked = false;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setCategory(MagazineCategory category) {
        this.category = category;
        nameField.setText(category.getName());
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            category.setName(nameField.getText());
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
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "类别名称不能为空！\n";
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
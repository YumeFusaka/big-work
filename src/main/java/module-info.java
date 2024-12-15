module com.yumefusaka.bigwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.yumefusaka.bigwork to javafx.fxml;
    opens com.yumefusaka.bigwork.controller to javafx.fxml;
    opens com.yumefusaka.bigwork.model to javafx.base;
    
    exports com.yumefusaka.bigwork;
    exports com.yumefusaka.bigwork.controller;
    exports com.yumefusaka.bigwork.model;
}
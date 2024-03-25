module com.MDLab3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;


    opens com.MDLab3 to javafx.fxml;
    exports com.MDLab3;
}
module com.example.MDLab2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens com.example.MDLab2 to javafx.fxml;
    exports com.example.MDLab2;
}
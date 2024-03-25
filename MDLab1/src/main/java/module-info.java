module com.example.pr1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.MDLab1 to javafx.fxml;
    exports com.example.MDLab1;
}
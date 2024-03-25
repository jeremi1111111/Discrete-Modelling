module com.mdlab4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires annotations;


    opens com.MDLab4 to javafx.fxml;
    exports com.MDLab4;
}
module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens org.example.controllers to javafx.fxml;
    opens org.example.application.customer to javafx.base;
    exports org.example;
}
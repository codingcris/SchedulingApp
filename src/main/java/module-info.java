module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens org.example.controllers to javafx.fxml;
    exports org.example;
}
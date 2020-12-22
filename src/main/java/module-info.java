module edu.wgu.cristianreyes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens edu.wgu.cristianreyes.controllers to javafx.fxml;
    opens edu.wgu.cristianreyes.application.appointment to javafx.base;
    opens edu.wgu.cristianreyes.application.customer to javafx.base;
    exports edu.wgu.cristianreyes;
}
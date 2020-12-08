package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.application.appointment.Appointment;
import org.example.application.appointment.AppointmentDaoImpl;
import org.example.application.customer.Customer;

import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentOperation {
    public static enum Operations {
        ADD, MODIFY
    }

    private final int MAX_TITLE_LENGTH = 50;
    private final int MAX_DESCRIPTION_LENGTH = 50;
    private final int MAX_LOCATION_LENGTH = 50;
    private final int MAX_ID_LENGTH = 10;

    private ResourceBundle resources;
    private AppointmentDaoImpl appointmentsDb;

    @FXML
    private HBox startTimePicker, endTimePicker;
    @FXML
    private ToggleGroup startTimePeriod, endTimePeriod;
    @FXML
    private Button saveButton, cancelButton;
    @FXML
    private TextField idField, customerIdField, userIdField, titleField, descriptionField, locationField, typeField;
    @FXML
    private ComboBox<String> contactId;
    @FXML
    private DatePicker startDateField, endDateField;


    @FXML
    public void initialize() {
        setupTimePickers();

        startTimePeriod = new ToggleGroup();
        endTimePeriod = new ToggleGroup();

        setNumericOnly(customerIdField, userIdField);

        setFieldLength(customerIdField, MAX_ID_LENGTH);
        setFieldLength(userIdField, MAX_ID_LENGTH);
        setFieldLength(titleField, MAX_TITLE_LENGTH);
        setFieldLength(descriptionField, MAX_DESCRIPTION_LENGTH);
        setFieldLength(locationField, MAX_LOCATION_LENGTH);
        setFieldLength(typeField, MAX_DESCRIPTION_LENGTH);
    }

    private void setFieldLength(TextField field, int maxLength) {
        field.textProperty().addListener(((observableValue, s, t1) -> {
            if (t1.length() > maxLength)
                field.setText(s);
        }));
    }

    private void setNumericOnly(TextField ... fields) {
        for (TextField field : fields) {
            field.textProperty().addListener(((observableValue, s, t1) -> {
                if(t1.matches(".*\\D.*"))
                    field.setText(s);
            }));
        }
    }

    public void setCustomerDb(AppointmentDaoImpl appointmentsDb) {
    }

    private void setupTimePickers() {
        Spinner<Integer> startHour = new Spinner<>(1, 12, 12);
        Spinner<Integer> startMinute = new Spinner<>(0, 59, 0);

        Spinner<Integer> endHour = new Spinner<>(1, 12, 12);
        Spinner<Integer> endMinute = new Spinner<>(0, 59, 0);

        Spinner[] spinners = {startHour, startMinute, endHour, endMinute};

        for (Spinner spinner : spinners) {
            spinner.setPrefWidth(75);
        }

        StringConverter<Integer> minutesConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer integer) {
                return String.format("%02d", integer);
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        };

        startMinute.getValueFactory().setConverter(minutesConverter);
        endMinute.getValueFactory().setConverter(minutesConverter);

        startMinute.increment(); startMinute.decrement();
        endMinute.increment(); endMinute.decrement();

        Label startTimeSeparator = new Label(":");
        Label endTimeSeparator = new Label(":");

        startTimeSeparator.setFont(new Font("System", 18));
        endTimeSeparator.setFont(new Font("System", 18));

        startTimePicker.getChildren().addAll(startHour, startTimeSeparator, startMinute);
        endTimePicker.getChildren().addAll(endHour, endTimeSeparator, endMinute);
    }

    public void setOperation(Operations modify) {
    }

    public void setAppointment(Integer appointmentId) {
    }

    public void cancelOperation() {
        if(confirmCancel()) {
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    private boolean confirmCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resources.getString("cancel"));
        alert.setHeaderText(resources.getString("dataLostNotification"));
        alert.setContentText(resources.getString("confirmationQuestion"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    private Appointment getAppointment() {
        Integer id = null;
        String title, description, location, type,

        try {
            id = Integer.parseInt(idField.getText() ,10);
        } catch (NumberFormatException e) {}

        if (id != null) {
            return new Appointment(id, );
        } else {
            return new Appointment();
        }
    }

    @FXML
    public void addAppointment(){
        clearNotifications();
        if (validFields()) {
            appointmentsDb.addAppointment(getAppointment());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    private boolean validFields() {
        return false;
    }

    private void clearNotifications() {
    }
}

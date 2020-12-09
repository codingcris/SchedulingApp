package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.application.appointment.Appointment;
import org.example.application.appointment.AppointmentDaoImpl;

import javax.xml.stream.Location;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private TextField[] fields;

    @FXML
    private HBox startTimePicker, endTimePicker;
    @FXML
    private ToggleGroup startTimePeriod, endTimePeriod;
    @FXML
    private Button saveButton, cancelButton;
    @FXML
    private TextField idField, customerIdField, userIdField, titleField, descriptionField, locationField, typeField;
    @FXML
    private ComboBox<Integer> contactField;
    @FXML
    private DatePicker startDateField, endDateField;
    @FXML
    private VBox notificationsArea, startPeriodPicker, endPeriodPicker;
    @FXML
    private RadioButton startAm, startPm, endAm, endPm;
    private Spinner<Integer> startHour, startMinute, endHour, endMinute;


    @FXML
    public void initialize() {
        resources = ResourceBundle.getBundle("org.example.bundles.AppointmentOperationResources");
        setupTimePickers();

        fields = new TextField[]{idField, customerIdField, userIdField, titleField, descriptionField, locationField, typeField};

        setNumericOnly(customerIdField, userIdField);

        setFieldLength(customerIdField, MAX_ID_LENGTH);
        setFieldLength(userIdField, MAX_ID_LENGTH);
        setFieldLength(titleField, MAX_TITLE_LENGTH);
        setFieldLength(descriptionField, MAX_DESCRIPTION_LENGTH);
        setFieldLength(locationField, MAX_LOCATION_LENGTH);
        setFieldLength(typeField, MAX_DESCRIPTION_LENGTH);

        contactField.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer contactId) {
                if (contactId == null)
                    return "NONE";
                return appointmentsDb.getContactName(contactId);
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
    }

    public AppointmentDaoImpl getAppointmentsDb() {
        return appointmentsDb;
    }

    public void setAppointmentsDb(AppointmentDaoImpl appointmentsDb) {
        this.appointmentsDb = appointmentsDb;
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
        startHour = new Spinner<>(1, 12, 12);
        startMinute = new Spinner<>(0, 59, 0);
        endHour = new Spinner<>(1, 12, 12);
        endMinute = new Spinner<>(0, 59, 0);

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
        int userId, customerId, contactId;
        String title, description, location, type;
        LocalDateTime start;
        LocalDateTime end;

        try {
            id = Integer.parseInt(idField.getText() ,10);
        } catch (NumberFormatException e) {}

          userId = Integer.parseInt(userIdField.getText());
          contactId = contactField.getValue() == null ? 1 : contactField.getValue();
          customerId = Integer.parseInt(customerIdField.getText());

          title = titleField.getText();
          description = descriptionField.getText();
          location = locationField.getText();
          type = typeField.getText();

          start = LocalDateTime.of(startDateField.getValue(), getStartTime());
          end = LocalDateTime.of(endDateField.getValue(), getEndTime());

        if (id != null) {
            return new Appointment(id,
                                   customerId,
                                   userId,
                                   contactId,
                                   title,
                                   description,
                                   location,
                                   type,
                                   start,
                                   end);
        } else {
            return new Appointment( customerId,
                                    userId,
                                    contactId,
                                    title,
                                    description,
                                    location,
                                    type,
                                    start,
                                    end);


        }

    }

    private LocalTime getStartTime() {
        int hour = startHour.getValue();
        if (startTimePeriod.getSelectedToggle() == startAm) {
            hour = hour < 12 ? hour : 0;
        } else if (startTimePeriod.getSelectedToggle() == startPm) {
            hour = hour < 12 ? hour + 12 : 12;
        }

        int minute = startMinute.getValue();

        return LocalTime.of(hour, minute);
    }

    private LocalTime getEndTime() {
        int hour = endHour.getValue();
        if (endTimePeriod.getSelectedToggle() == endAm) {
            hour = hour < 12 ? hour : 0;
        } else if (endTimePeriod.getSelectedToggle() == endPm) {
            hour = hour < 12 ? hour + 12 : 12;
        }

        int minute = endMinute.getValue();

        return LocalTime.of(hour, minute);
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
        return noEmptyFields() && validIds();
    }

    private boolean validIds() {
        int userId, customerId;
        boolean validUser, validCustomer;

        userId = Integer.parseInt(userIdField.getText());
        customerId = Integer.parseInt(customerIdField.getText());

        validUser = appointmentsDb.validUser(userId);
        validCustomer = appointmentsDb.validCustomer(customerId);

        if (!validUser)
            invalidUserIdAlert();
        if (!validCustomer)
            invalidCustomerIdAlert();

        return validUser && validCustomer;
    }

    private void invalidCustomerIdAlert() {
        new Alert(Alert.AlertType.ERROR, "The Customer ID entered for this appointment does not match a Customer in the database. Please enter a valid Customer ID.").show();
    }

    private void invalidUserIdAlert() {
        new Alert(Alert.AlertType.ERROR, "The User ID entered for this appointment does not match a User in the database. Please enter a valid User ID.").show();
    }

    private boolean noEmptyFields() {
        boolean emptyField = false;

        for(TextField field : fields) {
            if(field.getText().trim().isEmpty()) {
                highlightEmptyField(field);
                emptyField = true;
            } else {
                field.setStyle(null);
            }
        }

        if(startDateField.getValue() == null) {
            highlightEmptyField(startDateField);
            emptyField = true;
        } else {
            startDateField.setStyle(null);
        }

        if(endDateField.getValue() == null) {
            highlightEmptyField(endDateField);
            emptyField = true;
        } else {
            endDateField.setStyle(null);
        }

        if (emptyField)
            displayBlankFieldError();

        if (startTimePeriod.getSelectedToggle() == null) {
            emptyField = true;
            highlightEmptyField(startPeriodPicker);
        } else {
            startPeriodPicker.setStyle(null);
        }

        if (endTimePeriod.getSelectedToggle() == null) {
            emptyField = true;
            highlightEmptyField(endPeriodPicker);
        } else {
            endPeriodPicker.setStyle(null);
        }

        return !emptyField;
    }

    private void displayBlankFieldError() {
        final Label notification = new Label(resources.getString("noEmptyFields"));
        notification.setWrapText(true);
        notification.setStyle("-fx-background-color: rgba(255, 0 , 0, 0.5);");
        notification.setPrefWidth(notification.getMaxWidth());
        notification.setId("notification");


        notificationsArea.getChildren().add(notification);
    }

    private void highlightEmptyField(Node field) {
        field.setStyle("-fx-border-color: red");
    }

    private void clearNotifications() {
        notificationsArea.getChildren().removeIf(node -> node.getId() == "notification");
    }
}

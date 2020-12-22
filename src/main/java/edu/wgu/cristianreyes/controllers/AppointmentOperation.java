package edu.wgu.cristianreyes.controllers;

import edu.wgu.cristianreyes.application.appointment.AppointmentDaoImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import edu.wgu.cristianreyes.application.appointment.Appointment;
import edu.wgu.cristianreyes.application.contact.Contact;
import java.time.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the Appointment Operation window used in adding and updating appointments.
 */
public class AppointmentOperation {

    /**
     * An inner enum type used to specify the type of appointment operation being performed: adding or updating an appointment.
     */
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
    private Operations operation;
    private Appointment appointment;
    private ArrayList<Contact> contacts;

    @FXML
    private Label windowHeaderLabel, appointmentIdLabel, customerIdLabel, userIdLabel, titleLabel, typeLabel, descriptionLabel,
                  locationLabel, contactLabel, startDateLabel, endDateLabel, startTimeLabel, endTimeLabel, timeFormat;
    @FXML
    private HBox startTimePicker, endTimePicker;
    @FXML
    private Button saveButton, cancelButton;
    @FXML
    private TextField idField, customerIdField, userIdField, titleField, descriptionField, locationField, typeField,
            startHourField, startMinuteField, endHourField, endMinuteField;
    @FXML
    private ComboBox<Contact> contactField;
    @FXML
    private DatePicker startDateField, endDateField;
    @FXML
    private VBox notificationsArea;
    @FXML
    private RadioButton startAm, startPm, endAm, endPm;


    /**
     * Controller initialization method.
     */
    public void initialize() {
        resources = ResourceBundle.getBundle("edu.wgu.cristianreyes.bundles.AppointmentOperationResources");

        fields = new TextField[]{idField, customerIdField, userIdField, titleField, descriptionField, locationField, typeField, startHourField, startMinuteField, endHourField, endMinuteField};

        setNumericOnly(customerIdField, userIdField, startHourField, startMinuteField, endHourField, endMinuteField);

        setFieldLength(customerIdField, MAX_ID_LENGTH);
        setFieldLength(userIdField, MAX_ID_LENGTH);
        setFieldLength(titleField, MAX_TITLE_LENGTH);
        setFieldLength(descriptionField, MAX_DESCRIPTION_LENGTH);
        setFieldLength(locationField, MAX_LOCATION_LENGTH);
        setFieldLength(typeField, MAX_DESCRIPTION_LENGTH);
        setFieldLength(startHourField, 2);
        setFieldLength(startMinuteField, 2);
        setFieldLength(endHourField, 2);
        setFieldLength(endMinuteField, 2);

        contactField.setItems(FXCollections.observableArrayList(appointmentsDb.getAllContacts()));
        contactField.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                if (contact == null) {
                    return null;
                }
                return contact.getName();
            }

            @Override
            public Contact fromString(String s) {
                return null;
            }
        });

        contacts = appointmentsDb.getAllContacts();


    }

    /**
     * Sets the value of appointmentsDb field which is used to interact with the database storing appointment data.
     * @param appointmentsDb an AppointmentDaoImpl instance.
     */
    public void setAppointmentsDb(AppointmentDaoImpl appointmentsDb) {
        this.appointmentsDb = appointmentsDb;
    }

    /**
     * Sets the max length of text entered in the given TextField to the given maxLength.
     * @param field the TextField we are applying a max length constraint to.
     * @param maxLength the maximum number of characters that can be entered in field.
     */
    private void setFieldLength(TextField field, int maxLength) {
        field.textProperty().addListener(((observableValue, s, t1) -> {
            if (t1.length() > maxLength)
                field.setText(s);
        }));
    }

    /**
     * For each passed in TextField, ensures that only numeric characters may be entered into the TextField.
     * @param fields varargs list of TextFields
     */
    private void setNumericOnly(TextField ... fields) {
        for (TextField field : fields) {
            field.textProperty().addListener(((observableValue, s, t1) -> {
                if(t1.matches(".*\\D.*"))
                    field.setText(s);
            }));
        }
    }

    /**
     * Sets the operation field to an Operations enum. This field dictates the behavior of various methods in the controller.
     * @param op Operations enum type specifying the type of appointment operation being performed: adding or modifying.
     * Lambdas are used within this method allowing single line statements for setting a control's action. This makes the code within more readable.
     */
    public void setOperation(Operations op) {
        operation = op;

        if(op == Operations.ADD) {
            saveButton.setOnAction((e) -> addAppointment());
            windowHeaderLabel.setText(resources.getString("addAppointment"));
        } else if (op == Operations.MODIFY) {
            saveButton.setOnAction((e) -> modifyAppointment());
            windowHeaderLabel.setText(resources.getString("modifyAppointment"));
        }

        applyResources();
    }

    /**
     * Applies language resources from the ResourceBundle in the resources field to the appropriate labels and controls.
     */
    private void applyResources() {
        appointmentIdLabel.setText(resources.getString("appointmentId"));
        customerIdLabel.setText(resources.getString("customerId"));
        userIdLabel.setText(resources.getString("userId"));
        titleLabel.setText(resources.getString("title"));
        typeLabel.setText(resources.getString("type"));
        descriptionLabel.setText(resources.getString("description"));
        locationLabel.setText(resources.getString("location"));
        contactLabel.setText(resources.getString("contact"));
        startDateLabel.setText(resources.getString("startDate"));
        endDateLabel.setText(resources.getString("endDate"));
        startTimeLabel.setText(resources.getString("startTime"));
        endTimeLabel.setText(resources.getString("endTime"));
        timeFormat.setText(resources.getString("timeFormat"));
        saveButton.setText(resources.getString("save"));
        cancelButton.setText(resources.getString("cancel"));
        idField.setText(resources.getString("autoGenerated"));
    }

    /**
     * Verifies all fields are valid, updates the selected appointment, and closes the appointment operation window..
     */
    private void modifyAppointment() {
        clearNotifications();
        if (validFields()) {
            appointmentsDb.updateAppointment(getAppointment());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    /**
     * Receives the appointment ID of the appointment to be modified in this window. Populates all form fields with the
     * appointment's current data. A lambda within replaces a concrete implementation of the Predicate interface and allows
     * for more concise code when filtering a stream.
     * @param appointmentId the Appointment ID of an appointment to be modified.
     */
    public void setAppointment(Integer appointmentId) {
        appointment = appointmentsDb.getAppointment(appointmentId);
        LocalDateTime start = appointment.getStart().toLocalDateTime();
        LocalDateTime end = appointment.getEnd().toLocalDateTime();

        idField.setText("" + appointment.getAppointmentId());
        customerIdField.setText("" + appointment.getCustomerId());
        userIdField.setText("" + appointment.getUserId());
        titleField.setText(appointment.getTitle());
        descriptionField.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        typeField.setText(appointment.getType());
        startDateField.setValue(appointment.getStart().toLocalDate());
        endDateField.setValue(appointment.getEnd().toLocalDate());

        int startHourVal, endHourVal, startMinVal, endMinVal;
//
        startHourVal = start.getHour();
        startMinVal = start.getMinute();

        endHourVal = end.getHour();
        endMinVal = end.getMinute();

        startHourField.setText("" + startHourVal);
        startMinuteField.setText("" + startMinVal);
        endHourField.setText("" + endHourVal);
        endMinuteField.setText("" + endMinVal);

        contactField.setValue(contacts.stream().filter((contact -> contact.getId() == appointment.getContactId())).findFirst().get());
    }

    /**
     * On action for the cancel button, confirms the cancel and closes the appointment operation window.
     */
    public void cancelOperation() {
        if(confirmCancel()) {
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    /**
     * A confirmation alert confirms that the user wants to cancel the appointment operation.
     * @return true if user selects OK on the confirmation alert, returns false if user selects Cancel.
     */
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

    /**
     * Initializes and returns an Appointment instance with the data entered in the window's fields.
     * @return Appointment instance containing the data entered in the appointment operation form.
     */
    private Appointment getAppointment() {
        Integer id = null;
        int userId, customerId, contactId;
        String title, description, location, type;
        ZonedDateTime start;
        ZonedDateTime end;

        try {
            id = Integer.parseInt(idField.getText() ,10);
        } catch (NumberFormatException e) {}

          userId = Integer.parseInt(userIdField.getText());
          contactId = contactField.getValue().getId();
          customerId = Integer.parseInt(customerIdField.getText());

          title = titleField.getText().trim();
          description = descriptionField.getText().trim();
          location = locationField.getText().trim();
          type = typeField.getText().trim();

          start = ZonedDateTime.of(startDateField.getValue(), getStartTime(), ZoneId.systemDefault());
          end = ZonedDateTime.of(endDateField.getValue(), getEndTime(), ZoneId.systemDefault());

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
        Integer startHour, startMinute;

        startHour = Integer.parseInt(startHourField.getText(), 10);
        startMinute = Integer.parseInt(startMinuteField.getText(), 10);

        return LocalTime.of(startHour, startMinute);
    }

    private LocalTime getEndTime() {
        Integer endHour, endMinute;

        endHour = Integer.parseInt(endHourField.getText(),10);
        endMinute = Integer.parseInt(endMinuteField.getText(), 10);

        return LocalTime.of(endHour, endMinute);
    }

    /**
     * Action for the saveButton when the operation field == Operations.ADD, verifies data in the form fields and saves a
     * new appointment to the database if valid. Closes the appointment operation window when complete.
     */
    private void addAppointment(){
        clearNotifications();
        if (validFields()) {
            appointmentsDb.addAppointment(getAppointment());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    /**
     * Employs various field verification methods.
     * @return true if all fields are valid, false if any fields fail verification.
     */
    private boolean validFields() {
        return noEmptyFields() && validIds() && validDates() && validTimes();
    }

    /**
     * Validates the start and end times for the appointment entered in the form fields. Appointment times must be within
     * work hours and may not overlap another appointment for the same customer. A lambda within replaces a concrete
     * implementation of the Predicate interface and allows for more concise code when filtering a stream.
     * @return true if appointment times are valid false if otherwise.
     */
    private boolean validTimes() {
        Integer startHour, startMinute, endHour, endMinute;

        startHour = Integer.parseInt(startHourField.getText(), 10);
        startMinute = Integer.parseInt(startMinuteField.getText(), 10);
        endHour = Integer.parseInt(endHourField.getText(), 10);
        endMinute = Integer.parseInt(endMinuteField.getText(), 10);

        if (!(startHour >= 0 && startHour < 24)) {
            highlightField(startHourField);
            displayInvalidHourError();
            return false;
        }

        if (!(endHour >= 0 && endHour < 24)) {
            highlightField(endHourField);
            displayInvalidHourError();
            return false;
        }

        if (!(startMinute >= 0 && startMinute < 60)) {
            highlightField(startMinuteField);
            displayInvalidMinuteError();
            return false;
        }
        if (!(endMinute >= 0 && endMinute < 60)) {
            highlightField(endMinuteField);
            displayInvalidMinuteError();
            return false;
        }

        Appointment appointment = getAppointment();

        if (!appointment.getEnd().isAfter(appointment.getStart())) {
            highlightField(startTimePicker);
            highlightField(endTimePicker);
            displayEndTimeBeforeStartTimeError();
            return false;
        } else {
            startTimePicker.setStyle(null);
            endTimePicker.setStyle(null);
        }

        if (!(withinWorkHours(getAppointment().getStart()) && withinWorkHours(getAppointment().getEnd()))) {
            highlightField(startTimePicker);
            highlightField(endTimePicker);
            displayOutsideOfWorkHoursError();
            return false;
        } else {
            endTimePicker.setStyle(null);
            startTimePicker.setStyle(null);
        }

        var overlappingAppointments = appointmentsDb.getCustomerAppointments(appointment.getCustomerId())
                                                            .stream()
                                                            .filter(customerAppointment ->  appointment.getAppointmentId() != customerAppointment.getAppointmentId() &&
                                                                                            customerAppointment.getCustomerId() == appointment.getCustomerId() &&
                                                                                            appointmentsOverlap(customerAppointment, appointment)).toArray();

        if (overlappingAppointments.length != 0) {
            displayOverlapingAppointmentError((Appointment) overlappingAppointments[0]);
            return false;
        }

        return true;
    }

    private void displayInvalidMinuteError() {
        new Alert(Alert.AlertType.ERROR, resources.getString("invalidMinuteError")).show();
    }

    private void displayInvalidHourError() {
        new Alert(Alert.AlertType.ERROR, resources.getString("invalidHourError")).show();
    }

    /**
     * Displays an error alert in the case that the appointment times in the form overlap an already existing appointment.
     * @param customerAppointment the existing appointment that overlaps the appointment being added or updated in the form.
     */
    private void displayOverlapingAppointmentError(Appointment customerAppointment) {
        new Alert(Alert.AlertType.ERROR, resources.getString("overlappingAppointmentError") + "\n" +
                                            resources.getString("appointmentId") + ": " + customerAppointment.getAppointmentId() + "\n" +
                                            resources.getString("appointmentStart") + ": " + customerAppointment.getStart() + "\n" +
                                            resources.getString("appointmentEnd") + ": " + customerAppointment.getEnd()).show();
    }

    /**
     * Checks if two appointment times overlap.
     * @param appointment1 an appointment
     * @param appointment2 another appointment
     * @return true if appointments share overlapping times, false otherwise.
     */
    private boolean appointmentsOverlap(Appointment appointment1, Appointment appointment2) {
        return !appointment2.getStart().isAfter(appointment1.getEnd()) && !appointment1.getStart().isAfter(appointment2.getEnd());
    }

    /**
     * Displays an error informing the user that the times entered in the form for the new or existing appointment are outside of business hours.
     */
    private void displayOutsideOfWorkHoursError() {
        new Alert(Alert.AlertType.ERROR, resources.getString("outsideOfWorkHours")).show();
    }

    /**
     * Ensures that an appointment date time falls within work hours.
     * @param appointmentDate the date and time of the appointment.
     * @return true if date time is within work hours, false otherwise.
     */
    private boolean withinWorkHours(ZonedDateTime appointmentDate) {
        final ZonedDateTime BUSINESS_HOURS_START = ZonedDateTime.of(appointmentDate.toLocalDate(), LocalTime.of(8, 0), ZoneId.of("US/Eastern"));
        final ZonedDateTime BUSINESS_HOURS_END = ZonedDateTime.of(appointmentDate.toLocalDate(), LocalTime.of(22, 0), ZoneId.of("US/Eastern"));

        return appointmentDate.isBefore(BUSINESS_HOURS_END) && (appointmentDate.isAfter(BUSINESS_HOURS_START) || appointmentDate.isEqual(BUSINESS_HOURS_START));
    }

    /**
     * Displays an error informing that the entered appointment end time is prior to the start time.
     */
    private void displayEndTimeBeforeStartTimeError() {
        new Alert(Alert.AlertType.ERROR, resources.getString("endTimeBeforeStartTimeError")).show();
    }

    /**
     * Validates the start and end dates entered in the appointment operation form.
     * @return true if dates are valid, false otherwise.
     */
    private boolean validDates() {
        boolean validDate = true;

        if (startDateField.getValue().isAfter(endDateField.getValue())) {
            validDate = false;
            highlightField(startDateField);
            highlightField(endDateField);
            displayStartAfterEndDateError();
        } else {
            startDateField.setStyle(null);
            endDateField.setStyle(null);
        }

        return validDate;
    }

    /**
     * Displays an error informing that the entered appointment end date is prior to the start date.
     */
    private void displayStartAfterEndDateError() {
        new Alert(Alert.AlertType.ERROR, resources.getString("endDateBeforeStartDateError")).show();
    }

    /**
     * Validates the customer and user ids entered in the form. Ids must correspond to valid users and customers.
     * Calls error display methods if ids are invalid.
     * @return true if customer and user ids are valid, false otherwise.
     *
     */
    private boolean validIds() {
        int userId, customerId;
        boolean validUser, validCustomer;

        userId = Integer.parseInt(userIdField.getText());
        customerId = Integer.parseInt(customerIdField.getText());

        validUser = appointmentsDb.validUser(userId);
        validCustomer = appointmentsDb.validCustomer(customerId);

        if (!validUser){
            invalidUserIdAlert();
            highlightField(userIdField);
        }
        if (!validCustomer) {
            invalidCustomerIdAlert();
            highlightField(customerIdField);
        }

        return validUser && validCustomer;
    }

    /**
     * Displays an error informing that the customer ID entered in the form does not correspond to an existing customer.
     */
    private void invalidCustomerIdAlert() {
        new Alert(Alert.AlertType.ERROR, resources.getString("invalidCustomerIdError")).show();
    }

    /**
     * Displays an error informing that the user ID entered in the form does not correspond to an existing user.
     */
    private void invalidUserIdAlert() {
        new Alert(Alert.AlertType.ERROR, resources.getString("invalidUserIdError")).show();
    }

    /**
     * Checks that all fields in the form are filled out, calling error display methods for any empty fields and returning
     * whether or not all fields are filled out.
     * @return true if all form fields are filled out, false if any form fields are empty.
     */
    private boolean noEmptyFields() {
        ArrayList<Node> emptyFields = new ArrayList<>();
        ArrayList<Node> validFields = new ArrayList<>();

        for(TextField field : fields) {
            if(field.getText().trim().isEmpty()) {
                emptyFields.add(field);
            } else {
                validFields.add(field);
            }
        }

        if(startDateField.getValue() == null) {
            emptyFields.add(startDateField);
        } else {
            validFields.add(startDateField);
        }

        if(endDateField.getValue() == null) {
            emptyFields.add(endDateField);
        } else {
            validFields.add(endDateField);
        }

        if (contactField.getValue() == null) {
            emptyFields.add(contactField);
        } else {
            validFields.add(contactField);
        }

        for (Node node: emptyFields) {
            highlightField(node);
        }

        for (Node node: validFields) {
            node.setStyle(null);
        }

        if (!emptyFields.isEmpty())
            displayBlankFieldError();

        return emptyFields.isEmpty();
    }

    /**
     * Displays an error alert informing that a field in the form is empty.
     */
    private void displayBlankFieldError() {
        final Label notification = new Label(resources.getString("noEmptyFields"));
        notification.setWrapText(true);
        notification.setStyle("-fx-background-color: rgba(255, 0 , 0, 0.5);");
        notification.setPrefWidth(notification.getMaxWidth());
        notification.setId("notification");


        notificationsArea.getChildren().add(notification);
    }

    /**
     * Sets a node's style to indicate an error.
     * @param field the Node in which there is an error.
     */
    private void highlightField(Node field) {
        field.setStyle("-fx-border-color: red");
    }


    /**
     * clears notifications from the notifications area.
     * A lambda within replaces a concrete implementation of the Predicate interface
     * and allows for a concise one line statement for the whole method.
     */
    private void clearNotifications() {
        notificationsArea.getChildren().removeIf(node -> node.getId() == "notification");
    }
}

package edu.wgu.cristianreyes.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import edu.wgu.cristianreyes.application.customer.Customer;
import edu.wgu.cristianreyes.application.customer.CustomerDao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the customer operation window used to add or update customers.
 */
public class CustomerOperation {
    /**
     * An inner enum type used to specify the type of customer operation being performed: adding or updating a customer.
     */
    public static enum Operations {
        ADD, MODIFY
    }

    private CustomerDao customerDb;
    private Customer customer;
    private ResourceBundle resources = ResourceBundle.getBundle("edu.wgu.cristianreyes.bundles.CustomerOperationResources");
    private Map<Integer, String> countries;
    private Map<Integer, String> firstLevelDivs;
    private final ArrayList<Integer> firstLevelDivOptionsForCountry = new ArrayList<>();
    private Operations operation;

    @FXML
    private TextField idField, nameField, phoneField, addressField, postalCodeField;
    @FXML
    private Label operationLabel, idLabel, nameLabel, phoneLabel, addressLabel, postalCodeLabel, countryLabel, stateLabel;
    @FXML
    private ComboBox countryComboBox;
    @FXML
    private ComboBox firstDivisionComboBox;
    @FXML
    private Button saveButton, cancelButton;
    @FXML
    private VBox notificationsArea;

    private TextField[] fields;

    private final int MAX_NAME_LENGTH = 75;
    private final int MAX_PHONE_LENGTH = 15;
    private final int MAX_ADDRESS_LENGTH = 256;
    private final int MAX_POSTAL_CODE_LENGTH = 10;

    /**
     * Controller initialization. Lambdas within this method make the code adding event listeners to various controls much more concise
     * than had a ChangeListener class been directly implemented.
     */
    public void initialize() {
        fields = new TextField[]{idField, nameField, phoneField, addressField, postalCodeField};

        setFieldAcceptableLength(nameField, MAX_NAME_LENGTH);
        setFieldAcceptableLength(phoneField, MAX_PHONE_LENGTH);
        setFieldAcceptableLength(addressField, MAX_ADDRESS_LENGTH);
        setFieldAcceptableLength(postalCodeField, MAX_POSTAL_CODE_LENGTH);

        setNumericOnly(phoneField, postalCodeField);


        countries = customerDb.getCountriesOfBusiness();
        countryComboBox.setItems(FXCollections.observableList(new ArrayList<>(countries.keySet())));

        countryComboBox.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer o) {
                if (o != null) {
                    return countries.get(o);
                }

                return "";
            }

            @Override
            public Integer fromString(String s) {return null;}
        });

        firstDivisionComboBox.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer o) {
                if(o != null) {
                    return firstLevelDivs.get(o);
                }
                return "";
            }

            @Override
            public Integer fromString(String s) {return null;}
        });

        countryComboBox.valueProperty().addListener(((observableValue, o, t1) -> {
            updateFirstDivisions((Integer) t1);
        }));


        nameField.textProperty().addListener(((observableValue, s, t1) -> {
            if(t1.length() > 71) {
                nameField.setText(s);
            }
        }));

        cancelButton.setOnAction(e -> cancelOperation());
    }

    /**
     *  On action for the cancel button, confirms the cancel and closes the customer operation window.
     */
    public void cancelOperation() {
        if(confirmCancel()) {
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    /**
     * A confirmation alert confirms that the user wants to cancel the customer operation.
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
     * Verifies all fields are valid, updates the selected customer, and closes the customer operation window..
     */
    private void modifyCustomer() {
        clearNotifications();
        if (validFields()) {
            customerDb.updateCustomer(getCustomer());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    /**
     * Employs various field verification methods.
     * @return true if all fields are valid, false if any fields fail verification.
     */
    private boolean validFields() {
        return noEmptyFields();
    }

    /**
     * Sets the operation field to an Operations enum. This field dictates the behavior of various methods in the controller.
     * @param op Operations enum type specifying the type of customer operation being performed: adding or modifying.
     * Lambdas are used within this method allowing single line statements for setting a control's action. This makes the code within more readable.
     */

    public void setOperation(Operations op) {
        operation = op;

        if(op == Operations.ADD) {
            saveButton.setOnAction((e) -> addCustomer());
        } else if (op == Operations.MODIFY) {
            saveButton.setOnAction((e) -> modifyCustomer());
        }

        applyResources();
    }

    /**
     * Applies language resources from the ResourceBundle in the resources field to the appropriate labels and controls.
     */
    private void applyResources() {
        switch (operation) {
            case ADD: operationLabel.setText(resources.getString("addCustomer"));
                      break;
            case MODIFY: operationLabel.setText(resources.getString("modifyCustomer"));
                         break;
        }

        idLabel.setText(resources.getString("id"));
        nameLabel.setText(resources.getString("name"));
        phoneLabel.setText(resources.getString("phoneNumber"));
        addressLabel.setText(resources.getString("address"));
        countryLabel.setText(resources.getString("country"));
        stateLabel.setText(resources.getString("state"));
        saveButton.setText(resources.getString("save"));
        cancelButton.setText(resources.getString("cancel"));
        idField.setText(resources.getString("autoGenerated"));
    }

    /**
     * Receives the customer ID of the customer to be modified in this window. Populates all form fields with the
     * customer's current data.
     * @param customerId the Customer ID of a customer to be modified.
     */
    public void setCustomer(int customerId) {
        customer = customerDb.getCustomer(customerId);

        idField.setText("" + customer.getId());
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        addressField.setText(customer.getAddress());
        postalCodeField.setText(customer.getPostalCode());
        countryComboBox.getSelectionModel().select(customerDb.getCountryIdFromFirstDivisionId(customer.getDivisionId()));
        firstDivisionComboBox.getSelectionModel().select(Integer.valueOf(customer.getDivisionId()));

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
     * Sets the max length of text entered in the given TextField to the given maxLength.
     * @param field the TextField we are applying a max length constraint to.
     * @param maxLength the maximum number of characters that can be entered in field.
     */
    private void setFieldAcceptableLength(TextField field, int maxLength) {
        field.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > maxLength)
                field.setText(s);
        });
    }

    /**
     * Updates the options for the firstDivisionComboBox depending on the countryId passed in.
     * @param countryId the countryId used to filter the possible first level division options.
     */
    private void updateFirstDivisions(Integer countryId) {
        firstDivisionComboBox.setValue(null);
        firstLevelDivOptionsForCountry.clear();
        firstLevelDivs = customerDb.getFirstLevelDivisionsByCountry(countryId);

        if(!firstLevelDivs.isEmpty()) {
            firstLevelDivOptionsForCountry.addAll(firstLevelDivs.keySet());

            firstDivisionComboBox.setItems(FXCollections.observableList(firstLevelDivOptionsForCountry));
            firstDivisionComboBox.setDisable(false);
        } else {
            firstDivisionComboBox.setDisable(true);
        }


    }

    /**
     * Sets the value of customerDb field which is used to interact with the database storing customer data.
     * @param customerDb a CustomerDaoImpl instance.
     */
    public void setCustomerDb(CustomerDao customerDb) {
        this.customerDb = customerDb;
    }

    /**
     * Initializes and returns a Customer instance with the data entered in the window's fields.
     * @return Customer instance containing the data entered in the customer operation form.
     */
    private Customer getCustomer() {
        Integer id = null;

        try {
            id = Integer.parseInt(idField.getText() ,10);
        } catch (NumberFormatException e) {}

        if (id != null) {
            return new Customer(id.intValue(),
                                nameField.getText(),
                                String.join(" ", addressField.getText()),
                                postalCodeField.getText(),
                                phoneField.getText(),
                                (int) firstDivisionComboBox.getValue());
        } else {
            return new Customer(nameField.getText(),
                                String.join(" ",addressField.getText()),
                                postalCodeField.getText(),
                                phoneField.getText(),
                                (int) firstDivisionComboBox.getValue());
        }
    }

    /**
     * Action for the saveButton when the operation field == Operations.ADD, verifies data in the form fields and saves a
     * new Customer to the database if valid. Closes the customer operation window when complete.
     */
    public void addCustomer(){
        clearNotifications();
        if (validFields()) {
            customerDb.addCustomer(getCustomer());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    /**
     * clears notifications from the notifications area. A lambda within replaces a concrete implementation of the Predicate interface
     * and allows for a concise one line statement for the whole method.
     */
    private void clearNotifications() {
        notificationsArea.getChildren().removeIf(node -> node.getId() == "notification");
    }

    /**
     * Checks that all fields in the form are filled out, calling error display methods for any empty fields and returning
     * whether or not all fields are filled out.
     * @return true if all form fields are filled out, false if any form fields are empty.
     */
    public boolean noEmptyFields() {
        ArrayList<Node> emptyFields = new ArrayList<>();
        ArrayList<Node> validFields = new ArrayList<>();

        for(TextField field : fields) {
            if(field.getText().trim().isEmpty()) {
                emptyFields.add(field);
            } else {
                validFields.add(field);
            }
        }

        if(countryComboBox.getValue() == null) {
            emptyFields.add(countryComboBox);
        } else {
            validFields.add(countryComboBox);
        }

        if(firstDivisionComboBox.getValue() == null) {
            emptyFields.add(firstDivisionComboBox);
        } else {
            validFields.add(firstDivisionComboBox);
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
     * Sets a node's style to indicate an error.
     * @param field the node in which there is an error.
     */
    private void highlightField(Node field) {
        field.setStyle("-fx-border-color: red;");
    }

    /**
     * Displays an error alert informing that a field in the form is empty.
     */
    private void displayBlankFieldError(){
        final Label notification = new Label(resources.getString("noEmptyFields"));
        notification.setWrapText(true);
        notification.setStyle("-fx-background-color: rgba(255, 0 , 0, 0.5);");
        notification.setPrefWidth(notification.getMaxWidth());
        notification.setId("notification");


        notificationsArea.getChildren().add(notification);
    }
}

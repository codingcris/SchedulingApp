package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.application.customer.Customer;
import org.example.application.customer.CustomerDao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerOperation {
    public static enum Operations {
        ADD, MODIFY
    }

    private CustomerDao customerDb;
    private Customer customer;
    private ResourceBundle resources = ResourceBundle.getBundle("org.example.bundles.CustomerOperationResources");
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
     * Controller initialization.
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

    private void modifyCustomer() {
        clearNotifications();
        if (validFields()) {
            customerDb.updateCustomer(getCustomer());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    public void setOperation(Operations op) {
        operation = op;

        if(op == Operations.ADD) {
            saveButton.setOnAction((e) -> addCustomer());
        } else if (op == Operations.MODIFY) {
            saveButton.setOnAction((e) -> modifyCustomer());
        }

        applyResources();
    }

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
    }

    public Operations getOperation() {
        return operation;
    }

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

    private void setNumericOnly(TextField ... fields) {
        for (TextField field : fields) {
            field.textProperty().addListener(((observableValue, s, t1) -> {
                if(t1.matches(".*\\D.*"))
                    field.setText(s);
            }));
        }
    }

    private void setFieldAcceptableLength(TextField field, int length) {
        field.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > length)
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

    public void setCustomerDb(CustomerDao customerDb) {
        this.customerDb = customerDb;
    }

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

    @FXML
    public void addCustomer(){
        clearNotifications();
        if (validFields()) {
            customerDb.addCustomer(getCustomer());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    private void clearNotifications() {
        notificationsArea.getChildren().removeIf(node -> node.getId() == "notification");
    }

    public boolean validFields() {
        boolean emptyField = false;

        for(TextField field : fields) {
            if(field.getText().trim().isEmpty()) {
                highlightEmptyField(field);
                emptyField = true;
            }
        }


        if(countryComboBox.getValue() == null) {
            highlightEmptyField(countryComboBox);
            emptyField = true;
        } else {
            countryComboBox.setStyle(null);
        }

        if(firstDivisionComboBox.getValue() == null) {
            highlightEmptyField(firstDivisionComboBox);
            emptyField = true;
        } else {
            firstDivisionComboBox.setStyle(null);
        }

        if (emptyField)
            displayBlankFieldError();

        return emptyField;
    }

    private void highlightEmptyField(Node field) {
        field.setStyle("-fx-border-color: red;");
    }

    private void displayBlankFieldError(){
        final Label notification = new Label("Text field can not be empty");
        notification.setWrapText(true);
        notification.setStyle("-fx-background-color: rgba(255, 0 , 0, 0.5);");
        notification.setId("notification");


        notificationsArea.getChildren().add(notification);
    }
}

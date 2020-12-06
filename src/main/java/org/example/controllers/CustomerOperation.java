package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.application.customer.Customer;
import org.example.application.customer.CustomerDao;

import java.util.ArrayList;
import java.util.Map;

public class CustomerOperation {
    public static enum Opertions {
        ADD, MODIFY
    }

    private CustomerDao customerDb;
    private Customer customer;
    private Map<Integer, String> countries;
    private Map<Integer, String> firstLevelDivs;
    private final ArrayList<Integer> firstLevelDivOptionsForCountry = new ArrayList<>();
    private Opertions operation;

    @FXML
    private TextField idField, nameField, phoneField, addressField, postalCodeField;
    @FXML
    private ComboBox countryComboBox;
    @FXML
    private ComboBox firstDivisionComboBox;
    @FXML
    private Button saveButton;

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

        if(getOperation() == Opertions.ADD) {
            saveButton.setOnAction((e) -> addCustomer());
        } else if (getOperation() == Opertions.MODIFY) {
            saveButton.setOnAction((e) -> modifyCustomer());
        }
    }

    private void modifyCustomer() {
        clearFieldsNotifications();
        if (validFields()) {
            customerDb.updateCustomer(getCustomer());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    public void setOperation(Opertions op) {
        operation = op;
    }

    public Opertions getOperation() {
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
        clearFieldsNotifications();
        if (validFields()) {
            customerDb.addCustomer(getCustomer());
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    private void clearFieldsNotifications() {
        for(TextField field : fields) {
            VBox parent = (VBox) field.getParent();
            var children = parent.getChildren();
            if(children.get(children.size() - 1) instanceof Label) {
                children.removeIf(n -> n.getId() == "notification");
            }
        }
    }

    public boolean validFields() {
        boolean emptyTextField = false;
        boolean emptyComboBox = false;

        for(TextField field : fields) {
            if(field.getText().trim().isEmpty()) {
                displayBlankFieldError(field);
                emptyTextField = true;
            }
        }


        if(countryComboBox.getValue() == null) {
            highlightUnselectedBox(countryComboBox);
            emptyComboBox = true;
        } else {
            countryComboBox.setStyle(null);
        }

        if(firstDivisionComboBox.getValue() == null) {
            highlightUnselectedBox(firstDivisionComboBox);
            emptyComboBox = true;
        } else {
            firstDivisionComboBox.setStyle(null);
        }

        if(emptyTextField || emptyComboBox)
            return false;

        return true;
    }

    private void highlightUnselectedBox(ComboBox comboBox) {
        comboBox.setStyle("-fx-border-color: red;");
    }

    private void displayBlankFieldError(TextField field){
        final Label notification = new Label("Text field can not be empty");
        notification.setWrapText(true);
        notification.setStyle("-fx-background-color: rgba(255, 0 , 0, 0.5);");
        notification.setId("notification");


        VBox parent = (VBox) field.getParent();
        parent.getChildren().add(notification);
        notification.setPrefWidth(field.getWidth());
    }
}

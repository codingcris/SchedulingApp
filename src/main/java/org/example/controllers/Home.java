package org.example.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.App;
import org.example.application.appointment.AppointmentDaoImpl;
import org.example.application.customer.Customer;
import org.example.application.db.ConnectionPool;
import org.example.application.customer.CustomerDaoImpl;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class Home {
    private final ResourceBundle resources = ResourceBundle.getBundle("org.example.bundles.HomeWindowResources");
    private final String dbUrl = "jdbc:mysql://wgudb.ucertify.com/WJ07mxm";
    private final String dbUsername = "U07mxm";
    private final String dbPassword = "53689073251";

    private ConnectionPool dbConnectionPool;
    private CustomerDaoImpl customerDao;
    private AppointmentDaoImpl appointmentDao;


    private Map<Integer, String> countries;
    private Map<Integer, String> firstLevelDivs;

    @FXML
    private Label title;

    @FXML
    private TabPane tabs;
    @FXML
    private Tab customersTab, appointmentsTab;

    @FXML
    private TableView customersTable;

    @FXML
    private Button addButton, modifyButton, deleteButton;

    /**
     * Controller initialization method.
     */
    public void initialize() {
        dbConnectionPool = ConnectionPool.create(dbUrl, dbUsername, dbPassword, 3);

        try {
            customerDao = new CustomerDaoImpl(dbConnectionPool);
        } catch (ConnectionPool.ConnectionsUnavailableException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(-1);
        }

        countries = customerDao.getCountriesOfBusiness();
        firstLevelDivs = customerDao.getFirstLevelDivisions();


        customersTable.setPlaceholder(new Label("No customers in database."));
        updateCustomersTable();
        initializeCustomersTableColumns();
        customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customersTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, o, t1) -> {
                modifyButton.setDisable(t1 == null);
                deleteButton.setDisable(t1 == null);
        }));

        applyResources();
        updateTabs(null);

        tabs.getSelectionModel().selectedItemProperty().addListener(((observableValue, tab, t1) -> {
            updateTabs(t1);
        }));
    }

    private void updateTabs(Tab selectedTab) {
        if (selectedTab == null || selectedTab == customersTab) {
            addButton.setOnAction(e -> addCustomer());
            modifyButton.setOnAction(e -> modifyCustomer());
            deleteButton.setOnAction(e -> deleteCustomer());
        } else if (selectedTab == appointmentsTab) {
            addButton.setOnAction(e -> addAppointment());
            modifyButton.setOnAction(e -> modifyAppointment());
            deleteButton.setOnAction(e -> deleteAppointment());
        }
    }

    private void modifyAppointment() {
    }

    private void deleteAppointment() {
    }

    private void addAppointment() {
        appointmentOperation(null);
    }

    private void appointmentOperation(Integer appointmentId) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/appointmentOperation.fxml"));

        AppointmentOperation controller = new AppointmentOperation();
        controller.setCustomerDb(appointmentDao);
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (appointmentId != null){
            controller.setOperation(AppointmentOperation.Operations.MODIFY);
            controller.setAppointment(appointmentId);
        } else {
            controller.setOperation(AppointmentOperation.Operations.ADD);
        }

        Scene scene = new Scene(root);
        Stage customerWindow = new Stage();
        customerWindow.setScene(scene);
        customerWindow.initModality(Modality.APPLICATION_MODAL);
        customerWindow.showAndWait();
    }


    private void applyResources() {
        title.setText(App.resources.getString("appTitle"));
        customersTab.setText(resources.getString("customers"));
        appointmentsTab.setText(resources.getString("appointments"));
        modifyButton.setText(resources.getString("modify"));
        deleteButton.setText(resources.getString("delete"));
        addButton.setText(resources.getString("add"));
    }

    /**
     * Initializes the columns of the customersTable TableView and their values.
     */
    private void initializeCustomersTableColumns() {
        TableColumn<Customer, Integer> idColumn = new TableColumn<>(resources.getString("id"));
        TableColumn<Customer, String> nameColumn = new TableColumn<>(resources.getString("name"));
        TableColumn<Customer, String> phoneColumn = new TableColumn<>(resources.getString("phoneNumber"));
        TableColumn<Customer, String> addressColumn = new TableColumn<>(resources.getString("address"));
        TableColumn<Customer, String> postalColumn = new TableColumn<>(resources.getString("postalCode"));
        TableColumn<Customer, String> firstLevelDivColumn = new TableColumn<>(resources.getString("state"));

        idColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>( "phone"));
        firstLevelDivColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return new ReadOnlyObjectWrapper(firstLevelDivs.get(p.getValue().getDivisionId()));
            }
        });

        customersTable.getColumns().addAll(idColumn, nameColumn, phoneColumn, addressColumn,  postalColumn, firstLevelDivColumn);
    }

    @FXML
    /**
     * Opens the add customer window.
     * @throws IOException Will throw IOException if customerOperation.fxml resource is not available.
     */
    public void addCustomer(){
        customerOperation(null);
        updateCustomersTable();
    }

    @FXML
    public void modifyCustomer(){
        Customer selectedCustomer = (Customer) customersTable.getSelectionModel().getSelectedItem();
        customerOperation(selectedCustomer.getId());
        updateCustomersTable();
    }

    @FXML
    public void deleteCustomer() {
        Customer selectedCustomer = (Customer) customersTable.getSelectionModel().getSelectedItem();
        if (confirmDelete()) {
            customerDao.deleteCustomer(selectedCustomer.getId());
        }
        updateCustomersTable();
    }

    private Boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resources.getString("delete") + " " + resources.getString("customer").toLowerCase());
        alert.setHeaderText(resources.getString("deleteCustomerNotification"));
        alert.setContentText(resources.getString("confirmationQuestion"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    private void customerOperation(Integer customerId) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/customerOperation.fxml"));

        CustomerOperation controller = new CustomerOperation();
        controller.setCustomerDb(customerDao);
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (customerId != null){
            controller.setOperation(CustomerOperation.Operations.MODIFY);
            controller.setCustomer(customerId);
        } else {
            controller.setOperation(CustomerOperation.Operations.ADD);
        }

        Scene scene = new Scene(root);
        Stage customerWindow = new Stage();
        customerWindow.setScene(scene);
        customerWindow.initModality(Modality.APPLICATION_MODAL);
        customerWindow.showAndWait();

    }

    private void updateCustomersTable() {
        customersTable.setItems(FXCollections.observableList(customerDao.getAllCustomers()));
    }


}

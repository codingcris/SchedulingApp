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
import org.example.application.customer.Customer;
import org.example.application.db.ConnectionPool;
import org.example.application.customer.CustomerDaoImpl;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class Home {
    private final String dbUrl = "jdbc:mysql://wgudb.ucertify.com/WJ07mxm";
    private final String dbUsername = "U07mxm";
    private final String dbPassword = "53689073251";

    private ConnectionPool dbConnectionPool;
    private CustomerDaoImpl customerDao;

    private Map<Integer, String> countries;
    private Map<Integer, String> firstLevelDivs;

    @FXML
    private TableView customersTable;
    @FXML
    private Button modifyBttn, deleteBttn;

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
                modifyBttn.setDisable(t1 == null);
                deleteBttn.setDisable(t1 == null);
        }));

    }

    /**
     * Initializes the columns of the customersTable TableView and their values.
     */
    private void initializeCustomersTableColumns() {
        TableColumn<Customer, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Customer, String> phoneColumn = new TableColumn<>("Phone");
        TableColumn<Customer, String> addressColumn = new TableColumn<>("Address");
        TableColumn<Customer, String> postalColumn = new TableColumn<>("Postal Code");
        TableColumn<Customer, String> firstLevelDivColumn = new TableColumn<>("State/Province");

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

    /**
     * Opens the add customer window.
     * @throws IOException Will throw IOException if customerOperation.fxml resource is not available.
     */
    public void addCustomer() throws IOException {
        customerOperation(null);
        updateCustomersTable();
    }

    public void updateCustomer() throws IOException {
        Customer selectedCustomer = (Customer) customersTable.getSelectionModel().getSelectedItem();
        customerOperation(selectedCustomer.getId());
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

        Scene scene = new Scene(root);
        Stage customerWindow = new Stage();
        customerWindow.setScene(scene);
        customerWindow.initModality(Modality.APPLICATION_MODAL);

        if (customerId != null)
            controller.setCustomer(customerId);

        customerWindow.showAndWait();

    }

    private void updateCustomersTable() {
        customersTable.setItems(FXCollections.observableList(customerDao.getAllCustomers()));
    }


}

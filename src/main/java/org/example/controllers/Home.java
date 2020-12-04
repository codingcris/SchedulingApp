package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.application.customer.Customer;
import org.example.application.db.ConnectionPool;
import org.example.application.customer.CustomerDaoImpl;

import javax.swing.*;
import java.io.IOException;

public class Home {
    private final String dbUrl = "jdbc:mysql://wgudb.ucertify.com/WJ07mxm";
    private final String dbUsername = "U07mxm";
    private final String dbPassword = "53689073251";

    private ConnectionPool dbConnectionPool;
    private CustomerDaoImpl customerDao;

    @FXML
    private TableView customersTable;

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

        customersTable.setPlaceholder(new Label("No customers in database."));
    }

    /**
     * Opens the add customer window.
     * @throws IOException Will throw IOException if customerOperation.fxml resource is not available.
     */
    public void addCustomer() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/customerOperation.fxml"));
        CustomerOperation controller = new CustomerOperation();
        controller.setCustomerDb(customerDao);
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage customerWindow = new Stage();
        customerWindow.setScene(scene);
        customerWindow.initModality(Modality.APPLICATION_MODAL);
        customerWindow.showAndWait();
    }

}

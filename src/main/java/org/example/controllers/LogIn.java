package org.example.controllers;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.App;
import org.example.application.users.User;
import org.example.application.users.UserDaoImpl;

import javax.swing.*;

/**
 * Controller class for the application's login form.
 */
public class LogIn {
    private ResourceBundle resources;
    private UserDaoImpl loginClient;

    @FXML
    private Label title;
    @FXML
    private Label windowHeader;
    @FXML
    private Label userLocation;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField username;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField password;
    @FXML
    private Button logInBttn;
    @FXML
    private VBox notifications;

    /**
     * Controller initialization method.
     */
    public void initialize() {
        resources = ResourceBundle.getBundle("org.example.bundles.LoginResources", Locale.getDefault());

        try {
            loginClient = new UserDaoImpl();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, resources.getString("loginClientError"));
            System.exit(-1);
        }


        userLocation.setText(ZoneId.systemDefault().toString());
        notifications.setStyle("-fx-background-color: rgba(255,0,0,0.50)");
        applyResources();
    }

    /**
     * Retrieves a ResourceBundle according to user's default Locale and applies the resources to the appropriate
     * labels and controls.
     */
    private void applyResources() {
        resources = ResourceBundle.getBundle("org.example.bundles.LoginResources", Locale.getDefault());

        title.setText(App.resources.getString("appTitle"));
        windowHeader.setText(resources.getString("function"));
        usernameLabel.setText(resources.getString("username"));
        passwordLabel.setText(resources.getString("password"));
        logInBttn.setText(resources.getString("function"));
    }

    private void applyResources(Locale locale) {
        Locale.setDefault(locale);
        applyResources();
    }

    /**
     * Obtains the user entered login information and calls methods to verify the user's login.
     * If validLogin returns true, launches the application home screen.
     * If validLogin return false, displays an invalid message in the notifications area.
     * Clears the notification area prior to verifying each login attempt.
     */
    public void login() {
        clearNotifications();
        String usrname = username.getText().trim();
        char[] passwrd = password.getText().toCharArray();
        
        if(validLogin(usrname, passwrd)) {
            welcomeHome();
        } else {
            invalidLogin();
        }

        passwrd = null;
    }

    /**
     * Clears the notifications area.
     */
    private void clearNotifications() {
        notifications.getChildren().clear();
    }

    /**
     * Displays an invalid login message in the notifications area.
     */
    private void invalidLogin() {
        Label invalidLogIn = new Label(resources.getString("invalidLogin"));
        invalidLogIn.setWrapText(true);
        notifications.getChildren().add(invalidLogIn);
    }

    /**
     * Opens application home screen in a new window and closes the current window.
     */
    private void welcomeHome() {
        Stage home = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/home.fxml"));
            Parent root = loader.load();
            Scene homeScene = new Scene(root);
            home.setScene(homeScene);
            home.setTitle(App.resources.getString("appTitle"));
            home.show();
            ((Stage) logInBttn.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failure opening home screen of application. Contact IT").show();
            ((Stage) logInBttn.getScene().getWindow()).close();
        }
    }

    /**
     * Method used for verifying username and password combinations.
     * @param username login attempt username
     * @param password login attempt password
     * @return a boolean indicating whether the login attempt is valid.
     */
    private boolean validLogin(String username, char[] password) {
        User user = loginClient.getUser(username);

        if (user != null) {
            return loginClient.verifyUser(user, password);
        }

        return false;
    }
}

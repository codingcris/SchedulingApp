package edu.wgu.cristianreyes.controllers;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.wgu.cristianreyes.App;
import edu.wgu.cristianreyes.application.users.User;
import edu.wgu.cristianreyes.application.users.UserDaoImpl;

import javax.swing.*;

/**
 * Login window controller class.
 */
public class LogIn {
    private class LogInLogger {
        private final File logFile;

        {
            logFile = new File("login_activity.txt");
        }

        public LogInLogger() {
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Logging error, if problem persists contact IT.").showAndWait();
                    System.exit(-1);
                }
            }
        }

        public void writeLog(ZonedDateTime attemptTime, String username, boolean success) {
            try (PrintWriter logger = new PrintWriter(new FileOutputStream(logFile, true))){
                String format = "Log In Attempt: %s, Username: %s, Success: %b";
                logger.println(String.format(format, attemptTime, username, success));
            } catch (FileNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, "Logging error, if problem persists contact IT.").showAndWait();
                System.exit(-1);
            }
        }

    }

    private ResourceBundle resources;
    private UserDaoImpl loginClient;
    private LogInLogger logger;


    @FXML
    private Label title;
    @FXML
    private Label windowHeader;
    @FXML
    private Label userLocation;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button logInButton;
    @FXML
    private VBox notifications;

    /**
     * Controller initialization method.
     */
    public void initialize() {
        resources = ResourceBundle.getBundle("edu.wgu.cristianreyes.bundles.LoginResources", Locale.getDefault());

        try {
            loginClient = new UserDaoImpl();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, resources.getString("loginClientError"));
            System.exit(-1);
        }

        logger = new LogInLogger();

        userLocation.setText(ZoneId.systemDefault().toString());
        notifications.setStyle("-fx-background-color: rgba(255,0,0,0.50)");
        applyResources();
    }

    /**
     * Retrieves a ResourceBundle according to user's default Locale and applies the resources to the appropriate
     * labels and controls.
     */
    private void applyResources() {
        resources = ResourceBundle.getBundle("edu.wgu.cristianreyes.bundles.LoginResources", Locale.getDefault());

        title.setText(App.resources.getString("appTitle"));
        windowHeader.setText(resources.getString("function"));
        usernameLabel.setText(resources.getString("username"));
        passwordLabel.setText(resources.getString("password"));
        logInButton.setText(resources.getString("function"));
    }

    /**
     * Obtains the user entered login information and calls methods to verify the user's login.
     * If validLogin returns true, launches the application home screen.
     * If validLogin return false, displays an invalid message in the notifications area.
     * Clears the notification area prior to verifying each login attempt.
     */
    public void login() {
        clearNotifications();

        String username = usernameField.getText().trim();
        char[] password = passwordField.getText().toCharArray();
        
        if(validLogin(username, password)) {
            logger.writeLog(ZonedDateTime.now(ZoneId.of("UTC")), username, true);
            welcomeHome();
        } else {
            logger.writeLog(ZonedDateTime.now(ZoneId.of("UTC")), username, false);
            invalidLogin();
        }

        password = null;
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

    /**
     * Opens application home screen in a new window and closes the current window.
     */
    private void welcomeHome() {
        Stage home = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wgu/cristianreyes/home.fxml"));
            Parent root = loader.load();
            Scene homeScene = new Scene(root);
            home.setScene(homeScene);
            home.setTitle(App.resources.getString("appTitle"));
            home.show();
            home.setMaximized(true);
            home.setMinHeight(500);
            home.setMinWidth(600);
            ((Stage) logInButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, App.resources.getString("homeWindowFailure")).show();
            ((Stage) logInButton.getScene().getWindow()).close();
        }
    }
}

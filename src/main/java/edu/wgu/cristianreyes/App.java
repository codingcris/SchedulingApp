package edu.wgu.cristianreyes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * JavaFX App class handles program execution.
 */
public class App extends Application {
    public static ResourceBundle resources;

    /**
     * launches the login window
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/edu/wgu/cristianreyes/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(resources.getString("appTitle"));
        stage.show();
    }

    public static void main(String[] args) {
        resources = ResourceBundle.getBundle("edu.wgu.cristianreyes.bundles.AppResources", Locale.getDefault());
        launch();
    }



}
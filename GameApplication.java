package com.projetihm.application;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initializes the window manager to open the application window (Stage)
        new WindowManager();

    }
    // test
    public static void main(String[] args) {
        // starts the application
        launch();
    }
}
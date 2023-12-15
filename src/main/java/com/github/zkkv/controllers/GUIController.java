package com.github.zkkv.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

/*    private final GUIService service;

    private final Stage primaryStage;*/

    @FXML
    private CodeArea inputArea;

    @FXML
    private CodeArea outputArea;

    @FXML
    private CodeArea errorArea;

    @FXML
    private Button runButton;

    @FXML
    private Label runningLabel;

    @FXML
    private Label errorCodeLabel;

    public GUIController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

/*    public void showGUI() {
        primaryStage.show();
    }*/

    public void run() {
        // Block editing while the code is running
        inputArea.setEditable(false);

        //service.saveCodeToFile(path, inputArea.getText());

        // Unblock editing
        inputArea.setEditable(true);
    }
}

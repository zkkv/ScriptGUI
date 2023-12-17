package com.github.zkkv.controllers;

import com.github.zkkv.SharedConstants;
import com.github.zkkv.services.GUIService;
import com.github.zkkv.services.ScriptRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUIController implements Initializable {

    private GUIService service;

    private ScriptRunner scriptRunner;

    private SharedConstants constants;

    @FXML
    private CodeArea inputArea;

    @FXML
    private CodeArea outputArea;

    @FXML
    private VBox errorVBox;

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
        outputArea.setEditable(false);
        outputArea.setWrapText(false);

        inputArea.setParagraphGraphicFactory(LineNumberFactory.get(inputArea));
        inputArea.setEditable(true);
        inputArea.setWrapText(false);
    }

    public void setService(GUIService service) {
        this.service = service;
    }

    public void setScriptRunner(ScriptRunner scriptRunner) {
        this.scriptRunner = scriptRunner;
    }

    public void setConstants(SharedConstants constants) {
        this.constants = constants;
    }

    public void run() {
        // Block editing while the code is running
        inputArea.setEditable(false);

        // Clean VBox from previous errors
        errorVBox.getChildren().clear();

        Path savepath = Paths.get(constants.relativeScriptPath() + constants.scriptName());
        try {
            service.saveCodeToFile(savepath, inputArea.getText());
        } catch (IOException e) {
            // TODO: Handle the exception nicely
            e.printStackTrace();
            System.err.println("Something went wrong when trying to write to .kts file.");
            return;
        }

        runningLabel.setText("Running the script");
        try {
            Object result = scriptRunner.executeScript(constants);
            runningLabel.setText("Execution has finished");
            if (result != null) {
                outputArea.replaceText(0, outputArea.getLength(), result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong when trying to read from .kts file.");
        } catch (ScriptException e) {
            e.printStackTrace();
            String[] errorLines = e.getMessage().split(System.lineSeparator());
            handleScriptErrors(errorLines);
        }

        // Unblock editing
        inputArea.setEditable(true);
    }

    private void handleScriptErrors(final String[] errorLines) {
        for (String errorLine : errorLines) {
            // Extract line number and cursor position of the error
            Matcher matcher = Pattern.compile(":(\\d+):(\\d+)").matcher(errorLine);
            if (matcher.find()) {
                int lineNumber = Integer.parseInt(matcher.group(1));
                int cursorPosition = Integer.parseInt(matcher.group(2));

                // Add hyperlink with error to the VBox
                Hyperlink linkToError = new Hyperlink();
                linkToError.setText(errorLine);
                linkToError.setOnAction(event -> {
                    inputArea.requestFocus();
                    inputArea.moveTo(lineNumber - 1, cursorPosition);
                    inputArea.requestFollowCaret();
                });
                errorVBox.getChildren().add(linkToError);
            }
        }
        System.err.println("Something went wrong when executing the script.");
    }
}

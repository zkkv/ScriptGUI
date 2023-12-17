package com.github.zkkv.controllers;

import com.github.zkkv.SharedConstants;
import com.github.zkkv.services.GUIService;
import com.github.zkkv.services.ScriptRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private SplitPane mainSplitPane;

    @FXML
    private SplitPane outputSplitPane;

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
        outputArea.replaceText(0, outputArea.getLength(), "Output will appear here");

        inputArea.setParagraphGraphicFactory(LineNumberFactory.get(inputArea));
        inputArea.setEditable(true);
        inputArea.setWrapText(false);
        inputArea.replaceText(0, inputArea.getLength(), "var example = 42 * 3\nexample");

        // Restrict min and max values for the divider in SplitPane
        mainSplitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < 0.2) {
                mainSplitPane.getDividers().get(0).setPosition(0.2);
            } else if (newValue.doubleValue() > 0.8) {
                mainSplitPane.getDividers().get(0).setPosition(0.8);
            }
        });

        outputSplitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < 0.2) {
                outputSplitPane.getDividers().get(0).setPosition(0.2);
            } else if (newValue.doubleValue() > 0.8) {
                outputSplitPane.getDividers().get(0).setPosition(0.8);
            }
        });
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

        Path filepath = Paths.get(constants.relativeScriptPath() + constants.scriptName());
        try {
            service.saveCodeToFile(filepath, inputArea.getText());
        } catch (IOException e) {
            errorCodeLabel.setTextFill(Color.RED);
            errorCodeLabel.setText("Something went wrong when trying to write to .kts file");
            inputArea.setEditable(true);
            return;
        }

        String script;
        try {
            script = service.readCodeFromFile(filepath);
        } catch (IOException e) {
            errorCodeLabel.setTextFill(Color.RED);
            errorCodeLabel.setText("Something went wrong when trying to write to .kts file");
            inputArea.setEditable(true);
            return;
        }

        runningLabel.setText("Running the script");
        try {
            Object result = scriptRunner.executeScript(script);
            handleSuccessfulExecution(result);
        } catch (ScriptException e) {
            String[] errorLines = e.getMessage().split("\n");
            handleScriptErrors(errorLines);
        }
        // Unblock editing
        inputArea.setEditable(true);
    }

    private void handleSuccessfulExecution(Object result) {
        runningLabel.setText("Execution has finished successfully");
        errorCodeLabel.setTextFill(Color.BLACK);
        errorCodeLabel.setText("Exit code: 0");
        if (result != null) {
            outputArea.replaceText(0, outputArea.getLength(), result.toString());
        }
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
                linkToError.setTextFill(Color.RED);
                linkToError.setOnAction(event -> {
                    inputArea.requestFocus();
                    inputArea.moveTo(lineNumber - 1, cursorPosition);
                    inputArea.requestFollowCaret();
                    linkToError.setVisited(false);
                });
                errorVBox.getChildren().add(linkToError);
            }
        }
        errorCodeLabel.setTextFill(Color.RED);
        errorCodeLabel.setText("Error(s) with script execution");
        runningLabel.setText("Execution has finished with error(s)");
    }
}

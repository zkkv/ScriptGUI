package com.github.zkkv.controllers;

import com.github.zkkv.services.GUIService;
import com.github.zkkv.services.ScriptRunner;
import com.github.zkkv.strategies.ScriptingStrategy;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controls the only scene in the application.
 */
public class GUIController {

    private GUIService service;

    private ScriptRunner scriptRunner;

    private String scriptPath;

    private ScriptingStrategy scriptingStrategy;

    // Multithreading
    private final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private SplitPane outputSplitPane;

    @FXML
    private CodeArea inputArea;

    @FXML
    private TextArea outputArea;

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

    public void setOutputStreamToGUI() {
        System.setOut(new PrintStream(new OutputAreaWrapper(outputArea)));
    }

    public void resetOutputStream() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    /**
     * Sets up attributes and listeners for nodes in the scene.
     */
    public void setupScene() {
        outputArea.setEditable(false);
        outputArea.setWrapText(false);
        outputArea.replaceText(0, outputArea.getLength(), "Output will appear here");

        inputArea.setParagraphGraphicFactory(LineNumberFactory.get(inputArea));
        inputArea.setEditable(true);
        inputArea.setWrapText(false);
        inputArea.replaceText(0, inputArea.getLength(), scriptingStrategy.exampleSnippet());

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

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public void setScriptingStrategy(ScriptingStrategy scriptingStrategy) {
        this.scriptingStrategy = scriptingStrategy;
    }

    /**
     * Method is executed when the "Run" button in the GUI is pressed.
     * Script is saved and executed, and the interface is updated accordingly.
     */
    public void run() {
        // Block editing while the code is running
        inputArea.setEditable(false);

        runButton.setDisable(true);

        // Clean VBox from previous errors
        errorVBox.getChildren().clear();

        // Clean outputStream from previous results
        outputArea.clear();

        Path filePath = Paths.get(scriptPath);
        try {
            service.saveCodeToFile(filePath, inputArea.getText());
        } catch (IOException e) {
            errorCodeLabel.setTextFill(Color.RED);
            errorCodeLabel.setText("Something went wrong when trying to write to ."
                    + scriptingStrategy.extension() + "file");
            inputArea.setEditable(true);
            runButton.setDisable(false);
            return;
        }

        String script;
        try {
            script = service.readCodeFromFile(filePath);
        } catch (IOException e) {
            errorCodeLabel.setTextFill(Color.RED);
            errorCodeLabel.setText("Something went wrong when trying to write to ."
                    + scriptingStrategy.extension() + "file");
            inputArea.setEditable(true);
            runButton.setDisable(false);
            return;
        }

        executeScriptOnDedicatedThread(script);
    }

    private void executeScriptOnDedicatedThread(final String script) {
        runningLabel.setText("Running the script");

        // Create a Task to run the script on the background (not JavaFX) thread
        Task<Object> scriptTask = new Task<>() {
            @Override
            protected Object call() throws Exception {
                return scriptRunner.executeScript(script);
            }
        };
        
        // Set up event handler for when the Task is finished.
        // This is executed on JavaFX thread, unlike call().
        scriptTask.setOnSucceeded((WorkerStateEvent event) -> {
            Object result = scriptTask.getValue();
            handleSuccessfulExecution(result);
            inputArea.setEditable(true);
            runButton.setDisable(false);
        });

        scriptTask.setOnFailed((WorkerStateEvent event) -> {
            String[] errorLines = event.getSource().getException().getMessage().split("\n");
            handleScriptErrors(errorLines);
            inputArea.setEditable(true);
            runButton.setDisable(false);
        });

        EXECUTOR.submit(scriptTask);
    }

    private void handleSuccessfulExecution(final Object result) {
        runningLabel.setText("Execution has finished successfully");
        errorCodeLabel.setTextFill(Color.BLACK);
        errorCodeLabel.setText("Exit code: 0");
        if (result != null) {
            outputArea.replaceText(0, outputArea.getLength(), result.toString());
        }
    }

    private void handleScriptErrors(final String[] errorLines) {
        for (String errorLine : errorLines) {
            int[] lineAndColumn = scriptingStrategy.lineAndColumn(errorLine);

            if (lineAndColumn != null) {
                // Add hyperlink with error to the VBox
                Hyperlink linkToError = new Hyperlink();
                linkToError.setText(errorLine);
                linkToError.setTextFill(Color.RED);
                linkToError.setOnAction(event -> {
                    inputArea.requestFocus();
                    inputArea.moveTo(lineAndColumn[0] - 1, lineAndColumn[1]);
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

    /**
     * Shuts down active threads.
     */
    public void shutdownThreads() {
        EXECUTOR.shutdownNow();
    }

    private class OutputAreaWrapper extends OutputStream {
        private final TextArea outputArea;

        public OutputAreaWrapper(TextArea outputArea) {
            this.outputArea = outputArea;
        }

        @Override
        public void write(int b) throws IOException {
            Platform.runLater(() -> outputArea.appendText(String.valueOf((char)b)));
        }
    }
}

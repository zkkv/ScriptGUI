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
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

/**
 * Controls the only scene in the application.
 */
public class GUIController {

    private GUIService service;

    private ScriptRunner scriptRunner;

    private String scriptPath;

    private ScriptingStrategy scriptingStrategy;

    private Task<Object> scriptTask;

    // Multithreading
    private final ExecutorService executor = Executors.newCachedThreadPool();

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
    private Button abortButton;

    @FXML
    private Label runningLabel;

    @FXML
    private Label errorCodeLabel;

    public GUIController() {

    }

    /**
     * Redirects the default System.out stream to GUI output area.
     */
    public void setOutputStreamToGUI() {
        System.setOut(new PrintStream(new OutputAreaWrapper(outputArea)));
    }

    /**
     * Restores the System.out stream to its default value - FileDescriptor.out.
     */
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

        runButton.setDisable(false);
        abortButton.setDisable(true);

        // Restrict min and max values for the divider in SplitPane
        double minMainPane = 0.2;
        double maxMainPane = 0.8;
        double minOutputPane = 0.2;
        double maxOutputPane = 0.8;
        mainSplitPane.getDividers().get(0).positionProperty()
                .addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < minMainPane) {
                mainSplitPane.getDividers().get(0).setPosition(minMainPane);
            } else if (newValue.doubleValue() > maxMainPane) {
                mainSplitPane.getDividers().get(0).setPosition(maxMainPane);
            }
        });

        outputSplitPane.getDividers().get(0).positionProperty()
                .addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < minOutputPane) {
                outputSplitPane.getDividers().get(0).setPosition(minOutputPane);
            } else if (newValue.doubleValue() > maxOutputPane) {
                outputSplitPane.getDividers().get(0).setPosition(maxOutputPane);
            }
        });

        applyHighlighting(computeHighlighting(inputArea.getText()));

        // 1. Detect changes in the input area.
        // 2. Multiple changes within 250 ms window are considered a single event.
        // 3. Only consider the last event as it's the only one that matters.
        // 4. For each event call createHighlightingTask() method.
        // 5. Wait for the highlighting task to complete before proceeding.
        // 6. For each successful task return the result.
        // 7. For each successful event applyHighlighting to the result.
        inputArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(250))
                .retainLatestUntilLater()
                .supplyTask(this::createHighlightingTask)
                .awaitLatest(inputArea.multiPlainChanges())
                .filterMap(attempt -> {
                    if (attempt.isSuccess()) {
                        return Optional.of(attempt.get());
                    } else {
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
    }

    private Task<StyleSpans<Collection<String>>> createHighlightingTask() {
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(inputArea.getText());
            }
        };
        executor.submit(task);
        return task;
    }

    private void applyHighlighting(final StyleSpans<Collection<String>> highlighting) {
        inputArea.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeHighlighting(final String text) {
        Matcher matcher = scriptingStrategy.keywordPattern().matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            // No style group for non-matched parts
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            // Chosen style group for matched parts
            spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void setService(final GUIService service) {
        this.service = service;
    }

    public void setScriptRunner(final ScriptRunner scriptRunner) {
        this.scriptRunner = scriptRunner;
    }

    public void setScriptPath(final String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public void setScriptingStrategy(final ScriptingStrategy scriptingStrategy) {
        this.scriptingStrategy = scriptingStrategy;
    }

    /**
     * Method is executed when the "Run" button in the GUI is pressed.
     * Script is saved and executed, and the interface is updated accordingly.
     */
    public void run() {
        executeOnRunStart();

        Path filePath = Paths.get(scriptPath);
        try {
            service.saveCodeToFile(filePath, inputArea.getText());
        } catch (IOException e) {
            errorCodeLabel.setTextFill(Color.RED);
            errorCodeLabel.setText("Something went wrong when trying to write to ."
                    + scriptingStrategy.extension() + "file");
            executeOnRunFinish();
            return;
        }

        String script;
        try {
            script = service.readCodeFromFile(filePath);
        } catch (IOException e) {
            errorCodeLabel.setTextFill(Color.RED);
            errorCodeLabel.setText("Something went wrong when trying to write to ."
                    + scriptingStrategy.extension() + "file");
            executeOnRunFinish();
            return;
        }

        runningLabel.setText("Running the script (editing is disabled)");
        executor.submit(initializeScriptTask(script));
    }

    private Task<Object> initializeScriptTask(final String script) {
        // Create a Task to run the script on the background (not JavaFX) thread
        scriptTask = new Task<>() {
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
            executeOnRunFinish();
        });

        scriptTask.setOnFailed((WorkerStateEvent event) -> {
            String[] errorLines = event.getSource().getException().getMessage().split("\n");
            handleScriptErrors(errorLines);
            executeOnRunFinish();
        });

        return scriptTask;
    }

    private void executeOnRunStart() {
        // Block editing while the code is running
        inputArea.setEditable(false);
        runButton.setDisable(true);
        abortButton.setDisable(false);

        // Clean VBox from previous errors
        errorVBox.getChildren().clear();

        // Clean outputArea from previous results
        outputArea.clear();
    }

    private void executeOnRunFinish() {
        inputArea.setEditable(true);
        runButton.setDisable(false);
        abortButton.setDisable(true);
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

                if (scriptingStrategy.isError(errorLine)) {
                    linkToError.setTextFill(Color.RED);
                } else if (scriptingStrategy.isWarning(errorLine)) {
                    linkToError.setTextFill(Color.web("#b29518"));
                }

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
     * Method is executed when the "Abort" button in the GUI is pressed. Execution of the
     * currently running script is stopped.
     */
    public void abort() {
        scriptTask.cancel();
        executeOnRunFinish();
        runningLabel.setText("Execution was aborted");
    }

    /**
     * Shuts down active threads.
     */
    public void shutdownThreads() {
        executor.shutdownNow();
    }

    private class OutputAreaWrapper extends OutputStream {
        private final TextArea outputArea;

        OutputAreaWrapper(final TextArea outputArea) {
            this.outputArea = outputArea;
        }

        @Override
        public void write(final int b) throws IOException {
            Platform.runLater(() -> outputArea.appendText(String.valueOf((char) b)));
        }
    }
}

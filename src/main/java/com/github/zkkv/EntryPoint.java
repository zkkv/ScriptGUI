package com.github.zkkv;

import com.github.zkkv.controllers.GUIController;
import com.github.zkkv.services.GUIService;
import com.github.zkkv.services.ScriptRunner;
import com.github.zkkv.strategies.KotlinScriptingStrategy;
import com.github.zkkv.strategies.ScriptingStrategy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class EntryPoint extends Application {

    /**
     * Launches JavaFX application.
     *
     * @param args arguments from {@link Main}.
     */
    public static void main(final String[] args) {
        launch();
    }

    /**
     * Sets up the stage, the controllers and all utility classes for the application.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        ScriptingStrategy scriptingStrategy = new KotlinScriptingStrategy();
        GUIService service = new GUIService();
        ScriptRunner scriptRunner = new ScriptRunner(scriptingStrategy);

        scene.getStylesheets().add(getClass().getResource("/keywords.css").toExternalForm());

        String scriptName = "script";
        String scriptPath = "src/main/java/com/github/zkkv/scripts/" + scriptName
                + "." + scriptingStrategy.extension();
        GUIController controller = fxmlLoader.getController();

        controller.setScriptPath(scriptPath);
        controller.setService(service);
        controller.setScriptRunner(scriptRunner);
        controller.setScriptingStrategy(scriptingStrategy);
        controller.setOutputStreamToGUI();
        controller.setupScene();

        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(620);
        primaryStage.setMinHeight(500);
        primaryStage.setTitle(scriptingStrategy.languageName() + ": Script Runner GUI");
        primaryStage.setOnCloseRequest(event -> {
            controller.shutdownThreads();
            controller.resetOutputStream();
        });
        primaryStage.getIcons().add(new Image("file:src/main/resources/img/icon16.png"));
        primaryStage.getIcons().add(new Image("file:src/main/resources/img/icon32.png"));
        primaryStage.getIcons().add(new Image("file:src/main/resources/img/icon64.png"));
        primaryStage.getIcons().add(new Image("file:src/main/resources/img/icon128.png"));
        primaryStage.show();
    }
}

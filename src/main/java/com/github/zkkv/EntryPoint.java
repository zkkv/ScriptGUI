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
import javafx.stage.Stage;

public class EntryPoint extends Application {

    /**
     * Launches JavaFX application.
     *
     * @param args arguments from {@link Main}.
     */
    public static void main(String[] args) {
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
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        ScriptingStrategy scriptingStrategy = new KotlinScriptingStrategy();
        GUIService service = new GUIService();
        ScriptRunner scriptRunner = new ScriptRunner(scriptingStrategy);

        String scriptName = "script";
        String scriptPath = "src/main/java/com/github/zkkv/scripts/" + scriptName
                + "." + scriptingStrategy.extension();
        GUIController controller = fxmlLoader.getController();
        controller.setConstants(scriptPath);
        controller.setService(service);
        controller.setScriptRunner(scriptRunner);
        controller.setScriptingStrategy(scriptingStrategy);
        controller.setupScene();

        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(620);
        primaryStage.setMinHeight(500);
        primaryStage.setTitle(scriptingStrategy.languageName() + ": Script Runner GUI");
        primaryStage.show();
    }
}

package com.github.zkkv;

import com.github.zkkv.controllers.GUIController;
import com.github.zkkv.services.GUIService;
import com.github.zkkv.services.ScriptRunner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EntryPoint extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        SharedConstants constants = new SharedConstants("src/main/java/com/github/zkkv/", "script.kts");
        GUIController controller = fxmlLoader.getController();
        controller.setConstants(constants);
        controller.setService(new GUIService());
        controller.setScriptRunner(new ScriptRunner());

        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(620);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}

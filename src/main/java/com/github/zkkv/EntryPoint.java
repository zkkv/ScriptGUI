package com.github.zkkv;

import com.github.zkkv.controllers.GUIController;
import com.github.zkkv.services.GUIService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class EntryPoint extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*SharedConstants constants = new SharedConstants("src/main/java/com/github/zkkv/", "script.kts");
        ScriptRunner scriptRunner = new ScriptRunner(constants);
        scriptRunner.executeScript();*/

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

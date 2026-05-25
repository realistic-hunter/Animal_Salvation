package com.animalsalvation.app;

import com.animalsalvation.controller.MainController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        MainController controller = new MainController();
        Scene scene = new Scene(controller.createView());
        stage.setTitle("基于 Java 与数据结构的流浪动物救助调度管理系统");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

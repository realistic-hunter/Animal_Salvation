package com.animalsalvation.app;

import com.animalsalvation.controller.MainController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX 程序入口。
 *
 * <p>负责创建主控制器、设置窗口标题和最小尺寸。</p>
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        MainController controller = new MainController();
        Scene scene = new Scene(controller.createView());
        stage.setTitle("流浪动物救助调度管理系统");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

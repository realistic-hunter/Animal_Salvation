package com.animalsalvation.controller;

import com.animalsalvation.entity.RescueTask;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for staff pages and navigation.
 */
class StaffController extends AbstractRoleController {
    StaffController(MainController mainController) {
        super(mainController);
    }

    @Override
    public void showDefaultView() {
        showRescueTaskView();
    }

    @Override
    public void addMenuItems(VBox menu) {
        menu.getChildren().add(menuButton("????", event -> mainController.showAnimalManagementView()));
        menu.getChildren().add(menuButton("????", event -> showRescueTaskView()));
        menu.getChildren().add(menuButton("????", event -> mainController.showSearchAndSortView()));
    }

    /** 救助任务管理页面。 */
    void showRescueTaskView() {
        BorderPane content = page();
        Button addButton = primaryButton("新增任务");
        addButton.setOnAction(event -> showAddTaskDialog());
        Button dispatchButton = secondaryButton("派发下一任务");
        dispatchButton.setOnAction(event -> {
            RescueTask task = animalService.dispatchNextTask();
            showInfo(task == null ? "当前没有待处理救助任务。" : "已派发：" + task.getTitle());
            refreshAll();
        });
        content.setTop(toolbar("救助任务", "紧急任务优先处理，普通任务按队列先进先出。", addButton, dispatchButton));

        configureTaskTable(pendingTaskTable, pendingTaskRows);
        configureTaskTable(handledTaskTable, handledTaskRows);
        VBox tables = new VBox(10, sectionTitle("待处理任务"), pendingTaskTable,
                sectionTitle("已派发任务"), handledTaskTable);
        VBox.setVgrow(pendingTaskTable, Priority.ALWAYS);
        VBox.setVgrow(handledTaskTable, Priority.ALWAYS);
        content.setCenter(withMargin(tables));
        root.setCenter(content);
        refreshAll();
    }

    private void showAddTaskDialog() {
        Dialog<RescueTask> dialog = new Dialog<>();
        dialog.setTitle("新增救助任务");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField taskIdField = new TextField(String.valueOf(animalService.nextTaskId()));
        TextField animalIdField = new TextField();
        TextField titleField = new TextField();
        TextField locationField = new TextField();
        ComboBox<String> urgencyBox = combo("非常紧急", "紧急", "普通");
        TextField volunteerField = new TextField();
        List.of(taskIdField, animalIdField, titleField, locationField, volunteerField).forEach(this::styleInput);

        GridPane form = form();
        addFormRow(form, 0, "任务编号", taskIdField);
        addFormRow(form, 1, "动物编号", animalIdField);
        addFormRow(form, 2, "标题", titleField);
        addFormRow(form, 3, "地点", locationField);
        addFormRow(form, 4, "紧急程度", urgencyBox);
        addFormRow(form, 5, "志愿者", volunteerField);
        dialog.getDialogPane().setContent(form);

        protectDialog(dialog, () -> buildTaskFromForm(taskIdField, animalIdField, titleField,
                locationField, urgencyBox, volunteerField));
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? buildTaskFromForm(taskIdField, animalIdField, titleField, locationField, urgencyBox, volunteerField)
                : null);
        dialog.showAndWait().ifPresent(task -> {
            try {
                animalService.addRescueTask(task);
                refreshAll();
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });
    }

    private RescueTask buildTaskFromForm(TextField taskIdField, TextField animalIdField, TextField titleField,
                                         TextField locationField, ComboBox<String> urgencyBox,
                                         TextField volunteerField) {
        return new RescueTask(parsePositiveInt(taskIdField.getText(), "任务编号"),
                parsePositiveInt(animalIdField.getText(), "动物编号"),
                required(titleField.getText(), "标题"),
                required(locationField.getText(), "地点"),
                urgencyBox.getValue(),
                "待处理",
                required(volunteerField.getText(), "志愿者"),
                LocalDateTime.now());
    }

}

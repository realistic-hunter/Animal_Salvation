package com.animalsalvation.controller;

import com.animalsalvation.entity.AdoptionApplication;
import com.animalsalvation.entity.Animal;
import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.entity.RescueTask;
import com.animalsalvation.service.AnimalService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

public class MainController {
    private final AnimalService animalService = new AnimalService();
    private final BorderPane root = new BorderPane();

    private final ObservableList<Animal> animalRows = FXCollections.observableArrayList();
    private final ObservableList<RescueTask> pendingTaskRows = FXCollections.observableArrayList();
    private final ObservableList<RescueTask> handledTaskRows = FXCollections.observableArrayList();
    private final ObservableList<AdoptionApplication> pendingApplyRows = FXCollections.observableArrayList();
    private final ObservableList<AdoptionApplication> reviewedApplyRows = FXCollections.observableArrayList();
    private final ObservableList<OperationRecord> operationRows = FXCollections.observableArrayList();

    private final TableView<Animal> animalTable = new TableView<>();
    private final TableView<Animal> searchTable = new TableView<>();
    private final TableView<RescueTask> pendingTaskTable = new TableView<>();
    private final TableView<RescueTask> handledTaskTable = new TableView<>();
    private final TableView<AdoptionApplication> pendingApplyTable = new TableView<>();
    private final TableView<AdoptionApplication> reviewedApplyTable = new TableView<>();
    private final TableView<OperationRecord> operationTable = new TableView<>();

    public Parent createView() {
        root.setPrefSize(1180, 720);
        root.setLeft(createMenu());
        showAnimalManagementView();
        refreshAll();
        return root;
    }

    private VBox createMenu() {
        VBox menu = new VBox(8);
        menu.setPrefWidth(220);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #263238;");

        Label title = new Label("动物救助调度");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        menu.getChildren().add(title);

        menu.getChildren().add(menuButton("动物信息管理", event -> showAnimalManagementView()));
        menu.getChildren().add(menuButton("救助任务管理", event -> showRescueTaskView()));
        menu.getChildren().add(menuButton("领养申请管理", event -> showAdoptionView()));
        menu.getChildren().add(menuButton("查询与排序", event -> showSearchAndSortView()));
        menu.getChildren().add(menuButton("操作历史", event -> showOperationHistoryView()));
        return menu;
    }

    private Button menuButton(String text, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #ECEFF1; -fx-font-size: 14px;");
        button.setOnAction(handler);
        return button;
    }

    private void showAnimalManagementView() {
        BorderPane content = page("动物信息管理");
        Button addButton = new Button("新增动物");
        addButton.setOnAction(event -> showAddAnimalDialog());
        content.setTop(toolbar("动物信息管理", addButton));

        configureAnimalTable(animalTable, animalRows);
        content.setCenter(withMargin(animalTable));
        root.setCenter(content);
        refreshAll();
    }

    private void showRescueTaskView() {
        BorderPane content = page("救助任务管理");
        Button addButton = new Button("新增任务");
        addButton.setOnAction(event -> showAddTaskDialog());
        Button dispatchButton = new Button("派发下一任务");
        dispatchButton.setOnAction(event -> {
            RescueTask task = animalService.dispatchNextTask();
            if (task == null) {
                showInfo("当前没有待处理救助任务");
            } else {
                showInfo("已派发：" + task.getTitle());
            }
            refreshAll();
        });
        content.setTop(toolbar("救助任务管理", addButton, dispatchButton));

        configureTaskTable(pendingTaskTable, pendingTaskRows);
        configureTaskTable(handledTaskTable, handledTaskRows);
        VBox tables = new VBox(10, sectionTitle("待处理任务：优先队列 + 普通队列"), pendingTaskTable,
                sectionTitle("已派发任务"), handledTaskTable);
        VBox.setVgrow(pendingTaskTable, Priority.ALWAYS);
        VBox.setVgrow(handledTaskTable, Priority.ALWAYS);
        content.setCenter(withMargin(tables));
        root.setCenter(content);
        refreshAll();
    }

    private void showAdoptionView() {
        BorderPane content = page("领养申请管理");
        Button addButton = new Button("提交申请");
        addButton.setOnAction(event -> showAddApplicationDialog());
        Button passButton = new Button("审核通过");
        passButton.setOnAction(event -> reviewApplication("审核通过"));
        Button rejectButton = new Button("审核拒绝");
        rejectButton.setOnAction(event -> reviewApplication("审核拒绝"));
        content.setTop(toolbar("领养申请管理", addButton, passButton, rejectButton));

        configureApplicationTable(pendingApplyTable, pendingApplyRows);
        configureApplicationTable(reviewedApplyTable, reviewedApplyRows);
        VBox tables = new VBox(10, sectionTitle("待审核申请：FIFO 队列"), pendingApplyTable,
                sectionTitle("已审核申请"), reviewedApplyTable);
        VBox.setVgrow(pendingApplyTable, Priority.ALWAYS);
        VBox.setVgrow(reviewedApplyTable, Priority.ALWAYS);
        content.setCenter(withMargin(tables));
        root.setCenter(content);
        refreshAll();
    }

    private void showSearchAndSortView() {
        BorderPane content = page("查询与排序");

        TextField idField = new TextField();
        idField.setPromptText("输入动物编号");
        ComboBox<String> searchMode = new ComboBox<>(FXCollections.observableArrayList("哈希查找", "二分查找", "二叉排序树查找"));
        searchMode.getSelectionModel().selectFirst();
        Button searchButton = new Button("查询");

        ComboBox<String> sortMode = new ComboBox<>(FXCollections.observableArrayList(
                "快速排序：按编号升序", "冒泡排序：按年龄升序", "选择排序：按发现时间升序", "二叉排序树：中序遍历"));
        sortMode.getSelectionModel().selectFirst();
        Button sortButton = new Button("排序/遍历");

        searchButton.setOnAction(event -> {
            try {
                Animal animal = searchAnimal(parsePositiveInt(idField.getText(), "动物编号"), searchMode.getValue());
                animalRows.setAll(animal == null ? List.of() : List.of(animal));
                if (animal == null) {
                    showInfo("没有找到对应动物");
                }
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });
        sortButton.setOnAction(event -> {
            if ("二叉排序树：中序遍历".equals(sortMode.getValue())) {
                animalRows.setAll(animalService.treeInOrderAnimals());
            } else {
                animalRows.setAll(animalService.sortAnimals(sortMode.getValue()));
            }
        });

        HBox controls = new HBox(10, idField, searchMode, searchButton, new Separator(), sortMode, sortButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        content.setTop(toolbar("查询与排序", controls));
        configureAnimalTable(searchTable, animalRows);
        content.setCenter(withMargin(searchTable));
        root.setCenter(content);
        refreshAll();
    }

    private void showOperationHistoryView() {
        BorderPane content = page("操作历史");
        Button undoButton = new Button("撤销最近一次操作");
        undoButton.setOnAction(event -> {
            OperationRecord record = animalService.undoLastOperation();
            if (record == null) {
                showInfo("当前没有可撤销操作");
            } else {
                showInfo("已撤销：" + record.getDescription());
            }
            refreshAll();
        });
        content.setTop(toolbar("操作历史", undoButton));

        configureOperationTable();
        content.setCenter(withMargin(operationTable));
        root.setCenter(content);
        refreshAll();
    }

    private BorderPane page(String title) {
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(18));
        return content;
    }

    private HBox toolbar(String title, javafx.scene.Node... actions) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getChildren().add(heading);
        HBox.setHgrow(heading, Priority.ALWAYS);
        toolbar.getChildren().addAll(actions);
        return toolbar;
    }

    private javafx.scene.Node withMargin(javafx.scene.Node node) {
        BorderPane.setMargin(node, new Insets(16, 0, 0, 0));
        return node;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        return label;
    }

    private void configureAnimalTable(TableView<Animal> table, ObservableList<Animal> rows) {
        table.setItems(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        if (!table.getColumns().isEmpty()) {
            return;
        }
        table.getColumns().add(column("编号", "id", 70));
        table.getColumns().add(column("名称", "name", 90));
        table.getColumns().add(column("类型", "type", 80));
        table.getColumns().add(column("性别", "gender", 70));
        table.getColumns().add(column("年龄", "age", 70));
        table.getColumns().add(column("健康状态", "healthStatus", 130));
        table.getColumns().add(column("救助状态", "rescueStatus", 110));
        table.getColumns().add(column("领养状态", "adoptStatus", 120));
        table.getColumns().add(column("发现地点", "foundLocation", 140));
        table.getColumns().add(column("发现时间", "foundTimeText", 150));
    }

    private void configureTaskTable(TableView<RescueTask> table, ObservableList<RescueTask> rows) {
        table.setItems(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        if (!table.getColumns().isEmpty()) {
            return;
        }
        table.getColumns().add(column("任务编号", "taskId", 80));
        table.getColumns().add(column("动物编号", "animalId", 80));
        table.getColumns().add(column("标题", "title", 150));
        table.getColumns().add(column("地点", "location", 120));
        table.getColumns().add(column("紧急程度", "urgencyLevel", 90));
        table.getColumns().add(column("状态", "status", 90));
        table.getColumns().add(column("志愿者", "volunteerName", 100));
        table.getColumns().add(column("创建时间", "createTimeText", 140));
    }

    private void configureApplicationTable(TableView<AdoptionApplication> table, ObservableList<AdoptionApplication> rows) {
        table.setItems(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        if (!table.getColumns().isEmpty()) {
            return;
        }
        table.getColumns().add(column("申请编号", "applyId", 80));
        table.getColumns().add(column("动物编号", "animalId", 80));
        table.getColumns().add(column("申请人", "applicantName", 100));
        table.getColumns().add(column("电话", "phone", 120));
        table.getColumns().add(column("理由", "reason", 180));
        table.getColumns().add(column("状态", "status", 90));
        table.getColumns().add(column("申请时间", "applyTimeText", 140));
    }

    private void configureOperationTable() {
        operationTable.setItems(operationRows);
        operationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        if (!operationTable.getColumns().isEmpty()) {
            return;
        }
        operationTable.getColumns().add(column("操作类型", "operationType", 120));
        operationTable.getColumns().add(column("目标编号", "targetId", 80));
        operationTable.getColumns().add(column("说明", "description", 260));
        operationTable.getColumns().add(column("操作时间", "operationTimeText", 140));
    }

    private <T> TableColumn<T, Object> column(String title, String property, double width) {
        TableColumn<T, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private void showAddAnimalDialog() {
        Dialog<Animal> dialog = new Dialog<>();
        dialog.setTitle("新增动物");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idField = new TextField(String.valueOf(animalService.nextId()));
        TextField nameField = new TextField();
        ComboBox<String> typeBox = combo("猫", "狗", "其他");
        ComboBox<String> genderBox = combo("雄", "雌", "未知");
        TextField ageField = new TextField();
        TextField healthField = new TextField("待检查");
        ComboBox<String> rescueBox = combo("待救助", "已救助", "治疗中", "观察中");
        ComboBox<String> adoptBox = combo("待领养", "暂不可领养", "已领养");
        TextField locationField = new TextField();

        GridPane form = form();
        addFormRow(form, 0, "编号", idField);
        addFormRow(form, 1, "名称", nameField);
        addFormRow(form, 2, "类型", typeBox);
        addFormRow(form, 3, "性别", genderBox);
        addFormRow(form, 4, "年龄", ageField);
        addFormRow(form, 5, "健康状态", healthField);
        addFormRow(form, 6, "救助状态", rescueBox);
        addFormRow(form, 7, "领养状态", adoptBox);
        addFormRow(form, 8, "发现地点", locationField);
        dialog.getDialogPane().setContent(form);

        protectDialog(dialog, () -> buildAnimalFromForm(idField, nameField, typeBox, genderBox, ageField,
                healthField, rescueBox, adoptBox, locationField));
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? buildAnimalFromForm(idField, nameField, typeBox, genderBox, ageField, healthField,
                rescueBox, adoptBox, locationField)
                : null);
        dialog.showAndWait().ifPresent(animal -> {
            try {
                animalService.addAnimal(animal);
                refreshAll();
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });
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

    private void showAddApplicationDialog() {
        Dialog<AdoptionApplication> dialog = new Dialog<>();
        dialog.setTitle("提交领养申请");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField applyIdField = new TextField(String.valueOf(animalService.nextApplyId()));
        TextField animalIdField = new TextField();
        TextField applicantField = new TextField();
        TextField phoneField = new TextField();
        TextArea reasonArea = new TextArea();
        reasonArea.setPrefRowCount(3);

        GridPane form = form();
        addFormRow(form, 0, "申请编号", applyIdField);
        addFormRow(form, 1, "动物编号", animalIdField);
        addFormRow(form, 2, "申请人", applicantField);
        addFormRow(form, 3, "电话", phoneField);
        addFormRow(form, 4, "申请理由", reasonArea);
        dialog.getDialogPane().setContent(form);

        protectDialog(dialog, () -> buildApplicationFromForm(applyIdField, animalIdField, applicantField,
                phoneField, reasonArea));
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? buildApplicationFromForm(applyIdField, animalIdField, applicantField, phoneField, reasonArea)
                : null);
        dialog.showAndWait().ifPresent(application -> {
            try {
                animalService.addAdoptionApplication(application);
                refreshAll();
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });
    }

    private void reviewApplication(String status) {
        AdoptionApplication application = animalService.reviewNextApplication(status);
        if (application == null) {
            showInfo("当前没有待审核申请");
        } else {
            showInfo(application.getApplicantName() + " 的申请结果：" + status);
        }
        refreshAll();
    }

    private Animal searchAnimal(int animalId, String mode) {
        return switch (mode) {
            case "二分查找" -> animalService.binaryFindById(animalId);
            case "二叉排序树查找" -> animalService.treeFindById(animalId);
            default -> animalService.hashFindById(animalId);
        };
    }

    private Animal buildAnimalFromForm(TextField idField, TextField nameField, ComboBox<String> typeBox,
                                       ComboBox<String> genderBox, TextField ageField, TextField healthField,
                                       ComboBox<String> rescueBox, ComboBox<String> adoptBox,
                                       TextField locationField) {
        return new Animal(parsePositiveInt(idField.getText(), "编号"), required(nameField.getText(), "名称"),
                typeBox.getValue(), genderBox.getValue(), parseNonNegativeInt(ageField.getText(), "年龄"),
                required(healthField.getText(), "健康状态"), rescueBox.getValue(), adoptBox.getValue(),
                required(locationField.getText(), "发现地点"), LocalDateTime.now());
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

    private AdoptionApplication buildApplicationFromForm(TextField applyIdField, TextField animalIdField,
                                                         TextField applicantField, TextField phoneField,
                                                         TextArea reasonArea) {
        return new AdoptionApplication(parsePositiveInt(applyIdField.getText(), "申请编号"),
                parsePositiveInt(animalIdField.getText(), "动物编号"),
                required(applicantField.getText(), "申请人"),
                required(phoneField.getText(), "电话"),
                required(reasonArea.getText(), "申请理由"),
                "待审核",
                LocalDateTime.now());
    }

    private ComboBox<String> combo(String... values) {
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }

    private GridPane form() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        return form;
    }

    private void addFormRow(GridPane form, int row, String label, javafx.scene.Node field) {
        form.add(new Label(label), 0, row);
        form.add(field, 1, row);
    }

    private void protectDialog(Dialog<?> dialog, Runnable validator) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                validator.run();
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
                event.consume();
            }
        });
    }

    private void refreshAll() {
        animalRows.setAll(animalService.findAll());
        pendingTaskRows.setAll(animalService.pendingTasks());
        handledTaskRows.setAll(animalService.handledTasks());
        pendingApplyRows.setAll(animalService.pendingApplications());
        reviewedApplyRows.setAll(animalService.reviewedApplications());
        operationRows.setAll(animalService.operationHistory());
    }

    private String required(String value, String fieldName) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        return text;
    }

    private int parsePositiveInt(String value, String fieldName) {
        int number = parseNonNegativeInt(value, fieldName);
        if (number <= 0) {
            throw new IllegalArgumentException(fieldName + "必须大于 0");
        }
        return number;
    }

    private int parseNonNegativeInt(String value, String fieldName) {
        try {
            int number = Integer.parseInt(required(value, fieldName));
            if (number < 0) {
                throw new IllegalArgumentException(fieldName + "不能小于 0");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + "必须是整数");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("输入错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("系统提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

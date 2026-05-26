package com.animalsalvation.controller;

import com.animalsalvation.entity.AdoptionApplication;
import com.animalsalvation.entity.Animal;
import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.entity.RescueTask;
import com.animalsalvation.entity.User;
import com.animalsalvation.service.AnimalService;
import com.animalsalvation.service.UserService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 主界面控制器。
 *
 * <p>这个类负责界面设计和页面切换：先显示登录页，登录成功后根据用户角色显示不同菜单。</p>
 */
public class MainController {
    /** 界面标题和统一配色，集中放在这里方便调整整体风格。 */
    private static final String APP_TITLE = "流浪动物救助调度管理系统";
    static final String BG = "#F4F1EC";
    static final String PANEL = "#FFFFFF";
    static final String SIDEBAR = "#273238";
    static final String PRIMARY = "#4D7466";
    static final String TEXT = "#26312D";
    static final String MUTED = "#6B756F";

    /** 动物救助业务层。 */
    final AnimalService animalService = new AnimalService();
    /** 用户登录和用户管理业务层。 */
    final UserService userService = new UserService();
    /** 整个 JavaFX 界面的根布局。 */
    final BorderPane root = new BorderPane();

    /** 当前登录用户；为 null 时表示还没有登录。 */
    User currentUser;

    /** 以下 ObservableList 是表格的数据源，refreshAll 会刷新这些数据。 */
    final ObservableList<Animal> animalRows = FXCollections.observableArrayList();
    final ObservableList<RescueTask> pendingTaskRows = FXCollections.observableArrayList();
    final ObservableList<RescueTask> handledTaskRows = FXCollections.observableArrayList();
    final ObservableList<AdoptionApplication> pendingApplyRows = FXCollections.observableArrayList();
    final ObservableList<AdoptionApplication> reviewedApplyRows = FXCollections.observableArrayList();
    final ObservableList<OperationRecord> operationRows = FXCollections.observableArrayList();
    final ObservableList<User> userRows = FXCollections.observableArrayList();

    final TableView<Animal> animalTable = new TableView<>();
    final TableView<Animal> searchTable = new TableView<>();
    final TableView<RescueTask> pendingTaskTable = new TableView<>();
    final TableView<RescueTask> handledTaskTable = new TableView<>();
    final TableView<AdoptionApplication> pendingApplyTable = new TableView<>();
    final TableView<AdoptionApplication> reviewedApplyTable = new TableView<>();
    final TableView<OperationRecord> operationTable = new TableView<>();
    final TableView<User> userTable = new TableView<>();

    /** 创建初始界面，程序启动后先进入登录页。 */
    public Parent createView() {
        tryLoadUsers();
        root.setPrefSize(1180, 720);
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Microsoft YaHei', 'Segoe UI';");
        showLoginView();
        return root;
    }

    /** 初始登录页面。 */
    private void showLoginView() {
        root.setLeft(null);
        root.setTop(null);

        VBox card = new VBox(18);
        card.setMaxWidth(420);
        card.setPadding(new Insets(34));
        card.setStyle("-fx-background-color: " + PANEL + "; -fx-background-radius: 8;"
                + "-fx-border-color: #DED8CF; -fx-border-radius: 8;");

        Label title = new Label(APP_TITLE);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");
        Label subtitle = new Label("请输入账号进入对应角色工作台");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + MUTED + ";");

        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        styleInput(usernameField);
        styleInput(passwordField);

        Button loginButton = primaryButton("登录");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(event -> {
            try {
                currentUser = userService.login(usernameField.getText(), passwordField.getText());
                trySaveUsers();
                showMainShell();
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });

        card.getChildren().addAll(title, subtitle, fieldBlock("用户名", usernameField),
                fieldBlock("密码", passwordField), loginButton);

        BorderPane loginPane = new BorderPane();
        loginPane.setStyle("-fx-background-color: " + BG + ";");
        loginPane.setCenter(card);
        BorderPane.setAlignment(card, Pos.CENTER);
        root.setCenter(loginPane);
    }

    /** 登录成功后生成主界面框架，并按角色打开默认页面。 */
    private void showMainShell() {
        root.setLeft(createMenu());
        createRoleController().showDefaultView();
        refreshAll();
    }

    private RoleController createRoleController() {
        if (isAdmin()) {
            return new AdminController(this);
        }
        if (isStaff()) {
            return new StaffController(this);
        }
        return new AdopterController(this);
    }

    /** 根据当前用户角色创建左侧菜单。 */
    private VBox createMenu() {
        VBox menu = new VBox(8);
        menu.setPrefWidth(236);
        menu.setPadding(new Insets(22));
        menu.setStyle("-fx-background-color: " + SIDEBAR + ";");

        Label title = new Label("动物救助调度");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label user = new Label(currentUser.getRealName() + "  " + roleText(currentUser.getRole()));
        user.setStyle("-fx-text-fill: #B7C4BE; -fx-font-size: 12px;");

        menu.getChildren().addAll(title, user, gap(10));
        createRoleController().addMenuItems(menu);
        menu.getChildren().add(menuButton("个人信息", event -> showProfileView()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button logout = menuButton("退出登录", event -> {
            currentUser = null;
            showLoginView();
        });
        menu.getChildren().addAll(spacer, logout);
        return menu;
    }

    /** 创建左侧菜单按钮。 */
    Button menuButton(String text, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 12, 10, 12));
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #EEF3F0; -fx-font-size: 14px;"
                + "-fx-background-radius: 6;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #3A4943; -fx-text-fill: white;"
                + "-fx-font-size: 14px; -fx-background-radius: 6;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #EEF3F0;"
                + "-fx-font-size: 14px; -fx-background-radius: 6;"));
        button.setOnAction(handler);
        return button;
    }

    /** 所有角色都能进入的个人信息页面。 */
    void showProfileView() {
        BorderPane content = page();
        Button saveButton = primaryButton("保存资料");
        Button passwordButton = secondaryButton("修改密码");

        TextField realName = new TextField(currentUser.getRealName());
        TextField phone = new TextField(currentUser.getPhone());
        TextField email = new TextField(currentUser.getEmail());
        styleInput(realName);
        styleInput(phone);
        styleInput(email);

        GridPane form = form();
        addFormRow(form, 0, "用户名", new Label(currentUser.getUsername()));
        addFormRow(form, 1, "角色", new Label(roleText(currentUser.getRole())));
        addFormRow(form, 2, "姓名", realName);
        addFormRow(form, 3, "电话", phone);
        addFormRow(form, 4, "邮箱", email);
        addFormRow(form, 5, "最近登录", new Label(currentUser.getLastLoginTimeText()));

        saveButton.setOnAction(event -> {
            try {
                currentUser = userService.updatePersonalInfo(currentUser.getId(),
                        realName.getText(), phone.getText(), email.getText());
                trySaveUsers();
                root.setLeft(createMenu());
                showInfo("个人信息已保存。");
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });
        passwordButton.setOnAction(event -> showChangePasswordDialog());
        content.setTop(toolbar("个人信息", "维护自己的联系方式和登录密码。", saveButton, passwordButton));
        content.setCenter(withMargin(formPanel(form)));
        root.setCenter(content);
    }

    /** 动物信息管理页面。 */
    void showAnimalManagementView() {
        BorderPane content = page();
        Button addButton = primaryButton("新增动物");
        addButton.setOnAction(event -> showAddAnimalDialog());
        content.setTop(toolbar("动物信息", "登记、查看和维护救助动物基础信息。", addButton));

        configureAnimalTable(animalTable, animalRows);
        content.setCenter(withMargin(animalTable));
        root.setCenter(content);
        refreshAll();
    }

    /** 查询排序页面，展示哈希查找、二分查找、树查找和排序算法。 */
    void showSearchAndSortView() {
        BorderPane content = page();
        TextField idField = new TextField();
        idField.setPromptText("动物编号");
        styleInput(idField);
        ComboBox<String> searchMode = combo("哈希查找", "二分查找", "二叉搜索树查找");
        Button searchButton = primaryButton("查询");
        ComboBox<String> sortMode = combo("快速排序：按编号升序", "冒泡排序：按年龄升序",
                "选择排序：按发现时间升序", "二叉搜索树：中序遍历");
        Button sortButton = secondaryButton("排序/遍历");

        searchButton.setOnAction(event -> {
            try {
                Animal animal = searchAnimal(parsePositiveInt(idField.getText(), "动物编号"), searchMode.getValue());
                animalRows.setAll(animal == null ? List.of() : List.of(animal));
                if (animal == null) {
                    showInfo("没有找到对应动物。");
                }
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        });
        sortButton.setOnAction(event -> {
            if ("二叉搜索树：中序遍历".equals(sortMode.getValue())) {
                animalRows.setAll(animalService.treeInOrderAnimals());
            } else {
                animalRows.setAll(animalService.sortAnimals(sortMode.getValue()));
            }
        });

        HBox controls = new HBox(10, idField, searchMode, searchButton, new Separator(), sortMode, sortButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        content.setTop(toolbar("查询排序", "展示哈希查找、二分查找、树查找和排序算法。", controls));
        configureAnimalTable(searchTable, animalRows);
        content.setCenter(withMargin(searchTable));
        root.setCenter(content);
        refreshAll();
    }

    void showChangePasswordDialog() {
        PasswordField oldPassword = new PasswordField();
        PasswordField newPassword = new PasswordField();
        styleInput(oldPassword);
        styleInput(newPassword);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("修改密码");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane form = form();
        addFormRow(form, 0, "旧密码", oldPassword);
        addFormRow(form, 1, "新密码", newPassword);
        dialog.getDialogPane().setContent(form);
        protectDialog(dialog, () -> {
            required(oldPassword.getText(), "旧密码");
            validatePassword(newPassword.getText());
        });
        dialog.setResultConverter(button -> button == ButtonType.OK ? newPassword.getText() : null);
        dialog.showAndWait().ifPresent(value -> {
            userService.changePassword(currentUser.getId(), oldPassword.getText(), value);
            trySaveUsers();
            showInfo("密码已修改。");
        });
    }

    private void showAddAnimalDialog() {
        Dialog<Animal> dialog = new Dialog<>();
        dialog.setTitle("新增动物");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idField = new TextField(String.valueOf(animalService.nextId()));
        TextField nameField = new TextField();
        ComboBox<String> typeBox = combo("猫", "狗", "其他");
        ComboBox<String> genderBox = combo("雌", "雄", "未知");
        TextField ageField = new TextField();
        TextField healthField = new TextField("待检查");
        ComboBox<String> rescueBox = combo("待救助", "已救助", "治疗中", "观察中");
        ComboBox<String> adoptBox = combo("待领养", "暂不可领养", "已领养");
        TextField locationField = new TextField();
        List.of(idField, nameField, ageField, healthField, locationField).forEach(this::styleInput);

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

    Animal searchAnimal(int animalId, String mode) {
        return switch (mode) {
            case "二分查找" -> animalService.binaryFindById(animalId);
            case "二叉搜索树查找" -> animalService.treeFindById(animalId);
            default -> animalService.hashFindById(animalId);
        };
    }

    Animal buildAnimalFromForm(TextField idField, TextField nameField, ComboBox<String> typeBox,
                                       ComboBox<String> genderBox, TextField ageField, TextField healthField,
                                       ComboBox<String> rescueBox, ComboBox<String> adoptBox,
                                       TextField locationField) {
        return new Animal(parsePositiveInt(idField.getText(), "编号"), required(nameField.getText(), "名称"),
                typeBox.getValue(), genderBox.getValue(), parseNonNegativeInt(ageField.getText(), "年龄"),
                required(healthField.getText(), "健康状态"), rescueBox.getValue(), adoptBox.getValue(),
                required(locationField.getText(), "发现地点"), LocalDateTime.now());
    }

    void configureAnimalTable(TableView<Animal> table, ObservableList<Animal> rows) {
        table.setItems(rows);
        styleTable(table);
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

    void configureTaskTable(TableView<RescueTask> table, ObservableList<RescueTask> rows) {
        table.setItems(rows);
        styleTable(table);
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

    void configureApplicationTable(TableView<AdoptionApplication> table,
                                           ObservableList<AdoptionApplication> rows) {
        table.setItems(rows);
        styleTable(table);
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

    void configureOperationTable() {
        operationTable.setItems(operationRows);
        styleTable(operationTable);
        if (!operationTable.getColumns().isEmpty()) {
            return;
        }
        operationTable.getColumns().add(column("操作类型", "operationType", 120));
        operationTable.getColumns().add(column("目标编号", "targetId", 80));
        operationTable.getColumns().add(column("说明", "description", 260));
        operationTable.getColumns().add(column("操作时间", "operationTimeText", 140));
    }

    void configureUserTable() {
        userTable.setItems(userRows);
        styleTable(userTable);
        if (!userTable.getColumns().isEmpty()) {
            return;
        }
        userTable.getColumns().add(column("编号", "id", 70));
        userTable.getColumns().add(column("用户名", "username", 100));
        userTable.getColumns().add(column("姓名", "realName", 120));
        userTable.getColumns().add(column("电话", "phone", 120));
        userTable.getColumns().add(column("邮箱", "email", 170));
        userTable.getColumns().add(column("角色", "role", 90));
        userTable.getColumns().add(column("启用", "enabled", 70));
        userTable.getColumns().add(column("创建时间", "createTimeText", 140));
        userTable.getColumns().add(column("最近登录", "lastLoginTimeText", 140));
    }

    <T> TableColumn<T, Object> column(String title, String property, double width) {
        TableColumn<T, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    BorderPane page() {
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BG + ";");
        return content;
    }

    HBox toolbar(String title, String subtitle, javafx.scene.Node... actions) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");
        Label sub = new Label(subtitle);
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + MUTED + ";");
        VBox texts = new VBox(4, heading, sub);
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getChildren().add(texts);
        HBox.setHgrow(texts, Priority.ALWAYS);
        toolbar.getChildren().addAll(actions);
        return toolbar;
    }

    javafx.scene.Node withMargin(javafx.scene.Node node) {
        BorderPane.setMargin(node, new Insets(16, 0, 0, 0));
        return node;
    }

    Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");
        return label;
    }

    Button primaryButton(String text) {
        Button button = new Button(text);
        button.setPadding(new Insets(9, 15, 9, 15));
        button.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 6;"
                + "-fx-font-size: 13px;");
        return button;
    }

    Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setPadding(new Insets(9, 15, 9, 15));
        button.setStyle("-fx-background-color: #E8E2D8; -fx-text-fill: " + TEXT + "; -fx-background-radius: 6;"
                + "-fx-font-size: 13px;");
        return button;
    }

    private VBox fieldBlock(String label, javafx.scene.Node field) {
        Label text = new Label(label);
        text.setStyle("-fx-text-fill: " + TEXT + "; -fx-font-size: 12px;");
        return new VBox(6, text, field);
    }

    GridPane form() {
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(12));
        return form;
    }

    private VBox formPanel(GridPane form) {
        VBox panel = new VBox(form);
        panel.setMaxWidth(560);
        panel.setPadding(new Insets(18));
        panel.setStyle("-fx-background-color: " + PANEL + "; -fx-background-radius: 8;"
                + "-fx-border-color: #DED8CF; -fx-border-radius: 8;");
        return panel;
    }

    void addFormRow(GridPane form, int row, String label, javafx.scene.Node field) {
        Label labelNode = new Label(label);
        labelNode.setMinWidth(86);
        labelNode.setStyle("-fx-text-fill: " + TEXT + ";");
        form.add(labelNode, 0, row);
        form.add(field, 1, row);
    }

    ComboBox<String> combo(String... values) {
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.getSelectionModel().selectFirst();
        comboBox.setStyle(inputStyle());
        return comboBox;
    }

    void styleInput(TextField field) {
        field.setStyle(inputStyle());
        field.setPrefHeight(36);
    }

    String inputStyle() {
        return "-fx-background-color: #FBFAF7; -fx-border-color: #D8D0C5;"
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: " + TEXT + ";";
    }

    private void styleTable(TableView<?> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: " + PANEL + "; -fx-border-color: #DED8CF;"
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
    }

    private Region gap(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        return region;
    }

    void protectDialog(Dialog<?> dialog, Runnable validator) {
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

    void refreshAll() {
        animalRows.setAll(animalService.findAll());
        pendingTaskRows.setAll(animalService.pendingTasks());
        handledTaskRows.setAll(animalService.handledTasks());
        pendingApplyRows.setAll(animalService.pendingApplications());
        reviewedApplyRows.setAll(animalService.reviewedApplications());
        operationRows.setAll(animalService.operationHistory());
        userRows.setAll(userService.findAllUsers());
    }

    boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    boolean isStaff() {
        return currentUser != null && "STAFF".equals(currentUser.getRole());
    }

    private String roleText(String role) {
        return switch (role) {
            case "ADMIN" -> "管理员";
            case "STAFF" -> "工作人员";
            default -> "领养用户";
        };
    }

    private void tryLoadUsers() {
        try {
            userService.loadUsers();
        } catch (IOException exception) {
            trySaveUsers();
        }
    }

    void trySaveUsers() {
        try {
            userService.saveUsers();
        } catch (IOException exception) {
            showError("用户数据保存失败：" + exception.getMessage());
        }
    }

    String required(String value, String fieldName) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空。");
        }
        return text;
    }

    int parsePositiveInt(String value, String fieldName) {
        int number = parseNonNegativeInt(value, fieldName);
        if (number <= 0) {
            throw new IllegalArgumentException(fieldName + "必须大于 0。");
        }
        return number;
    }

    int parseNonNegativeInt(String value, String fieldName) {
        try {
            int number = Integer.parseInt(required(value, fieldName));
            if (number < 0) {
                throw new IllegalArgumentException(fieldName + "不能小于 0。");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + "必须是整数。");
        }
    }

    void validatePassword(String password) {
        String value = required(password, "密码");
        if (value.length() < 6) {
            throw new IllegalArgumentException("密码至少需要 6 位。");
        }
    }

    void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("系统提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("系统提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

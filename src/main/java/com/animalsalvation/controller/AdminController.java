package com.animalsalvation.controller;

import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.entity.User;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Controller for administrator pages and navigation.
 */
class AdminController extends AbstractRoleController {
    AdminController(MainController mainController) {
        super(mainController);
    }

    @Override
    public void showDefaultView() {
        showUserManagementView();
    }

    @Override
    public void addMenuItems(VBox menu) {
        menu.getChildren().add(menuButton("????", event -> showUserManagementView()));
        menu.getChildren().add(menuButton("????", event -> mainController.showAnimalManagementView()));
        menu.getChildren().add(menuButton("????", event -> new StaffController(mainController).showRescueTaskView()));
        menu.getChildren().add(menuButton("????", event -> new AdopterController(mainController).showAdoptionView()));
        menu.getChildren().add(menuButton("????", event -> mainController.showSearchAndSortView()));
        menu.getChildren().add(menuButton("????", event -> showOperationHistoryView()));
    }

    /** 管理员页面：用户增删改查、重置密码。 */
    void showUserManagementView() {
        BorderPane content = page();
        Button addButton = primaryButton("新增用户");
        addButton.setOnAction(event -> showAddUserDialog());
        Button editButton = secondaryButton("编辑用户");
        editButton.setOnAction(event -> showEditUserDialog());
        Button resetButton = secondaryButton("重置密码");
        resetButton.setOnAction(event -> resetSelectedUserPassword());
        Button deleteButton = secondaryButton("删除用户");
        deleteButton.setOnAction(event -> deleteSelectedUser());
        content.setTop(toolbar("用户管理", "管理员可以维护系统账号、角色和启用状态。",
                addButton, editButton, resetButton, deleteButton));

        configureUserTable();
        content.setCenter(withMargin(userTable));
        root.setCenter(content);
        refreshAll();
    }

    /** 操作历史页面，管理员可以撤销最近一次新增操作。 */
    void showOperationHistoryView() {
        BorderPane content = page();
        Button undoButton = primaryButton("撤销最近操作");
        undoButton.setOnAction(event -> {
            OperationRecord record = animalService.undoLastOperation();
            showInfo(record == null ? "当前没有可撤销操作。" : "已撤销：" + record.getDescription());
            refreshAll();
        });
        content.setTop(toolbar("操作历史", "使用栈保存操作记录，支持撤销最近一次新增。", undoButton));

        configureOperationTable();
        content.setCenter(withMargin(operationTable));
        root.setCenter(content);
        refreshAll();
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("新增用户");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        TextField realName = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        ComboBox<String> role = combo("STAFF", "ADOPTER", "ADMIN");
        styleInput(username);
        styleInput(password);
        styleInput(realName);
        styleInput(phone);
        styleInput(email);

        GridPane form = form();
        addFormRow(form, 0, "用户名", username);
        addFormRow(form, 1, "密码", password);
        addFormRow(form, 2, "姓名", realName);
        addFormRow(form, 3, "电话", phone);
        addFormRow(form, 4, "邮箱", email);
        addFormRow(form, 5, "角色", role);
        dialog.getDialogPane().setContent(form);

        protectDialog(dialog, () -> {
            required(username.getText(), "用户名");
            required(realName.getText(), "姓名");
            validatePassword(password.getText());
            if (userService.usernameExists(username.getText())) {
                throw new IllegalArgumentException("用户名已存在。");
            }
        });
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? userService.addUser(username.getText(), password.getText(), realName.getText(),
                phone.getText(), email.getText(), role.getValue())
                : null);
        dialog.showAndWait().ifPresent(user -> {
            trySaveUsers();
            refreshAll();
        });
    }

    private void showEditUserDialog() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("请先选择一个用户。");
            return;
        }
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("编辑用户");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField realName = new TextField(selected.getRealName());
        TextField phone = new TextField(selected.getPhone());
        TextField email = new TextField(selected.getEmail());
        ComboBox<String> role = combo("STAFF", "ADOPTER", "ADMIN");
        role.setValue(selected.getRole());
        CheckBox enabled = new CheckBox("启用");
        enabled.setSelected(selected.isEnabled());
        styleInput(realName);
        styleInput(phone);
        styleInput(email);

        GridPane form = form();
        addFormRow(form, 0, "用户名", new Label(selected.getUsername()));
        addFormRow(form, 1, "姓名", realName);
        addFormRow(form, 2, "电话", phone);
        addFormRow(form, 3, "邮箱", email);
        addFormRow(form, 4, "角色", role);
        addFormRow(form, 5, "状态", enabled);
        dialog.getDialogPane().setContent(form);

        protectDialog(dialog, () -> required(realName.getText(), "姓名"));
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? userService.updateUser(selected.getId(), realName.getText(), phone.getText(),
                email.getText(), role.getValue(), enabled.isSelected())
                : null);
        dialog.showAndWait().ifPresent(user -> {
            trySaveUsers();
            refreshAll();
        });
    }

    private void resetSelectedUserPassword() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("请先选择一个用户。");
            return;
        }
        TextField password = new PasswordField();
        styleInput(password);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("重置密码");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane form = form();
        addFormRow(form, 0, "新密码", password);
        dialog.getDialogPane().setContent(form);
        protectDialog(dialog, () -> validatePassword(password.getText()));
        dialog.setResultConverter(button -> button == ButtonType.OK ? password.getText() : null);
        dialog.showAndWait().ifPresent(value -> {
            userService.resetPassword(selected.getId(), value);
            trySaveUsers();
            showInfo("密码已重置。");
        });
    }

    private void deleteSelectedUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("请先选择一个用户。");
            return;
        }
        if (selected.getId() == currentUser.getId()) {
            showError("不能删除当前登录用户。");
            return;
        }
        userService.removeUser(selected.getId());
        trySaveUsers();
        refreshAll();
    }

}

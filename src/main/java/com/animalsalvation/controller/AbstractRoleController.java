package com.animalsalvation.controller;

import com.animalsalvation.entity.AdoptionApplication;
import com.animalsalvation.entity.Animal;
import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.entity.RescueTask;
import com.animalsalvation.entity.User;
import com.animalsalvation.service.AnimalService;
import com.animalsalvation.service.UserService;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

abstract class AbstractRoleController implements RoleController {
    protected final MainController mainController;
    protected final AnimalService animalService;
    protected final UserService userService;
    protected final BorderPane root;
    protected final User currentUser;
    protected final ObservableList<Animal> animalRows;
    protected final ObservableList<RescueTask> pendingTaskRows;
    protected final ObservableList<RescueTask> handledTaskRows;
    protected final ObservableList<AdoptionApplication> pendingApplyRows;
    protected final ObservableList<AdoptionApplication> reviewedApplyRows;
    protected final ObservableList<OperationRecord> operationRows;
    protected final ObservableList<User> userRows;
    protected final TableView<Animal> animalTable;
    protected final TableView<Animal> searchTable;
    protected final TableView<RescueTask> pendingTaskTable;
    protected final TableView<RescueTask> handledTaskTable;
    protected final TableView<AdoptionApplication> pendingApplyTable;
    protected final TableView<AdoptionApplication> reviewedApplyTable;
    protected final TableView<OperationRecord> operationTable;
    protected final TableView<User> userTable;

    AbstractRoleController(MainController mainController) {
        this.mainController = mainController;
        this.animalService = mainController.animalService;
        this.userService = mainController.userService;
        this.root = mainController.root;
        this.currentUser = mainController.currentUser;
        this.animalRows = mainController.animalRows;
        this.pendingTaskRows = mainController.pendingTaskRows;
        this.handledTaskRows = mainController.handledTaskRows;
        this.pendingApplyRows = mainController.pendingApplyRows;
        this.reviewedApplyRows = mainController.reviewedApplyRows;
        this.operationRows = mainController.operationRows;
        this.userRows = mainController.userRows;
        this.animalTable = mainController.animalTable;
        this.searchTable = mainController.searchTable;
        this.pendingTaskTable = mainController.pendingTaskTable;
        this.handledTaskTable = mainController.handledTaskTable;
        this.pendingApplyTable = mainController.pendingApplyTable;
        this.reviewedApplyTable = mainController.reviewedApplyTable;
        this.operationTable = mainController.operationTable;
        this.userTable = mainController.userTable;
    }

    protected Button menuButton(String text, javafx.event.EventHandler<ActionEvent> handler) {
        return mainController.menuButton(text, handler);
    }

    protected BorderPane page() {
        return mainController.page();
    }

    protected HBox toolbar(String title, String subtitle, javafx.scene.Node... actions) {
        return mainController.toolbar(title, subtitle, actions);
    }

    protected javafx.scene.Node withMargin(javafx.scene.Node node) {
        return mainController.withMargin(node);
    }

    protected Label sectionTitle(String text) {
        return mainController.sectionTitle(text);
    }

    protected Button primaryButton(String text) {
        return mainController.primaryButton(text);
    }

    protected Button secondaryButton(String text) {
        return mainController.secondaryButton(text);
    }

    protected GridPane form() {
        return mainController.form();
    }

    protected void addFormRow(GridPane form, int row, String label, javafx.scene.Node field) {
        mainController.addFormRow(form, row, label, field);
    }

    protected ComboBox<String> combo(String... values) {
        return mainController.combo(values);
    }

    protected void styleInput(TextField field) {
        mainController.styleInput(field);
    }

    protected String inputStyle() {
        return mainController.inputStyle();
    }

    protected void protectDialog(Dialog<?> dialog, Runnable validator) {
        mainController.protectDialog(dialog, validator);
    }

    protected void refreshAll() {
        mainController.refreshAll();
    }

    protected void trySaveUsers() {
        mainController.trySaveUsers();
    }

    protected boolean isAdmin() {
        return mainController.isAdmin();
    }

    protected String required(String value, String fieldName) {
        return mainController.required(value, fieldName);
    }

    protected int parsePositiveInt(String value, String fieldName) {
        return mainController.parsePositiveInt(value, fieldName);
    }

    protected void validatePassword(String password) {
        mainController.validatePassword(password);
    }

    protected void showError(String message) {
        mainController.showError(message);
    }

    protected void showInfo(String message) {
        mainController.showInfo(message);
    }

    protected void configureTaskTable(TableView<RescueTask> table, ObservableList<RescueTask> rows) {
        mainController.configureTaskTable(table, rows);
    }

    protected void configureApplicationTable(TableView<AdoptionApplication> table,
                                             ObservableList<AdoptionApplication> rows) {
        mainController.configureApplicationTable(table, rows);
    }

    protected void configureOperationTable() {
        mainController.configureOperationTable();
    }

    protected void configureUserTable() {
        mainController.configureUserTable();
    }
}

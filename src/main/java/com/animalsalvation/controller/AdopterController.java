package com.animalsalvation.controller;

import com.animalsalvation.entity.AdoptionApplication;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for adopter pages and navigation.
 */
class AdopterController extends AbstractRoleController {
    AdopterController(MainController mainController) {
        super(mainController);
    }

    @Override
    public void showDefaultView() {
        showAdoptionView();
    }

    @Override
    public void addMenuItems(VBox menu) {
        menu.getChildren().add(menuButton("????", event -> showAdoptionView()));
        menu.getChildren().add(menuButton("????", event -> mainController.showSearchAndSortView()));
    }

    /** 领养申请页面；管理员能审核，领养用户只能提交和查看。 */
    void showAdoptionView() {
        BorderPane content = page();
        Button addButton = primaryButton("提交申请");
        addButton.setOnAction(event -> showAddApplicationDialog());
        HBox actions;
        if (isAdmin()) {
            Button passButton = secondaryButton("审核通过");
            passButton.setOnAction(event -> reviewApplication("审核通过"));
            Button rejectButton = secondaryButton("审核拒绝");
            rejectButton.setOnAction(event -> reviewApplication("审核拒绝"));
            actions = new HBox(10, addButton, passButton, rejectButton);
        } else {
            actions = new HBox(10, addButton);
        }
        content.setTop(toolbar("领养申请", "待审核申请按提交顺序进入 FIFO 队列。", actions));

        configureApplicationTable(pendingApplyTable, pendingApplyRows);
        configureApplicationTable(reviewedApplyTable, reviewedApplyRows);
        VBox tables = new VBox(10, sectionTitle("待审核申请"), pendingApplyTable,
                sectionTitle("已审核申请"), reviewedApplyTable);
        VBox.setVgrow(pendingApplyTable, Priority.ALWAYS);
        VBox.setVgrow(reviewedApplyTable, Priority.ALWAYS);
        content.setCenter(withMargin(tables));
        root.setCenter(content);
        refreshAll();
    }

    private void showAddApplicationDialog() {
        Dialog<AdoptionApplication> dialog = new Dialog<>();
        dialog.setTitle("提交领养申请");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField applyIdField = new TextField(String.valueOf(animalService.nextApplyId()));
        TextField animalIdField = new TextField();
        TextField applicantField = new TextField(currentUser == null ? "" : currentUser.getRealName());
        TextField phoneField = new TextField(currentUser == null ? "" : currentUser.getPhone());
        TextArea reasonArea = new TextArea();
        reasonArea.setPrefRowCount(3);
        List.of(applyIdField, animalIdField, applicantField, phoneField).forEach(this::styleInput);
        reasonArea.setStyle(inputStyle());

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
        showInfo(application == null ? "当前没有待审核申请。" : application.getApplicantName() + " 的申请结果：" + status);
        refreshAll();
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

}

package com.animalsalvation.controller;

import javafx.scene.layout.VBox;

/**
 * Role-specific page routing and menu setup after login.
 */
interface RoleController {
    void showDefaultView();

    void addMenuItems(VBox menu);
}

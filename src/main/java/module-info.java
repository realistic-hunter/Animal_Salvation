module com.animalsalvation {
    requires javafx.controls;

    exports com.animalsalvation.app;
    exports com.animalsalvation.entity;
    exports com.animalsalvation.structure;
    exports com.animalsalvation.service;
    exports com.animalsalvation.controller;
    exports com.animalsalvation.algorithm;
    exports com.animalsalvation.util;

    opens com.animalsalvation.entity to javafx.base;
}

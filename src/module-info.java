module is.progetto {
    requires javafx.controls;
    requires javafx.graphics;

    requires java.sql;

    exports is.progetto;
    exports is.model;
    exports is.progetto.view;
    exports is.progetto.controller;

    opens is.model to javafx.base;
}
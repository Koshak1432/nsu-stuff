module philharmoonia {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    requires jakarta.validation;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.boot;
    requires jakarta.persistence;
    requires lombok;
    requires org.hibernate.orm.core;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.beans;
    requires spring.security.core;
    requires spring.security.config;

    opens nsu.philharmoonia to javafx.fxml;
    exports nsu.philharmoonia;
    exports nsu.philharmoonia.view;
    opens nsu.philharmoonia.view to javafx.fxml;
}
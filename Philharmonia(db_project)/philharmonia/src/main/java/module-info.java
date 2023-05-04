module nsu.philharmonia {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.hibernate.orm.core;
    requires jakarta.validation;
    requires jakarta.persistence;
    requires  lombok;

    opens nsu.philharmonia to javafx.fxml;
    exports nsu.philharmonia;
    exports nsu.philharmonia.controllers;
    opens nsu.philharmonia.controllers to javafx.fxml;
}
module com.example.fothlab {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.protobuf;


    opens com.example.fothlab to javafx.fxml;
    exports com.example.fothlab;
}
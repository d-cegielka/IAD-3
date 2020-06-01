module org.iad {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.iad to javafx.fxml;
    exports org.iad;
}
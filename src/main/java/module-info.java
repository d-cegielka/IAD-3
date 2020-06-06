module org.iad {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires voronoi.java;
    requires javafx.swing;

    opens org.iad to javafx.fxml;
    exports org.iad;
}
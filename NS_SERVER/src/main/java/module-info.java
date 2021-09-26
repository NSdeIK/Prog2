module ns_srv.ns_server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens ns_srv.ns_server to javafx.fxml;
    exports ns_srv.ns_server;
}
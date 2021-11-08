module ns_srv.ns_server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires scrypt;

    opens ns_srv.ns_server to javafx.fxml;
    exports ns_srv.ns_server;
    exports Server;
    opens Server to javafx.fxml;
}
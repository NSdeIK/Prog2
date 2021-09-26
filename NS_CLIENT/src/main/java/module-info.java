module com.ns_deik.ns_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;

    opens com.ns_deik.ns_client to javafx.fxml;
    exports com.ns_deik.ns_client;
    exports com.ns_deik.ns_client.homepage;
    opens com.ns_deik.ns_client.homepage to javafx.fxml;
    exports com.ns_deik.ns_client.gameroom;
    opens com.ns_deik.ns_client.gameroom to javafx.fxml;
    exports com.ns_deik.ns_client.gameroom.room_create;
    opens com.ns_deik.ns_client.gameroom.room_create to javafx.fxml;
    exports com.ns_deik.ns_client.gameroom.room_join;
    opens com.ns_deik.ns_client.gameroom.room_join to javafx.fxml;
    exports com.ns_deik.ns_client.mainServer;
    opens com.ns_deik.ns_client.mainServer to javafx.fxml;
    exports com.ns_deik.ns_client.lobby;
    opens com.ns_deik.ns_client.lobby to javafx.fxml;
}
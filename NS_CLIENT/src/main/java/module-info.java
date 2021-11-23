module com.ns_deik.ns_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires javafx.graphics;
    requires scrypt;

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
    exports Server;
    opens Server to javafx.fxml;
}
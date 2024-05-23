module com.example.hito2entrega {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;


    opens com.example.hito2entrega to javafx.fxml;
    exports com.example.hito2entrega;
}
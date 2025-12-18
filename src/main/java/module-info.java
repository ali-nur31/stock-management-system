module stock.main.stock {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    opens stock.main.stock to javafx.fxml;
    exports stock.main.stock;
    exports stock.main.stock.controllers;
    opens stock.main.stock.controllers to javafx.fxml;
    exports stock.main.stock.models;
    opens stock.main.stock.models to javafx.fxml;
    exports stock.main.stock.database;
    opens stock.main.stock.database to javafx.fxml;
}

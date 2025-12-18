package stock.main.stock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stock.main.stock.database.Database;

public class MainApp extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Database.createTables();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/stock/main/stock/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Stock - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

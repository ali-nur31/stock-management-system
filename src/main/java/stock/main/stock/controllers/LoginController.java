package stock.main.stock.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import stock.main.stock.database.Database;
import stock.main.stock.MainApp;
import stock.main.stock.models.Session;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    protected void onLogin() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText().trim();

        if (u.isEmpty() || p.isEmpty()) {
            showError("Validation", "Username and password required.");
            return;
        }

        try {
            if (Database.validateUser(u, p)) {

                int id = Database.getUserId(u);
                Session.currentUserId = id;

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/stock/main/stock/stock.fxml"));
                Scene scene = new Scene(loader.load());

                StockController controller = loader.getController();
                controller.setUser(id);

                MainApp.getPrimaryStage().setScene(scene);

            } else {
                showError("Login failed", "Invalid username or password.");
            }
        } catch (SQLException | IOException e) {
            showError("Database error", e.getMessage());
        }
    }


    @FXML
    protected void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stock/main/stock/register.fxml"
            ));
            Scene scene = new Scene(loader.load());
            MainApp.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error", e.getMessage());
        }
    }

    private void showError(String SKU, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(SKU);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}

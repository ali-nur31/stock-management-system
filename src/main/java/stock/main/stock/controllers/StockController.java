package stock.main.stock.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import stock.main.stock.models.Product;
import stock.main.stock.database.Database;
import stock.main.stock.MainApp;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private int userId;

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colSku;
    @FXML private TableColumn<Product, String> colSupplier;
    @FXML private TableColumn<Product, String> colQuantity;

    @FXML private TextField nameField;
    @FXML private TextField skuField;
    @FXML private TextField supplierField;
    @FXML private TextField quantityField;
    @FXML private TextField searchField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupColumns();
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> fillForm(newSel)
        );
    }

    public void setUser(int userId) {
        this.userId = userId;
        try {
            loadProducts();
        } catch (SQLException e) {
            showError("DB Error", e.getMessage());
        }
    }

    @FXML
    protected void loadProducts() throws SQLException {
        ObservableList<Product> list = Database.getAllProductsForUser(userId);
        productsTable.setItems(list);
        if (list.isEmpty()) clearForm();
    }

    @FXML
    protected void addItem() {
        Product productObj = getFormData();
        if (productObj == null) return;
        try {
            Database.addProductForUser(productObj, userId);
            showInfo("Success", "Product added");
            reload();
        } catch (SQLException e) {
            showError("DB Error", e.getMessage());
        }
    }

    @FXML
    protected void deleteProduct() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select product", "Choose product to delete");
            return;
        }

        if (confirmAction("Delete product", "Delete product: " + selected.getSKU() + "?")) {
            try {
                Database.deleteProductForUser(selected.getSupplier(), userId);
                showInfo("Deleted", "Product removed");
                reload();
            } catch (SQLException e) {
                showError("DB Error", e.getMessage());
            }
        }
    }

    @FXML
    protected void editProduct() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        Product productObj = getFormData();
        if (selected == null) {
            showWarning("Select product", "Choose a product to edit.");
            return;
        }
        if (productObj == null) return;
        if (!productObj.getSupplier().equals(selected.getSupplier())) {
            showWarning("Supplier change", "Changing supplier is not allowed.");
            return;
        }

        try {
            Database.updateProductForUser(productObj, userId);
            showInfo("Updated", "Product updated.");
            reload();
        } catch (SQLException e) {
            showError("DB Error", e.getMessage());
        }
    }

    @FXML
    protected void searchProduct() {
        String text = searchField.getText().trim();
        try {
            if (text.length() < 2) {
                loadProducts();
                return;
            }
            productsTable.setItems(Database.searchProductsForUser(text, userId));
        } catch (SQLException e) {
            showError("DB Error", e.getMessage());
        }
    }

    private void setupColumns() {
        colName.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getName()));
        colSku.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getSKU()));
        colSupplier.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getSupplier()));
        colQuantity.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getQuantity()));
    }

    private void fillForm(Product p) {
        if (p == null) {
            clearForm();
            return;
        }
        nameField.setText(p.getName());
        skuField.setText(p.getSKU());
        supplierField.setText(p.getSupplier());
        supplierField.setEditable(false);
        quantityField.setText(p.getQuantity());
    }

    private Product getFormData() {
        String name = nameField.getText().trim();
        String sku = skuField.getText().trim();
        String supp = supplierField.getText().trim();
        String qty = quantityField.getText().trim();

        if (name.isEmpty() || sku.isEmpty() || supp.isEmpty()) {
            showWarning("Validation", "Fields Name, SKU and Supplier are required");
            return null;
        }
        return new Product(name, sku, supp, qty);
    }

    private void clearForm() {
        nameField.clear();
        skuField.clear();
        supplierField.clear();
        supplierField.setEditable(true);
        quantityField.clear();
        productsTable.getSelectionModel().clearSelection();
    }

    private boolean confirmAction(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setTitle(title);
        return a.showAndWait().filter(btn -> btn == ButtonType.YES).isPresent();
    }

    private void showError(String title, String msg) { showAlert(Alert.AlertType.ERROR, title, msg); }
    private void showWarning(String title, String msg) { showAlert(Alert.AlertType.WARNING, title, msg); }
    private void showInfo(String title, String msg) { showAlert(Alert.AlertType.INFORMATION, title, msg); }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    private void reload() throws SQLException {
        clearForm();
        loadProducts();
    }

    public static void changeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("stock.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = MainApp.getPrimaryStage();
        stage.setScene(scene);
        stage.show();
    }
}

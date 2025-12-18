package stock.main.stock.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stock.main.stock.models.Product;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:stock.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() throws SQLException {
        String usersSql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            );
            """;

        String productsSql = """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                SKU TEXT,
                supplier TEXT,
                quantity INTEGER,
                user_id INTEGER,
                FOREIGN KEY(user_id) REFERENCES users(id)
            );
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersSql);
            stmt.execute(productsSql);
        }
    }

    public static boolean registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean validateUser(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static int getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
                else return -1;
            }
        }
    }


    public static void addProductForUser(Product product, int userId) throws SQLException {
        String sql = "INSERT INTO products(name, SKU, supplier, quantity, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getSKU());
            pstmt.setString(3, product.getSupplier());
            pstmt.setString(4, product.getQuantity());
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();
        }
    }

    public static ObservableList<Product> getAllProductsForUser(int userId) throws SQLException {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String sql = "SELECT name, SKU, supplier, quantity FROM products WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getString("name"),
                            rs.getString("SKU"),
                            rs.getString("supplier"),
                            rs.getString("quantity")
                    ));
                }
            }
        }
        return list;
    }

    public static void deleteProductForUser(String supplier, int userId) throws SQLException {
        String sql = "DELETE FROM products WHERE supplier = ? AND user_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public static void updateProductForUser(Product product, int userId) throws SQLException {
        String sql = "UPDATE products SET name = ?, SKU = ?, quantity = ? WHERE supplier = ? AND user_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getSKU());
            pstmt.setString(3, product.getQuantity());
            pstmt.setString(4, product.getSupplier());
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();
        }
    }

    public static ObservableList<Product> searchProductsForUser(String text, int userId) throws SQLException {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String sql = "SELECT name, SKU, supplier, quantity FROM products WHERE user_id = ? AND (" +
                "LOWER(name) LIKE ? OR LOWER(SKU) LIKE ? OR LOWER(supplier) LIKE ? OR LOWER(quantity) LIKE ?)";
        String q = "%" + text.toLowerCase() + "%";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, q);
            pstmt.setString(3, q);
            pstmt.setString(4, q);
            pstmt.setString(5, q);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getString("name"),
                            rs.getString("SKU"),
                            rs.getString("supplier"),
                            rs.getString("quantity")
                    ));
                }
            }
        }
        return list;
    }
}

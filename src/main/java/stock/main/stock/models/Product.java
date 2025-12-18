package stock.main.stock.models;

public class Product {
    private String name;
    private String SKU;
    private String supplier;
    private String quantity;

    public Product(String name, String SKU, String supplier, String quantity) {
        this.name = name;
        this.SKU = SKU;
        this.supplier = supplier;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

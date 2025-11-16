package domain;

/**
 * Domain object representing the stock level for a product.
 * Tracks available and reserved quantities.
 */
public class InventoryItem {
    
    private String productId;
    private int availableQty; // Stock available for reservation
    private int reservedQty;  // Stock reserved but not yet committed (sold)

    public InventoryItem(String productId, int initialAvailableQty) {
        this.productId = productId;
        this.availableQty = initialAvailableQty;
        this.reservedQty = 0;
    }

    // --- Getters and Setters ---

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public int getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(int reservedQty) {
        this.reservedQty = reservedQty;
    }
}
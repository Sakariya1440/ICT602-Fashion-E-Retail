package domain;

import java.time.LocalDateTime;

/**
 * Domain object representing a temporary reservation of stock.
 * Holds allocation details and an expiry timestamp.
 */
public class Reservation {
    
    private String id;
    private String productId;
    private int qty;
    private LocalDateTime expiresAt;

    public Reservation(String id, String productId, int qty, LocalDateTime expiresAt) {
        this.id = id;
        this.productId = productId;
        this.qty = qty;
        this.expiresAt = expiresAt;
    }

    // --- Getters ---

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public int getQty() {
        return qty;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
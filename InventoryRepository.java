package repository;

import domain.InventoryItem;
import java.util.Optional;

/**
 * Interface for the Inventory Repository, abstracting persistence.
 * This follows the Repository Pattern.
 */
public interface InventoryRepository {

    /**
     * Finds an inventory item by its associated product ID.
     * @param productId The ID of the product.
     * @return An Optional containing the InventoryItem if found.
     */
    Optional<InventoryItem> findByProductId(String productId);

    /**
     * Saves (creates or updates) an inventory item.
     * @param item The InventoryItem to save.
     * @return The saved InventoryItem.
     */
    InventoryItem save(InventoryItem item);
}
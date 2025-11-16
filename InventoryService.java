package service;

import domain.InventoryItem;
import domain.Reservation;
import repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Complex Module: InventoryService
 * Handles reservation, commit, release, and expiry logic with concurrency control.
 * This implementation uses in-memory locks to ensure atomicity for a single-process demo.
 */
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    
    // In-memory store for active reservations. In a real app, this would be a database table.
    private final ConcurrentHashMap<String, Reservation> activeReservations = new ConcurrentHashMap<>();
    
    // A map of locks, one for each product, to ensure atomic operations on a per-product basis.
    private final ConcurrentHashMap<String, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    // Reservation duration in minutes
    private static final int RESERVATION_EXPIRY_MINUTES = 15;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Atomically reserves a specified quantity of a product.
     *
     * @param productId The ID of the product to reserve.
     * @param qty       The quantity to reserve.
     * @return The Reservation object if successful.
     * @throws IllegalStateException if stock is insufficient or product doesn't exist.
     */
    public Reservation reserve(String productId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        // Get or create a lock for this specific product to prevent race conditions
        ReentrantLock lock = productLocks.computeIfAbsent(productId, k -> new ReentrantLock());

        lock.lock();
        try {
            // 1. Get the current inventory item
            InventoryItem item = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));

            // 2. Check availability (availableQty = total stock - already reserved stock)
            if (item.getAvailableQty() < qty) {
                throw new IllegalStateException("Insufficient stock for product: " + productId);
            }

            // 3. Create the reservation
            String reservationId = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESERVATION_EXPIRY_MINUTES);
            Reservation reservation = new Reservation(reservationId, productId, qty, expiresAt);

            // 4. Update the inventory item's state
            item.setAvailableQty(item.getAvailableQty() - qty);
            item.setReservedQty(item.getReservedQty() + qty);
            inventoryRepository.save(item); // Save the updated item

            // 5. Store the active reservation and return it
            activeReservations.put(reservationId, reservation);
            return reservation;

        } finally {
            lock.unlock(); // Always release the lock
        }
    }

    /**
     * Commits a reservation, finalizing the sale.
     * This converts reserved stock into a permanent reduction.
     *
     * @param reservationId The ID of the reservation to commit.
     * @throws IllegalStateException if the reservation is not found or is expired.
     */
    public void commit(String reservationId) {
        Reservation reservation = activeReservations.remove(reservationId);
        if (reservation == null) {
            throw new IllegalStateException("Invalid or expired reservation ID: " + reservationId);
        }

        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Reservation already expired, stock should have been released
            throw new IllegalStateException("Reservation expired: " + reservationId);
        }

        ReentrantLock lock = productLocks.get(reservation.getProductId());
        if (lock == null) {
            // Should not happen if reservation was made
            throw new IllegalStateException("Product lock not found for commit.");
        }
        
        lock.lock();
        try {
            // 1. Get the inventory item
            InventoryItem item = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Product not found: " + reservation.getProductId()));
            
            // 2. Finalize: Decrement reservedQty. availableQty was already decremented.
            item.setReservedQty(item.getReservedQty() - reservation.getQty());
            
            // Note: Total stock would also be reduced here.
            // For this demo, we just adjust the reserved count.
            inventoryRepository.save(item);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases a reservation, returning stock to the available pool.
     * Called on payment failure, cancellation, or expiry.
     *
     * @param reservationId The ID of the reservation to release.
     */
    public void release(String reservationId) {
        Reservation reservation = activeReservations.remove(reservationId);
        if (reservation == null) {
            // Reservation already committed, expired, or never existed. No action needed.
            return; 
        }

        ReentrantLock lock = productLocks.get(reservation.getProductId());
        if (lock == null) {
            return; // Product lock might not exist if no activity
        }

        lock.lock();
        try {
            // 1. Get the inventory item
            InventoryItem item = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElse(null); // Don't throw if item is gone
            
            if (item != null) {
                // 2. Return stock to the pool
                item.setAvailableQty(item.getAvailableQty() + reservation.getQty());
                item.setReservedQty(item.getReservedQty() - reservation.getQty());
                inventoryRepository.save(item);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Background task to clean up expired reservations.
     * In a real app, this would be run by a scheduled job (e.g., cron, @Scheduled).
     */
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        for (Reservation reservation : activeReservations.values()) {
            if (reservation.getExpiresAt().isBefore(now)) {
                System.out.println("Cleaning up expired reservation: " + reservation.getId());
                release(reservation.getId());
            }
        }
    }
}
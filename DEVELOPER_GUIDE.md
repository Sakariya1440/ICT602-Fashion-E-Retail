Developer Guide: Mapping UML Classes to Java Implementation

This guide explains where each major class from the conceptual class diagram is implemented in the source code repository. It allows markers to quickly locate the core components of the system and verify consistency between the design and implementation.

1. Presentation Layer

UML Class

Java File

Notes

WebController

src/main/java/presentation/WebController.java

Handles incoming requests, validates input and delegates to service layer. No business logic implemented at this level.

2. Service Layer

UML Class

Java File

Notes

ProductService

service/ProductService.java

Provides product search and retrieval functionality. Relies on ProductRepository.

CartService

service/CartService.java

Manages cart operations (add, update, remove). Stateless, uses in-memory map for prototype.

OrderService

service/OrderService.java

Coordinates checkout: creates order and interacts with InventoryService.

InventoryService

service/InventoryService.java

Complex module implemented for the project. Handles reservation, commit, release, and expiry cleanup with concurrency control.

3. Domain Layer

UML Class

Java File

Notes

Product

domain/Product.java

Represents catalogue item with price and SKU.

InventoryItem

domain/InventoryItem.java

Tracks available and reserved stock for each product. Used by InventoryService.

Reservation

domain/Reservation.java

Holds temporary allocation details with expiry timestamp.

Order

domain/Order.java

Aggregate root representing a customer order.

OrderLine

domain/OrderLine.java

Line-item for order (product ID, qty, unit price).

4. Repository Layer

UML Class (Interface)

Java Interface

Implementation Example

ProductRepository

repository/ProductRepository.java

In-memory variant: ProductRepositoryMemory.java

InventoryRepository

repository/InventoryRepository.java

In-memory variant: InventoryRepositoryMemory.java

OrderRepository

repository/OrderRepository.java

In-memory variant: OrderRepositoryMemory.java

All repositories follow the Repository Pattern to isolate persistence concerns and allow future replacement with database-backed implementations.
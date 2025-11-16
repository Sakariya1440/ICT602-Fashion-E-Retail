ICT602 - Fashion E-Retail Platform Project (Assignment 3)

This repository contains the Java implementation for the Fashion E-Retail Platform, focusing on the "Inventory Reservation with Concurrency Control" complex module for Assessment 3, Part A.

Project Structure

/src/main/java/domain/: Contains the main domain objects (POJOs) like InventoryItem.java and Reservation.java.

/src/main/java/repository/: Contains the data access interfaces, such as InventoryRepository.java. (Implementations like InventoryRepositoryMemory.java would go in an impl sub-package).

/src/main/java/service/: Contains the core business logic, including the complex module InventoryService.java.

System Requirements

Java Version: JDK 11 or newer

Build Tool: Apache Maven 3.6+ (recommended) or Gradle

How to Build and Run

This project is a standard Java application. You can build it and run its tests using Maven.

1. Build the Project

From the root directory, run:

mvn clean install


This will compile the code and run any unit tests.

2. Run the Marker Test/Demo

A simple demo class (Main.java or a JUnit test) can be used to demonstrate the functionality of the InventoryService.

Sample Data (To be pre-loaded in an in-memory repository):

Product ID: "SKU-TSHIRT-001"

Product Name: "Classic White T-Shirt"

Initial Available Quantity: 10

Steps for Marker to Follow:

Here is a conceptual test scenario to demonstrate the concurrency control:

Check Initial State:

InventoryItem for "SKU-TSHIRT-001" has availableQty = 10 and reservedQty = 0.

Scenario: Successful Reservation (User 1)

Call inventoryService.reserve("SKU-TSHIRT-001", 3)

Expected: Success. A Reservation object is returned.

Check State: availableQty = 7, reservedQty = 3.

Scenario: Concurrent Reservation (User 2)

Call inventoryService.reserve("SKU-TSHIRT-001", 5)

Expected: Success. A different Reservation object is returned.

Check State: availableQty = 2, reservedQty = 8.

Scenario: Insufficient Stock (User 3)

Call inventoryService.reserve("SKU-TSHIRT-001", 3)

Expected: Fails with IllegalStateException("Insufficient stock...").

Check State: availableQty = 2, reservedQty = 8.

Scenario: Commit Reservation (User 1)

Call inventoryService.commit(reservationIdFromStep2)

Expected: Success.

Check State: availableQty = 2, reservedQty = 5. (User 1's 3 items are now sold).

Scenario: Release Reservation (User 2 - Payment Failed)

Call inventoryService.release(reservationIdFromStep3)

Expected: Success.

Check State: availableQty = 7, reservedQty = 0. (User 2's 5 items are returned to the pool).
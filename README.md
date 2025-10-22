# 🚌 BookingBus – Spring Boot Reservation System

A Spring Boot 3.5.6 web application for bus ticket booking and seat availability management. This project demonstrates RESTful API design, request validation, and price calculation logic — packaged as a deployable WAR.

## Project Overview
Framework: Spring Boot 3.5.6  
Build Tool: Apache Maven  
Java Version: 17  
Packaging: WAR  
Testing Framework: TestNG  
Default Port: 8080

## Prerequisites
Before building or running the project, ensure the following tools are installed:
- Java JDK 17 or later → verify using `java -version`
- Apache Maven 3.8+ → verify using `mvn -v`
- (Optional) Apache Tomcat 9+ for external deployment

## Build Instructions
Clone the repository and navigate into it:
```bash
git clone https://github.com/ruwanliyanage123/book-your-bus.git
cd book-your-bus
```
Build the project and create a WAR file:
```bash
mvn clean package
```
After building, the WAR file will be generated at: target/bookingbus-1.0.0.war

## How to Run the Project
### Run directly (self-contained WAR)
You can execute the WAR directly using Java:
```bash
java -jar target/bookingbus-1.0.0.war
```

## REST API Documentation
Base URL: `http://localhost:8080/api/v1/bus/booking`

### API 1: Check Seat Availability and Price
**Method:** GET  
**Endpoint:** `/availability-and-price`  
**Example Request:**
```bash
curl --location 'http://localhost:8080/api/v1/bus/booking/availability-and-price?numberOfPassengers=5&origin=A&destination=B'
```
**Query Parameters:**
- numberOfPassengers (Integer, required): Number of passengers to book
- origin (Character, required): Starting point (A, B, C, D)
- destination (Character, required): Destination point (A, B, C, D)

**Response Example:**
```json
{
  "availableSeatCount": 3,
  "availableSeats": [
    "D8",
    "D9",
    "D10"
  ],
  "totalPrice": 150.0
}
```
**Error Responses:**
- 400: "Invalid origin or destination"
- 400: "Number of passengers must be greater than zero"
- 400: "Sorry! No seats available"

### API 2: Reserve Tickets
**Method:** POST  
**Endpoint:** `/tickets`  
**Example Request:**
```bash
curl --location 'http://localhost:8080/api/v1/bus/booking/tickets' --header 'Content-Type: application/json' --data '{
    "passengerCount":37,
    "origin":"A",
    "destination":"B",
    "priceConfirmation":true
}'
```
**Request Body Fields:**
- passengerCount (Integer, required): Number of passengers
- origin (Character, required): Journey start point
- destination (Character, required): Journey end point
- priceConfirmation (Boolean, required): Confirms user has accepted fare

**Response Example:**
```json
{
    "ticketNumbers": [
        38,
        39
    ],
    "seatNumbers": [
        "D8",
        "D9"
    ],
    "origin": "A",
    "destination": "B",
    "totalPrice": 100.0
}
```
**Error Responses:**
- 400: "Invalid reservation request"
- 400: "Not enough available seats"
- 400: "Invalid origin or destination"

## Run Unit Tests
Run all tests using Maven:
```bash
mvn test
```
Example output:
```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.bookingbus.bookingbus.service.impl.BookingServiceImplTest
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Price Matrix
| Route | Price (Rs.) |
|--------|--------------|
| A → B / B → A | 50 |
| A → C / C → A | 100 |
| A → D / D → A | 150 |
| B → C / C → B | 50 |
| B → D / D → B | 100 |
| C → D / D → C | 50 |
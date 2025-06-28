# WebSocket
Real-time chat application built with Spring Boot, WebSocket, and a modern responsive frontend. Supports public and private messaging with live online user tracking.

# WebSocket Chat Application

A real-time chat application built with **Spring Boot**, **WebSocket (STOMP)**, and a modern HTML/CSS/JS frontend.

## Features

- Real-time messaging using WebSocket and STOMP
- Public (group) and private (1:1) chat
- Live online users list
- Responsive design (desktop & mobile)
- Light and dark mode (auto-detect)
- Message history
- User-friendly, accessible interface

## How does it work?

- **Spring Boot backend** sets up a WebSocket endpoint (`/ws`) using STOMP protocol.
- When a user connects, their username is registered and broadcast to all connected clients.
- Users can send:
  - **Public messages** (seen by everyone)
  - **Private messages** (sent directly to another user)
- All messages and user sessions are managed on the server side.
- The **frontend** uses SockJS and STOMP.js to connect, send, and receive messages in real time.
- The UI updates instantly for all users, showing new messages and online users.
- Message history is loaded when a user connects.

## Getting Started

### Prerequisites

- Java 17+ (or compatible with your Spring Boot version)
- Maven or Gradle
- MySQL (or H2/PostgreSQL for development)

### Setup

1. **Clone the repository:**
    ```
    git clone https://github.com/ManojAcharya08/WebSocket.git
    cd WebSocket
    ```

2. **Configure your database:**
    - Edit `src/main/resources/application.properties` with your DB credentials.

3. **Build and run:**
    ```
    ./mvnw spring-boot:run
    # or
    ./gradlew bootRun
    ```

4. **Open the chat UI:**
    ```
    http://localhost:8080/index.html
    ```

## Usage

- Enter your name and click **Connect**.
- Type a message and click **Send**.
- Leave "Send to" blank for public messages, or enter a username for private chat.
- Disconnect and reconnect as needed.
- The UI automatically switches between light and dark mode based on your system settings.

## Database Reset (Development Only)

To clear all messages and reset IDs in MySQL:

# DevConnector 2.0

A high-performance, full-stack social networking application specifically engineered for the developer community. This platform facilitates professional networking, portfolio showcasing, and technical discussion through a robust integrated ecosystem.

## Technical Architecture

The application utilizes a decoupled architecture to ensure scalability and maintainability:

*   **Backend**: Developed with Java 21 and Spring Boot 3.4.5, implementing a RESTful API architecture. It features Spring Security with stateless JWT authentication and Spring Data JPA for object-relational mapping.
*   **Frontend**: A responsive Single Page Application (SPA) built with React 18, utilizing Redux for centralized state management and Axios for asynchronous API communication.
*   **Database**: PostgreSQL serves as the relational database management system, ensuring data persistence and integrity.

## Core Functional Capabilities

### 1. Identity and Security
*   **JWT Authentication**: Implements secure, token-based authentication with custom filters to protect sensitive endpoints.
*   **Profile Management**: Supports comprehensive developer profiles including professional status, skill sets, and biography.

### 2. Social Connectivity
*   **Nested Social Mappings**: Integration of social media platforms (LinkedIn, Twitter, GitHub, etc.) via an embedded data structure for streamlined data handling.
*   **Professional Showcases**: Automated retrieval of public repositories via the GitHub API to display real-time technical contributions.

### 3. Data Integrity and Lifecycle
*   **Nuclear Account Wipe**: An advanced deletion mechanism that executes cascading operations across profiles, posts, comments, and likes, ensuring the complete removal of orphaned data.
*   **Media Integration**: A dynamic avatar system allowing users to upload and persist personalized imagery via base64 encoding.

## Installation and Configuration

### Backend Setup
1. Navigate to the `devconnector-api` directory.
2. Configure database credentials in `src/main/resources/application.properties`.
3. Execute the command: `./mvnw spring-boot:run`

### Frontend Setup
1. Navigate to the client directory.
2. Install dependencies: `npm install`
3. Launch the development server: `npm start`

---
© 2026 Developer Name. Distributed under the MIT License.
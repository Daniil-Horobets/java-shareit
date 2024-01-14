# ShareIt

ShareIt is a collaborative item-sharing application designed to facilitate users in borrowing and lending various items within a community. 

## Features

- **Share Items**: Users can share items with others in their community.
- **Request Items**: Users can create requests for specific items they need.
- **Respond to Requests**: Users can respond to item requests by offering their items.
- **Book Items**: Users can book items they wish to borrow.
- **Pagination**: The application incorporates pagination to manage large sets of data effectively.

## Project Structure

The ShareIt project is divided into two main components:

- **ShareIt Server**: Houses the core application logic, manages interactions with the database (PostgreSQL), and handles item creation, requests, bookings, and related functionalities. The server is implemented in ShareItServer.java.
- **ShareIt Gateway**: Acts as a lightweight gateway for user interactions, validates incoming requests to ensure data integrity, and communicates with ShareIt Server through REST to execute validated operations. The gateway is implemented in ShareItGateway.java.

## Microservices Architecture

The decision to adopt a microservices architecture aims to address performance issues arising from invalid requests. The gateway (ShareIt Gateway) serves as a filter, validating and excluding incorrect or duplicate requests before reaching the main application (ShareIt Server). This separation enhances the overall efficiency of the application.

## Technical Considerations

- **Docker Integration**: Both ShareIt Server and ShareIt Gateway can be deployed using Docker. Docker containers provide encapsulation, making it easier to manage dependencies and ensuring consistent deployment across different environments. The Docker configurations are provided in [server Dockerfile](server/Dockerfile) and [gateway Dockerfile](gateway/Dockerfile).
- **Maven Multi-Module Project**: The project follows a Maven multi-module structure, simplifying the build process. A single Maven command compiles and builds both ShareIt Server and ShareIt Gateway, promoting consistency in the development workflow. The Maven configurations are provided in server pom.xml, gateway pom.xml, and root pom.xml.
- **REST Communication**: RESTful communication between ShareIt Server and ShareIt Gateway is facilitated through the BaseClient class, streamlining the integration process. This allows for efficient communication while maintaining modularity between the components.

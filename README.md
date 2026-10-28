# Train Ticket Reservation Application

A Java-based console application for managing train reservations, ticket booking, and cancellations. The project is structured with layered components for UI, services, repositories, models, DTOs, and exceptions, making it a good example of a small reservation-system design in Java.

## Features

- User registration and login
- Train search by source and destination
- Seat availability and fare calculation
- Ticket booking with passenger details
- PNR-based booking lookup
- Cancellation of full or partial bookings
- Waiting list and RAC handling
- Admin passenger listing and management view
- In-memory data storage by default

## Tech Stack

- Java
- Console-based UI
- Object-oriented design with service and repository layers
- In-memory repositories for runtime data
- JDBC/MySQL support present in repository implementations, though the default application uses in-memory storage

## Project Structure

```text
TrainTicketReservationApplication/
├── src/
│   ├── Main.java
│   ├── dto/
│   ├── exception/
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── ui/
│   └── utils/
├── .gitignore
├── README.md
├── TicketReservationSystem.iml
├── src.zip
└── .idea/
```

## Key Components

- `src/Main.java` – application bootstrap and wiring of repositories and services
- `src/ui/` – console screens for user and admin interactions
- `src/service/` – booking, cancellation, user, admin, and train logic
- `src/repository/` – repository interfaces and implementations
- `src/model/` – train, station, booking, passenger, and user domain models
- `src/dto/` – request and response data objects
- `src/exception/` – custom exceptions for domain validation and errors
- `src/utils/` – helper classes such as input and session utilities

## Getting Started

### Prerequisites

- Java Development Kit (JDK) installed
- IntelliJ IDEA or any Java IDE (optional)

### Run with IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Import the project or open the folder containing the source files.
3. Set the project SDK to a valid JDK.
4. Run `src/Main.java`.

### Run from the Command Line

From the project root:

#### Linux/macOS

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out Main
```

#### Windows (PowerShell)

```powershell
javac -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out Main
```

## Application Flow

1. The user starts the console application from `Main`.
2. The main menu allows login, registration, booking, cancellation, or admin actions.
3. Users can search trains by route and select a travel class.
4. Booking logic checks seat availability and assigns seats, RAC, or waiting-list status when needed.
5. Bookings can be viewed by PNR or user account and cancelled as needed.

## Notes

- This project currently uses in-memory repositories for runtime data, so data resets when the program exits.
- A database layer and MySQL-based repository implementations are present in the project, but they are not the default runtime configuration.
- There is no Maven or Gradle build file in the repository, so this project is primarily run as a simple Java console app.

## License

This project does not include a license file. If you plan to reuse or distribute the code, add an appropriate open-source license before publishing.

## Contributing

Contributions are welcome. You can:

- Improve validation and error handling
- Add a persistent database layer
- Add test cases
- Enhance the UI and input validation
- Refactor the codebase for cleaner architecture

## Contact

For project-related questions or collaboration, open an issue in the repository or contact the project maintainer.

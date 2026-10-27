# Train Ticket Reservation System

A Java console application for searching trains and managing ticket bookings. The code is organized into UI, service, repository, model, and DTO layers.

## Quick start

### IntelliJ IDEA

Open `TicketReservationSystem.iml` (or the project folder), set a JDK, and run `src/Main.java`.

### Command line

From the project root, compile and run with a JDK installed:

```powershell
javac -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out Main
```

There is no Maven or Gradle build file in this project. The current `Main` creates **in-memory repositories**, so data is held in process memory and resets when the application exits. `src.zip` is an archive copy of source files.

## Start here

- [`src/Main.java`](src/Main.java) wires repositories, services, UI objects, then starts the menu.
- [`src/ui/MainMenuUi.java`](src/ui/MainMenuUi.java) is the console menu and dispatches user actions.
- [`src/service/BookingService.java`](src/service/BookingService.java) contains train search for a route, seat availability/pricing, ticket booking, passenger detail assembly, and PNR generation.
- [`src/service/CancellationService.java`](src/service/CancellationService.java) handles full/partial cancellation and promotes waiting-list passengers.
- [`src/utils/SessionStorage.java`](src/utils/SessionStorage.java) stores the current logged-in user for the process.
- [`src/utils/InputHandler.java`](src/utils/InputHandler.java) reads and validates console input.

## Project map

| Path | Contents |
| --- | --- |
| `src/Main.java` | Application composition root |
| `src/ui/` | Console screens and user interaction (`MainMenuUi`, `BookingUi`, `CancellationUi`, `UserUi`, `AdminUi`) |
| `src/service/` | Business logic for users, trains, booking, cancellation, and admin views |
| `src/repository/` | Repository interfaces for users, bookings, passengers, seats, stations, trains, and waiting lists |
| `src/repository/Implementation/InMemory/` | In-process repository implementations used by `Main` |
| `src/repository/Implementation/DataBase/` | JDBC/MySQL repository implementations; not selected by `Main` |
| `src/model/` | Core entities and enums: `User`, `Train`, `Station`, `Seat`, `Booking`, `Passenger`, statuses and types |
| `src/dto/` | Request/response data passed between UI, services, and repositories |
| `src/exception/` | Domain-specific exception types |
| `src/utils/` | Input, session, and database utilities |
| `TicketReservationSystem.iml` | IntelliJ module configuration (includes a MySQL connector library entry) |

## Main user flows

1. Register or log in from the main menu. The admin passenger view recognizes the account `admin@gmail.com` as admin.
2. Choose **Book Tickets**, enter departure and arrival station codes, select a train/class, and enter passenger details. If there is no direct train, the UI offers an advanced multi-hop search.
3. Booking assigns confirmed seats where available, then RAC/wait-list positions when those queues have capacity. The service creates a PNR and returns the booking summary.
4. **Show Bookings** and **Show Booking Details By PNR** display booking information. **Cancel Tickets** supports full or passenger-level cancellation; released capacity can promote queued passengers.
5. The admin passenger listing is restricted to the admin email above.

## Persistence and database configuration

The default application is in-memory. To use persistence, `Main` would need to construct the `DataBase` repository implementations instead. `DbUtils` currently connects to `jdbc:mysql://127.0.0.1:3306/train` with username `root` and password `root`; configure appropriate credentials and a matching MySQL schema before enabling those repositories. The IntelliJ module references a MySQL Connector/J library, but no portable dependency/build configuration or schema file is included.

## BookingService quick reference

- `getAllTrains(from, to)` finds trains serving the route and builds train/seat availability responses.
- `bookTickets(trainId, seatType, from, to, passengers)` checks capacity, creates a booking/PNR, assigns confirmed seats and queues remaining passengers.
- `getAllBookings()` and `getActiveBookings()` return the current user's bookings.
- `getBookingDetails(pnr)` loads a booking and assembles passenger and seat/queue details.
- `gatherPassengerDetails(booking, trainId)` maps stored passengers into display DTOs.
- `calculatePrice(seatType, from, to, count)` calculates fare from route distance and class multiplier.

## Notes

- Input is interactive; menu choices are entered in the console.
- The database code is present but is not the runtime configuration in `Main`.
- No automated test sources or build-tool test task are currently included.

# Online Reservation System

A desktop train-ticket reservation system built with **Java Swing**, **JDBC** and **SQLite**.
Users log in, book tickets (a unique PNR is generated), and cancel bookings using the PNR.

**Author:** Alat Nandu Kumar  |  **Task 1** – Online Reservation System

## Screenshots

| Login | Access denied |
|---|---|
| ![Login](docs/1_login.png) | ![Denied](docs/2_login_denied.png) |

| Reservation form (train name auto-fills) | Input validation |
|---|---|
| ![Booking](docs/3_booking_form.png) | ![Validation](docs/4_validation_error.png) |

| Booking confirmation with PNR | Cancellation: fetch by PNR |
|---|---|
| ![Confirmation](docs/5_booking_confirmation.png) | ![Fetch](docs/6_cancel_fetch.png) |

| "Are you sure?" dialog | Cancelled |
|---|---|
| ![Confirm](docs/7_cancel_confirm.png) | ![Done](docs/8_cancel_success.png) |

## Features

- **Login form** – username + password; invalid credentials show "Access Denied".
- **Reservation form** – passenger name, train number, train name (auto-populated from the train number), class type, date of journey, source and destination station.
- **Book button** – saves to the database and generates a unique 10-digit **PNR**.
- **Confirmation dialog** – shows all booking details after a successful reservation.
- **Cancellation form** – enter a PNR, click **Fetch** to see the full booking.
- **Confirm cancellation** – "Are you sure?" dialog; the booking is deleted only on **Yes**.
- **Input validation** – no empty required fields, numeric train number, strict `yyyy-MM-dd` date (rejects impossible dates like 2027-02-30 and dates in the past), source ≠ destination, 10-digit PNR.

### Security & quality notes
- Every SQL statement uses `PreparedStatement` (prevents SQL injection).
- Passwords are stored as **salted SHA-256 hashes**, never as plain text.
- Connections are opened with try-with-resources so they always close.
- UI code (`ui`), database code (`dao`, `db`), models and utilities are separated into packages.

## Tech stack

Java 17+ · Swing · JDBC · SQLite (`org.xerial:sqlite-jdbc`) · Maven

## Project structure

```
src/main/java/com/reservation/
├── Main.java
├── db/DatabaseHelper.java        connection, table creation, sample data
├── model/Reservation.java
├── dao/UserDAO.java, TrainDAO.java, ReservationDAO.java
├── util/PasswordUtil.java, Validator.java
└── ui/LoginFrame.java, MainFrame.java, ReservationPanel.java, CancellationPanel.java
```

## How to run

**Requirements:** JDK 17 or newer. Maven is optional (see option B).

### Option A – Maven
```bash
mvn clean package
java -jar target/online-reservation-system-1.0.jar
```

### Option B – No Maven (plain `javac`)
Download `sqlite-jdbc-3.44.1.0.jar` and `slf4j-api-1.7.36.jar` from Maven Central into a `lib/` folder, then:
```bash
# Linux / macOS
javac -d out -cp "lib/*" $(find src -name "*.java")
java -cp "out:lib/*" com.reservation.Main

# Windows (PowerShell)
javac -d out -cp "lib/*" (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "out;lib/*" com.reservation.Main
```

### Option C – IDE
Open the folder as a Maven project in IntelliJ IDEA / Eclipse / VS Code and run `com.reservation.Main`.

The SQLite file `reservation.db` is created automatically in the working directory on first launch.

## Default login

| Username | Password |
|---|---|
| `admin` | `admin123` |

## Sample trains (for the auto-populate feature)

12723 Telangana Express · 12701 Hussainsagar Express · 12759 Charminar Express · 12797 Venkatadri Express ·
12616 Grand Trunk Express · 12627 Karnataka Express · 12951 Mumbai Rajdhani Express ·
12301 Howrah Rajdhani Express · 12009 Shatabdi Express · 12839 Howrah Mail
*(sample data for demonstration; train numbers/names are illustrative).*

## Database schema

```sql
users(username PK, salt, password_hash)
trains(train_no PK, train_name)
reservations(pnr PK, passenger_name, train_no, train_name, class_type,
             journey_date, source, destination)
```

## Testing

The following were tested end to end (login, validation, booking, PNR uniqueness, fetch, cancel, edge cases):
wrong/empty credentials, SQL-injection attempts on login, empty fields, non-numeric train number,
wrong date format, impossible date, past date, same source/destination, unknown train, unknown PNR,
answering **No** to the cancel prompt, cancelling the same PNR twice, and 200 consecutive bookings producing 200 unique PNRs.

# CricketPulse — Real-Time Cricket Score Management

CricketPulse is a React + TypeScript frontend and Spring Boot REST/WebSocket backend for managing cricket matches, scoring deliveries, and viewing live scorecards.

## Prerequisites

- JDK 17 or newer compatible with this project (the backend Maven configuration currently targets Java 17; Java 26 can compile Java 17-targeted source, but configure Eclipse's project JDK correctly).
- Node.js LTS and npm.
- MySQL 8.x or compatible MySQL (XAMPP is supported).
- Eclipse IDE for the backend. VS Code is convenient for the frontend.

## 1. Database setup (XAMPP)

1. Start **MySQL** in XAMPP.
2. Open phpMyAdmin at `http://localhost/phpmyadmin`.
3. Create a database named `cricketpulse_db`, or execute:

```sql
CREATE DATABASE cricketpulse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The backend configuration defaults to `root` with an empty password, as commonly configured in local XAMPP. If yours differs, update `cricketpulse-backend/src/main/resources/application.properties` or use environment variables. Hibernate is configured to create/update tables when the application starts.

Optional SQL files are in `database/`. Review `seed.sql` before executing it.

## 2. Run the backend

### Eclipse

1. Extract this project folder.
2. In Eclipse, choose **File → Import → Maven → Existing Maven Projects**.
3. Select the `cricketpulse-backend` folder (the folder containing `pom.xml`).
4. Finish and wait for Maven dependencies to download.
5. Set the project JDK/build path to an installed JDK compatible with Java 17 target.
6. Run `com.cricketpulse.CricketPulseApplication` as **Java Application**.

### Windows terminal alternative

Open PowerShell in `CricketPulse\cricketpulse-backend` and run:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend is configured for port `8080`.

- Health: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

The database must be running first. If startup fails, check the database name, MySQL credentials, port conflicts, and the console error.

## 3. Run the frontend

Open a terminal in `CricketPulse\cricketpulse-frontend` and run:

```powershell
npm install
npm run dev
```

Open the local URL printed by Vite, normally `http://localhost:5173`.

The repository intentionally does not ship `node_modules`; install dependencies on your own computer so npm obtains the correct native packages for your operating system. The included `.env` is for local development; review it before use. `.env.example` documents the expected configuration.

For a production build:

```powershell
npm run build
```

## 4. Useful configuration

Backend settings are in `cricketpulse-backend/src/main/resources/application.properties`:
- MySQL URL, username and password
- server port
- JWT signing secret and expiry
- allowed frontend origins
- simulator interval
- external provider settings

Do not use development secrets in a public deployment. Configure a strong secret outside source control for any real deployment.

Frontend API configuration is in `cricketpulse-frontend/.env` / `.env.example`.

## 5. Eclipse and Java version note

The project `pom.xml` currently targets Java 17. A newer JDK such as Java 26 can generally compile source targeting Java 17, but Eclipse must point to a valid installed JDK and Maven dependencies must download successfully. You do not have to select Java 26 in Spring Initializr to run this existing project.

## 6. Troubleshooting

- **Port 8080 is busy:** change `server.port` in `application.properties`.
- **Cannot connect to MySQL:** start MySQL in XAMPP and verify credentials/database name.
- **Frontend cannot reach backend:** ensure backend is running on port 8080 and check CORS/API base URL configuration.
- **npm/Vite native binding errors:** delete `node_modules` and run `npm install` again in the frontend directory. Do not copy `node_modules` from a different operating system.
- **Maven download failure:** verify internet access and retry Maven import/update.
- **Authentication fails:** inspect the backend response and ensure the frontend is using the expected auth endpoints.

## 7. Current verification status

The frontend TypeScript project check (`tsc -b`) passed after fixing incorrect page import paths and the entry-point import. A production Vite build could not be verified in the provided Linux environment because the uploaded `node_modules` contained platform-specific/missing Rolldown native bindings; run `npm install` on your Windows computer and then `npm run build`. Backend tests could not be run in this environment because Maven wrapper distribution download failed. Verify the backend in Eclipse after dependencies are downloaded.

This documentation does not claim the whole application has passed end-to-end testing. Confirm database, login, scoring, simulation, and WebSocket flows locally before presenting it as complete.

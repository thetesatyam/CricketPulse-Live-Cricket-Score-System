# CricketPulse REST API Documentation

CricketPulse exposes OpenAPI 3 documentation at `/swagger-ui/index.html` (or `/swagger-ui.html`) and raw JSON specs at `/api-docs`.

## 1. Authentication Endpoints

### POST `/api/auth/register`
Registers a new user. Public registration always assigns `ROLE_VIEWER`.
- **Payload**:
  ```json
  {
    "username": "scorer_demo",
    "email": "scorer@cricketpulse.io",
    "password": "password123"
  }
  ```
- **Response**: `200 OK` with `UserDto`.

### POST `/api/auth/login`
Authenticates a user and returns a signed JWT token.
- **Payload**:
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Login successful",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "id": 1,
      "username": "admin",
      "role": "ROLE_ADMIN"
    }
  }
  ```

### GET `/api/auth/me`
Returns currently authenticated user profile.
- **Headers**: `Authorization: Bearer <token>`

---

## 2. Matches Endpoints

- `GET /api/matches`: Lists all matches ordered by start time.
- `GET /api/matches/live`: Lists active live matches.
- `GET /api/matches/upcoming`: Lists scheduled fixtures.
- `GET /api/matches/completed`: Lists completed matches.
- `GET /api/matches/{matchId}`: Returns detailed match state including live striker, non-striker, bowler, and recent deliveries.
- `GET /api/matches/{matchId}/scorecard`: Full official batting and bowling scorecards.
- `GET /api/matches/{matchId}/commentary`: Ball-by-ball commentary stream.
- `POST /api/matches`: *(Admin only)* Schedule a new match.

---

## 3. Scoring Console Endpoints

Requires `ROLE_SCORER` or `ROLE_ADMIN`.

### POST `/api/scoring/innings/{inningsId}/deliveries`
Records a live delivery.
- **Payload**:
  ```json
  {
    "strikerId": 1,
    "nonStrikerId": 2,
    "bowlerId": 19,
    "batterRuns": 1,
    "extraType": "NONE",
    "extraRuns": 0,
    "isWicket": false,
    "wicketType": null,
    "dismissedPlayerId": null,
    "commentary": "Pushed to mid-on for a brisk single."
  }
  ```

### POST `/api/scoring/deliveries/corrections`
Applies an auditable correction / reversal to a previous delivery.
- **Payload**:
  ```json
  {
    "deliveryId": 142,
    "reason": "Umpire reversed decision upon DRS review"
  }
  ```

---

## 4. Match Simulation Endpoints

Requires `ROLE_SCORER` or `ROLE_ADMIN`.
- `POST /api/simulation/matches/{matchId}/start?intervalMs=2000`: Starts autonomous simulation loop.
- `POST /api/simulation/matches/{matchId}/pause`: Pauses simulation loop.
- `POST /api/simulation/matches/{matchId}/stop`: Halts simulation loop.
- `GET /api/simulation/matches/{matchId}/status`: Returns `{ "matchId": 2, "running": true }`.

---

## 5. Teams & Players Endpoints

- `GET /api/teams`: List all teams.
- `GET /api/teams/{teamId}/players`: List squad for team.
- `GET /api/players/{playerId}/statistics`: Player tournament career stats.
- `GET /api/players/top-scorers`: Top 10 run scorers leaderboard.
- `GET /api/players/top-wickets`: Top 10 wicket takers leaderboard.

---

## 6. Administration Endpoints

Requires `ROLE_ADMIN`.
- `GET /api/admin/users`: List all registered accounts.
- `PATCH /api/admin/users/{userId}/role`: Promote user (e.g. `ROLE_SCORER` or `ROLE_ADMIN`).
- `GET /api/admin/audit-logs`: Audit trail of all administrative and scoring operations.

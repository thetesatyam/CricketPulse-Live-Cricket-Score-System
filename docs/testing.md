# CricketPulse Testing & Verification Guide

## 1. Automated Test Strategy

CricketPulse includes unit and integration tests covering security, cricket scoring mathematics, strike rotation, dismissal credit, and database persistence.

### Running Backend Tests
Execute the Maven test suite using the included Maven wrapper:

```bash
cd CricketPulse/cricketpulse-backend
.\mvnw.cmd test
```

### Key Test Suites:
1. **`ScoringEngineTest`**:
   - `testSingleRunAndStrikeRotation`: Validates score increment and strike rotation on odd runs.
   - `testBoundaryFour`: Validates boundary 4 and striker retention.
   - `testWideDelivery`: Validates extra run addition without consuming a legal ball.
   - `testEndOfOverStrikeRotation`: Validates over completion (6 legal balls) end swap.
   - `testWicketDelivery`: Validates bowler credit and wicket counter increment.
   - `testTargetReachedInSecondInnings`: Validates match win calculation when target is chased down.
   - `testAuditableCorrection`: Validates reversal of erroneous delivery and automatic recalculation of innings state.
2. **`AuthServiceTest`**:
   - Validates public registration assigns least-privileged `ROLE_VIEWER`.
   - Validates BCrypt password hashing.
   - Validates JWT token generation upon successful login.
3. **`CricketPulseApplicationTests`**:
   - Tests complete Spring context boot using an in-memory H2 database.

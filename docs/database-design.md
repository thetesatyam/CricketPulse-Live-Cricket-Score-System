# CricketPulse Database Design & Entity Relationship

## 1. Entity Relationship (ER) Diagram

```mermaid
erDiagram
    USERS {
        bigint id PK
        varchar username UK
        varchar email UK
        varchar password_hash
        varchar role
        boolean enabled
        datetime created_at
    }

    TEAMS {
        bigint id PK
        varchar name UK
        varchar short_name UK
        varchar country
        varchar logo_url
        datetime created_at
    }

    PLAYERS {
        bigint id PK
        varchar full_name
        bigint team_id FK
        varchar batting_style
        varchar bowling_style
        varchar playing_role
        varchar profile_image_url
    }

    TOURNAMENTS {
        bigint id PK
        varchar name
        varchar season
        date start_date
        date end_date
        varchar status
    }

    VENUES {
        bigint id PK
        varchar name
        varchar city
        varchar country
        int capacity
    }

    MATCHES {
        bigint id PK
        varchar external_provider_id
        bigint tournament_id FK
        bigint venue_id FK
        bigint home_team_id FK
        bigint away_team_id FK
        varchar match_type
        int overs_per_innings
        datetime scheduled_start_time
        bigint toss_winner_id FK
        varchar toss_decision
        varchar status
        int current_innings_number
        varchar result
        bigint winner_team_id FK
        bigint version
    }

    MATCH_PLAYERS {
        bigint id PK
        bigint match_id FK
        bigint team_id FK
        bigint player_id FK
        boolean is_captain
        boolean is_wicket_keeper
        int batting_order
    }

    INNINGS {
        bigint id PK
        bigint match_id FK
        int innings_number
        bigint batting_team_id FK
        bigint bowling_team_id FK
        int total_runs
        int wickets
        int legal_balls
        int extras
        int wide_runs
        int no_ball_runs
        int bye_runs
        int leg_bye_runs
        int target
        varchar status
    }

    DELIVERIES {
        bigint id PK
        bigint innings_id FK
        int sequence_number
        int over_number
        int ball_number
        bigint striker_id FK
        bigint non_striker_id FK
        bigint bowler_id FK
        int batter_runs
        varchar extra_type
        int extra_runs
        boolean is_legal_delivery
        boolean is_wicket
        varchar wicket_type
        bigint dismissed_player_id FK
        bigint fielder_id FK
        varchar commentary
        boolean is_reversed
        varchar reversal_reason
    }

    PLAYER_MATCH_STATS {
        bigint id PK
        bigint match_id FK
        bigint player_id FK
        bigint team_id FK
        int runs_scored
        int balls_faced
        int fours
        int sixes
        boolean is_out
        varchar dismissal_info
        decimal strike_rate
        int legal_balls_bowled
        varchar overs_bowled
        int runs_conceded
        int wickets_taken
        decimal economy_rate
    }

    AUDIT_LOGS {
        bigint id PK
        varchar action
        varchar entity_name
        bigint entity_id
        varchar performed_by
        text details
        varchar ip_address
        datetime created_at
    }

    TEAMS ||--o{ PLAYERS : "rosters"
    TEAMS ||--o{ MATCHES : "hosts / visits"
    TOURNAMENTS ||--o{ MATCHES : "includes"
    VENUES ||--o{ MATCHES : "hosts"
    MATCHES ||--o{ MATCH_PLAYERS : "squad lineup"
    PLAYERS ||--o{ MATCH_PLAYERS : "plays in"
    MATCHES ||--o{ INNINGS : "contains"
    INNINGS ||--o{ DELIVERIES : "consists of"
    PLAYERS ||--o{ DELIVERIES : "striker / bowler"
    MATCHES ||--o{ PLAYER_MATCH_STATS : "records"
    PLAYERS ||--o{ PLAYER_MATCH_STATS : "performance"
```

---

## 2. Indexing Strategy for Performance

- `deliveries(innings_id, sequence_number)`: Enables instantaneous retrieval of delivery sequences and commentary feeds.
- `matches(status, scheduled_start_time)`: Optimizes dashboard queries for live, upcoming, and completed matches.
- `player_match_stats(match_id, player_id)`: Enforces unique constraint per match-player and speeds up scorecard lookups.
- `users(username)` & `users(email)`: Enforces unique credentials and accelerates JWT authentication.

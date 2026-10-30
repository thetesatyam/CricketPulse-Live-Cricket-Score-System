-- CricketPulse Seed Data
USE cricketpulse_db;

-- Users (BCrypt hash for password: 'password123' -> $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.KJHTEQGShzmUy)
INSERT INTO users (username, email, password_hash, role, enabled) VALUES
('admin', 'admin@cricketpulse.io', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.KJHTEQGShzmUy', 'ADMIN', true),
('scorer1', 'scorer@cricketpulse.io', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.KJHTEQGShzmUy', 'SCORER', true),
('viewer1', 'viewer@cricketpulse.io', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.KJHTEQGShzmUy', 'VIEWER', true)
ON DUPLICATE KEY UPDATE id=id;

-- Teams
INSERT INTO teams (id, name, short_name, country, logo_url) VALUES
(1, 'India', 'IND', 'India', 'https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?auto=format&fit=crop&w=128&q=80'),
(2, 'Australia', 'AUS', 'Australia', 'https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=128&q=80'),
(3, 'England', 'ENG', 'England', 'https://images.unsplash.com/photo-1517649763962-0c623266ddc0?auto=format&fit=crop&w=128&q=80'),
(4, 'South Africa', 'SA', 'South Africa', 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=128&q=80')
ON DUPLICATE KEY UPDATE id=id;

-- Venues
INSERT INTO venues (id, name, city, country, capacity) VALUES
(1, 'Wankhede Stadium', 'Mumbai', 'India', 33108),
(2, 'Melbourne Cricket Ground', 'Melbourne', 'Australia', 100024),
(3, 'Lord''s Cricket Ground', 'London', 'England', 31100),
(4, 'Newlands Cricket Ground', 'Cape Town', 'South Africa', 25000)
ON DUPLICATE KEY UPDATE id=id;

-- Tournaments
INSERT INTO tournaments (id, name, season, start_date, end_date, status) VALUES
(1, 'ICC T20 World Cup 2026', '2026', '2026-03-01', '2026-04-15', 'ACTIVE')
ON DUPLICATE KEY UPDATE id=id;

-- Players (India squad: 11 players)
INSERT INTO players (id, full_name, team_id, batting_style, bowling_style, playing_role) VALUES
(1, 'Rohit Sharma', 1, 'Right-hand bat', 'Right-arm offbreak', 'BATTER'),
(2, 'Virat Kohli', 1, 'Right-hand bat', 'Right-arm medium', 'BATTER'),
(3, 'Suryakumar Yadav', 1, 'Right-hand bat', 'Right-arm offbreak', 'BATTER'),
(4, 'Rishabh Pant', 1, 'Left-hand bat', 'None', 'WICKET_KEEPER'),
(5, 'Hardik Pandya', 1, 'Right-hand bat', 'Right-arm fast-medium', 'ALL_ROUNDER'),
(6, 'Ravindra Jadeja', 1, 'Left-hand bat', 'Slow left-arm orthodox', 'ALL_ROUNDER'),
(7, 'Axar Patel', 1, 'Left-hand bat', 'Slow left-arm orthodox', 'ALL_ROUNDER'),
(8, 'Jasprit Bumrah', 1, 'Right-hand bat', 'Right-arm fast', 'BOWLER'),
(9, 'Kuldeep Yadav', 1, 'Left-hand bat', 'Left-arm wrist spin', 'BOWLER'),
(10, 'Mohammed Siraj', 1, 'Right-hand bat', 'Right-arm fast', 'BOWLER'),
(11, 'Arshdeep Singh', 1, 'Left-hand bat', 'Left-arm fast-medium', 'BOWLER')
ON DUPLICATE KEY UPDATE id=id;

-- Players (Australia squad: 11 players)
INSERT INTO players (id, full_name, team_id, batting_style, bowling_style, playing_role) VALUES
(12, 'Travis Head', 2, 'Left-hand bat', 'Right-arm offbreak', 'BATTER'),
(13, 'David Warner', 2, 'Left-hand bat', 'Right-arm legbreak', 'BATTER'),
(14, 'Mitchell Marsh', 2, 'Right-hand bat', 'Right-arm medium', 'ALL_ROUNDER'),
(15, 'Glenn Maxwell', 2, 'Right-hand bat', 'Right-arm offbreak', 'ALL_ROUNDER'),
(16, 'Marcus Stoinis', 2, 'Right-hand bat', 'Right-arm medium', 'ALL_ROUNDER'),
(17, 'Tim David', 2, 'Right-hand bat', 'Right-arm offbreak', 'BATTER'),
(18, 'Matthew Wade', 2, 'Left-hand bat', 'None', 'WICKET_KEEPER'),
(19, 'Pat Cummins', 2, 'Right-hand bat', 'Right-arm fast', 'BOWLER'),
(20, 'Mitchell Starc', 2, 'Left-hand bat', 'Left-arm fast', 'BOWLER'),
(21, 'Adam Zampa', 2, 'Right-hand bat', 'Right-arm legbreak', 'BOWLER'),
(22, 'Josh Hazlewood', 2, 'Left-hand bat', 'Right-arm fast-medium', 'BOWLER')
ON DUPLICATE KEY UPDATE id=id;

-- Matches:
-- Match 1: Completed Match (IND vs AUS - T20 World Cup Final)
INSERT INTO matches (id, tournament_id, venue_id, home_team_id, away_team_id, match_type, overs_per_innings, scheduled_start_time, toss_winner_id, toss_decision, status, current_innings_number, result, winner_team_id)
VALUES (1, 1, 1, 1, 2, 'T20', 20, '2026-03-20 19:30:00', 1, 'BAT', 'COMPLETED', 2, 'India won by 15 runs', 1)
ON DUPLICATE KEY UPDATE id=id;

-- Innings 1 (IND batting)
INSERT INTO innings (id, match_id, innings_number, batting_team_id, bowling_team_id, total_runs, wickets, legal_balls, extras, wide_runs, no_ball_runs, bye_runs, leg_bye_runs, target, status)
VALUES (1, 1, 1, 1, 2, 176, 7, 120, 8, 4, 1, 1, 2, NULL, 'COMPLETED')
ON DUPLICATE KEY UPDATE id=id;

-- Innings 2 (AUS batting)
INSERT INTO innings (id, match_id, innings_number, batting_team_id, bowling_team_id, total_runs, wickets, legal_balls, extras, wide_runs, no_ball_runs, bye_runs, leg_bye_runs, target, status)
VALUES (2, 1, 2, 2, 1, 161, 8, 120, 6, 3, 0, 1, 2, 177, 'COMPLETED')
ON DUPLICATE KEY UPDATE id=id;

-- Match 2: Live Match for Simulator & Scoring (IND vs AUS at MCG)
INSERT INTO matches (id, tournament_id, venue_id, home_team_id, away_team_id, match_type, overs_per_innings, scheduled_start_time, toss_winner_id, toss_decision, status, current_innings_number, result, winner_team_id)
VALUES (2, 1, 2, 2, 1, 'T20', 20, NOW(), 2, 'BAT', 'LIVE', 1, NULL, NULL)
ON DUPLICATE KEY UPDATE id=id;

-- Innings for Match 2 (AUS batting first)
INSERT INTO innings (id, match_id, innings_number, batting_team_id, bowling_team_id, total_runs, wickets, legal_balls, extras, wide_runs, no_ball_runs, bye_runs, leg_bye_runs, target, status)
VALUES (3, 2, 1, 2, 1, 28, 0, 18, 2, 2, 0, 0, 0, NULL, 'IN_PROGRESS')
ON DUPLICATE KEY UPDATE id=id;

-- Match 3: Upcoming Match (ENG vs SA at Lord's)
INSERT INTO matches (id, tournament_id, venue_id, home_team_id, away_team_id, match_type, overs_per_innings, scheduled_start_time, toss_winner_id, toss_decision, status, current_innings_number, result, winner_team_id)
VALUES (3, 1, 3, 3, 4, 'T20', 20, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NULL, 'SCHEDULED', 1, NULL, NULL)
ON DUPLICATE KEY UPDATE id=id;

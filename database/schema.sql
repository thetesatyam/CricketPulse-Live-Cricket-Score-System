-- CricketPulse: Real-Time Cricket Score Management System
-- Database Schema (MySQL 8.x / MariaDB compatible)

CREATE DATABASE IF NOT EXISTS cricketpulse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cricketpulse_db;

-- Users table with role-based access control
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'VIEWER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- Teams table
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    short_name VARCHAR(10) NOT NULL UNIQUE,
    country VARCHAR(100) NOT NULL,
    logo_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Players table
CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    team_id BIGINT,
    batting_style VARCHAR(50),
    bowling_style VARCHAR(50),
    playing_role VARCHAR(50) NOT NULL, -- BATTER, BOWLER, ALL_ROUNDER, WICKET_KEEPER
    profile_image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    INDEX idx_player_team (team_id),
    INDEX idx_player_role (playing_role)
) ENGINE=InnoDB;

-- Tournaments table
CREATE TABLE IF NOT EXISTS tournaments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    season VARCHAR(20) NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Venues table
CREATE TABLE IF NOT EXISTS venues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    capacity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Matches table
CREATE TABLE IF NOT EXISTS matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_provider_id VARCHAR(100) NULL,
    tournament_id BIGINT,
    venue_id BIGINT,
    home_team_id BIGINT NOT NULL,
    away_team_id BIGINT NOT NULL,
    match_type VARCHAR(20) NOT NULL DEFAULT 'T20', -- T20, ODI, TEST, T10
    overs_per_innings INT NOT NULL DEFAULT 20,
    scheduled_start_time DATETIME NOT NULL,
    toss_winner_id BIGINT NULL,
    toss_decision VARCHAR(10) NULL, -- BAT, BOWL
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, LIVE, INNINGS_BREAK, COMPLETED, ABANDONED
    current_innings_number INT DEFAULT 1,
    result VARCHAR(255) NULL,
    winner_team_id BIGINT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE SET NULL,
    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE SET NULL,
    FOREIGN KEY (home_team_id) REFERENCES teams(id),
    FOREIGN KEY (away_team_id) REFERENCES teams(id),
    FOREIGN KEY (toss_winner_id) REFERENCES teams(id),
    FOREIGN KEY (winner_team_id) REFERENCES teams(id),
    INDEX idx_match_status (status),
    INDEX idx_match_scheduled (scheduled_start_time)
) ENGINE=InnoDB;

-- Match Squad / Players
CREATE TABLE IF NOT EXISTS match_players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    is_captain BOOLEAN DEFAULT FALSE,
    is_wicket_keeper BOOLEAN DEFAULT FALSE,
    batting_order INT DEFAULT 0,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (player_id) REFERENCES players(id),
    UNIQUE KEY uk_match_player (match_id, player_id)
) ENGINE=InnoDB;

-- Innings table
CREATE TABLE IF NOT EXISTS innings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    innings_number INT NOT NULL,
    batting_team_id BIGINT NOT NULL,
    bowling_team_id BIGINT NOT NULL,
    total_runs INT DEFAULT 0,
    wickets INT DEFAULT 0,
    legal_balls INT DEFAULT 0,
    extras INT DEFAULT 0,
    wide_runs INT DEFAULT 0,
    no_ball_runs INT DEFAULT 0,
    bye_runs INT DEFAULT 0,
    leg_bye_runs INT DEFAULT 0,
    penalty_runs INT DEFAULT 0,
    target INT NULL,
    is_declared BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (batting_team_id) REFERENCES teams(id),
    FOREIGN KEY (bowling_team_id) REFERENCES teams(id),
    UNIQUE KEY uk_match_innings (match_id, innings_number),
    INDEX idx_innings_match (match_id)
) ENGINE=InnoDB;

-- Deliveries table (Persistent ball events)
CREATE TABLE IF NOT EXISTS deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    innings_id BIGINT NOT NULL,
    sequence_number INT NOT NULL,
    over_number INT NOT NULL,
    ball_number INT NOT NULL, -- Ball in current over (1..6 for legal)
    striker_id BIGINT NOT NULL,
    non_striker_id BIGINT NOT NULL,
    bowler_id BIGINT NOT NULL,
    batter_runs INT DEFAULT 0,
    extra_type VARCHAR(20) NULL, -- WIDE, NO_BALL, BYE, LEG_BYE, PENALTY, NONE
    extra_runs INT DEFAULT 0,
    is_legal_delivery BOOLEAN DEFAULT TRUE,
    is_wicket BOOLEAN DEFAULT FALSE,
    wicket_type VARCHAR(30) NULL, -- BOWLED, CAUGHT, LBW, RUN_OUT, STUMPED, HIT_WICKET
    dismissed_player_id BIGINT NULL,
    fielder_id BIGINT NULL,
    commentary VARCHAR(500) NULL,
    is_reversed BOOLEAN DEFAULT FALSE,
    reversal_reason VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (innings_id) REFERENCES innings(id) ON DELETE CASCADE,
    FOREIGN KEY (striker_id) REFERENCES players(id),
    FOREIGN KEY (non_striker_id) REFERENCES players(id),
    FOREIGN KEY (bowler_id) REFERENCES players(id),
    FOREIGN KEY (dismissed_player_id) REFERENCES players(id),
    FOREIGN KEY (fielder_id) REFERENCES players(id),
    INDEX idx_delivery_innings (innings_id),
    INDEX idx_delivery_sequence (innings_id, sequence_number)
) ENGINE=InnoDB;

-- Player Match Statistics
CREATE TABLE IF NOT EXISTS player_match_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    -- Batting stats
    runs_scored INT DEFAULT 0,
    balls_faced INT DEFAULT 0,
    fours INT DEFAULT 0,
    sixes INT DEFAULT 0,
    is_out BOOLEAN DEFAULT FALSE,
    dismissal_info VARCHAR(100) NULL,
    strike_rate DECIMAL(6,2) DEFAULT 0.00,
    -- Bowling stats
    legal_balls_bowled INT DEFAULT 0,
    overs_bowled VARCHAR(10) DEFAULT '0.0',
    maiden_overs INT DEFAULT 0,
    runs_conceded INT DEFAULT 0,
    wickets_taken INT DEFAULT 0,
    economy_rate DECIMAL(6,2) DEFAULT 0.00,
    wides_conceded INT DEFAULT 0,
    no_balls_conceded INT DEFAULT 0,
    -- Fielding
    catches INT DEFAULT 0,
    run_outs INT DEFAULT 0,
    stumpings INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id),
    FOREIGN KEY (team_id) REFERENCES teams(id),
    UNIQUE KEY uk_player_match (match_id, player_id),
    INDEX idx_pms_match (match_id),
    INDEX idx_pms_player (player_id)
) ENGINE=InnoDB;

-- Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    performed_by VARCHAR(50) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB;

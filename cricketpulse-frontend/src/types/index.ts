export type Role = 'ROLE_ADMIN' | 'ROLE_SCORER' | 'ROLE_VIEWER';

export type MatchStatus = 'SCHEDULED' | 'LIVE' | 'INNINGS_BREAK' | 'COMPLETED' | 'ABANDONED';
export type MatchType = 'T20' | 'ODI' | 'TEST' | 'T10';
export type ExtraType = 'NONE' | 'WIDE' | 'NO_BALL' | 'BYE' | 'LEG_BYE' | 'PENALTY';
export type WicketType = 'BOWLED' | 'CAUGHT' | 'LBW' | 'RUN_OUT' | 'STUMPED' | 'HIT_WICKET' | 'RETIRED_HURT';
export type PlayingRole = 'BATTER' | 'BOWLER' | 'ALL_ROUNDER' | 'WICKET_KEEPER';

export interface User {
  id: number;
  username: string;
  email: string;
  role: Role;
  enabled: boolean;
  createdAt?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;
  role: Role;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface Team {
  id: number;
  name: string;
  shortName: string;
  country: string;
  logoUrl?: string;
}

export interface Player {
  id: number;
  fullName: string;
  team?: Team;
  battingStyle?: string;
  bowlingStyle?: string;
  playingRole: PlayingRole;
  profileImageUrl?: string;
}

export interface MatchSummary {
  id: number;
  tournamentName?: string;
  venueName?: string;
  venueCity?: string;
  homeTeamId: number;
  homeTeamName: string;
  homeTeamShortName: string;
  homeTeamLogoUrl?: string;
  awayTeamId: number;
  awayTeamName: string;
  awayTeamShortName: string;
  awayTeamLogoUrl?: string;
  matchType: MatchType;
  oversPerInnings: number;
  scheduledStartTime: string;
  status: MatchStatus;
  currentInningsNumber: number;
  result?: string;
  winnerTeamShortName?: string;
  innings1Score?: string;
  innings2Score?: string;
  liveScore?: string;
  currentRunRate: number;
  requiredRunRate?: number;
}

export interface BatterScore {
  playerId: number;
  playerName: string;
  runs: number;
  balls: number;
  fours: number;
  sixes: number;
  strikeRate: number;
  isOut: boolean;
  dismissalInfo: string;
}

export interface BowlerScore {
  playerId: number;
  playerName: string;
  overs: string;
  maidens: number;
  runs: number;
  wickets: number;
  economy: number;
  wides: number;
  noBalls: number;
}

export interface Innings {
  id: number;
  inningsNumber: number;
  battingTeamId: number;
  battingTeamName: string;
  battingTeamShortName: string;
  bowlingTeamId: number;
  bowlingTeamName: string;
  bowlingTeamShortName: string;
  totalRuns: number;
  wickets: number;
  legalBalls: number;
  oversFormatted: string;
  runRate: number;
  extras: number;
  wideRuns: number;
  noBallRuns: number;
  byeRuns: number;
  legByeRuns: number;
  target?: number;
  status: 'IN_PROGRESS' | 'COMPLETED';
  batters: BatterScore[];
  bowlers: BowlerScore[];
}

export interface MatchDetail {
  summary: MatchSummary;
  striker?: BatterScore;
  nonStriker?: BatterScore;
  currentBowler?: BowlerScore;
  recentBalls: string[];
  target?: number;
  runsNeeded?: number;
  ballsRemaining?: number;
  requiredRunRate?: number;
  innings: Innings[];
}

export interface Scorecard {
  matchId: number;
  matchTitle: string;
  status: string;
  result?: string;
  innings: Innings[];
}

export interface Commentary {
  deliveryId: number;
  overNumber: number;
  ballNumber: number;
  overBallFormatted: string;
  bowlerName: string;
  strikerName: string;
  batterRuns: number;
  extraType?: ExtraType;
  extraRuns: number;
  wicket: boolean;
  wicketType?: WicketType;
  dismissedPlayerName?: string;
  commentary: string;
  totalRunsAfter: number;
  wicketsAfter: number;
  timestamp: string;
}

export interface DeliveryRequest {
  strikerId: number;
  nonStrikerId: number;
  bowlerId: number;
  batterRuns: number;
  extraType: ExtraType;
  extraRuns: number;
  isWicket: boolean;
  wicketType?: WicketType;
  dismissedPlayerId?: number;
  fielderId?: number;
  commentary?: string;
}

export interface DeliveryResponse {
  id: number;
  inningsId: number;
  sequenceNumber: number;
  overNumber: number;
  ballNumber: number;
  strikerId: number;
  strikerName: string;
  nonStrikerId: number;
  nonStrikerName: string;
  bowlerId: number;
  bowlerName: string;
  batterRuns: number;
  extraType: ExtraType;
  extraRuns: number;
  legalDelivery: boolean;
  wicket: boolean;
  wicketType?: WicketType;
  dismissedPlayerName?: string;
  commentary: string;
  createdAt: string;
  totalRuns: number;
  wickets: number;
  oversFormatted: string;
  currentRunRate: number;
  nextStrikerId?: number;
  nextNonStrikerId?: number;
  isOverComplete: boolean;
  isInningsComplete: boolean;
  isMatchComplete: boolean;
  matchResult?: string;
}

export interface PlayerStats {
  playerId: number;
  fullName: string;
  teamName?: string;
  teamShortName?: string;
  playingRole: PlayingRole;
  matchesPlayed: number;
  totalRuns: number;
  ballsFaced: number;
  fours: number;
  sixes: number;
  battingAverage: number;
  strikeRate: number;
  wickets: number;
  legalBallsBowled: number;
  runsConceded: number;
  bowlingEconomy: number;
}

export interface AuditLog {
  id: number;
  action: string;
  entityName: string;
  entityId?: number;
  performedBy: string;
  details?: string;
  createdAt: string;
}

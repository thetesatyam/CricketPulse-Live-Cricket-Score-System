import api from './api';
import { ApiResponse, Commentary, MatchDetail, MatchSummary, Scorecard } from '../types';

export const matchService = {
  getAllMatches: async (): Promise<MatchSummary[]> => {
    const res = await api.get<ApiResponse<MatchSummary[]>>('/matches');
    return res.data.data;
  },

  getLiveMatches: async (): Promise<MatchSummary[]> => {
    const res = await api.get<ApiResponse<MatchSummary[]>>('/matches/live');
    return res.data.data;
  },

  getUpcomingMatches: async (): Promise<MatchSummary[]> => {
    const res = await api.get<ApiResponse<MatchSummary[]>>('/matches/upcoming');
    return res.data.data;
  },

  getCompletedMatches: async (): Promise<MatchSummary[]> => {
    const res = await api.get<ApiResponse<MatchSummary[]>>('/matches/completed');
    return res.data.data;
  },

  getMatchDetails: async (matchId: number): Promise<MatchDetail> => {
    const res = await api.get<ApiResponse<MatchDetail>>(`/matches/${matchId}`);
    return res.data.data;
  },

  getScorecard: async (matchId: number): Promise<Scorecard> => {
    const res = await api.get<ApiResponse<Scorecard>>(`/matches/${matchId}/scorecard`);
    return res.data.data;
  },

  getCommentary: async (matchId: number): Promise<Commentary[]> => {
    const res = await api.get<ApiResponse<Commentary[]>>(`/matches/${matchId}/commentary`);
    return res.data.data;
  },

  startMatch: async (matchId: number): Promise<MatchSummary> => {
    const res = await api.post<ApiResponse<MatchSummary>>(`/matches/${matchId}/start`);
    return res.data.data;
  },

  createMatch: async (payload: {
    homeTeamId: number;
    awayTeamId: number;
    matchType: string;
    oversPerInnings: number;
    scheduledStartTime: string;
    venueId?: number;
    tournamentId?: number;
  }): Promise<MatchSummary> => {
    const res = await api.post<ApiResponse<MatchSummary>>('/matches', payload);
    return res.data.data;
  }
};

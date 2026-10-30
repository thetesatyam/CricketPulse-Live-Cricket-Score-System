import api from './api';
import { ApiResponse, AuditLog, Player, PlayerStats, Team, User } from '../types';

export const teamService = {
  getAllTeams: async (): Promise<Team[]> => {
    const res = await api.get<ApiResponse<Team[]>>('/teams');
    return res.data.data;
  },

  getTeamPlayers: async (teamId: number): Promise<Player[]> => {
    const res = await api.get<ApiResponse<Player[]>>(`/teams/${teamId}/players`);
    return res.data.data;
  },

  getAllPlayers: async (): Promise<Player[]> => {
    const res = await api.get<ApiResponse<Player[]>>('/players');
    return res.data.data;
  },

  getPlayerStats: async (playerId: number): Promise<PlayerStats> => {
    const res = await api.get<ApiResponse<PlayerStats>>(`/players/${playerId}/statistics`);
    return res.data.data;
  },

  getTopScorers: async (): Promise<PlayerStats[]> => {
    const res = await api.get<ApiResponse<PlayerStats[]>>('/players/top-scorers');
    return res.data.data;
  },

  getTopWickets: async (): Promise<PlayerStats[]> => {
    const res = await api.get<ApiResponse<PlayerStats[]>>('/players/top-wickets');
    return res.data.data;
  },

  // Admin APIs
  getAllUsers: async (): Promise<User[]> => {
    const res = await api.get<ApiResponse<User[]>>('/admin/users');
    return res.data.data;
  },

  updateUserRole: async (userId: number, role: string): Promise<User> => {
    const res = await api.patch<ApiResponse<User>>(`/admin/users/${userId}/role`, { role });
    return res.data.data;
  },

  getAuditLogs: async (): Promise<AuditLog[]> => {
    const res = await api.get<ApiResponse<AuditLog[]>>('/admin/audit-logs');
    return res.data.data;
  }
};

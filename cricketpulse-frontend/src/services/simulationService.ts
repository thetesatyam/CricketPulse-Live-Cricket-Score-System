import api from './api';
import { ApiResponse } from '../types';

export const simulationService = {
  start: async (matchId: number, intervalMs: number = 2000): Promise<{ running: boolean; matchId: number; intervalMs: number }> => {
    const res = await api.post<ApiResponse<{ running: boolean; matchId: number; intervalMs: number }>>(
      `/simulation/matches/${matchId}/start?intervalMs=${intervalMs}`
    );
    return res.data.data;
  },

  pause: async (matchId: number): Promise<{ running: boolean; matchId: number }> => {
    const res = await api.post<ApiResponse<{ running: boolean; matchId: number }>>(`/simulation/matches/${matchId}/pause`);
    return res.data.data;
  },

  stop: async (matchId: number): Promise<{ running: boolean; matchId: number }> => {
    const res = await api.post<ApiResponse<{ running: boolean; matchId: number }>>(`/simulation/matches/${matchId}/stop`);
    return res.data.data;
  },

  getStatus: async (matchId: number): Promise<{ matchId: number; running: boolean }> => {
    const res = await api.get<ApiResponse<{ matchId: number; running: boolean }>>(`/simulation/matches/${matchId}/status`);
    return res.data.data;
  }
};

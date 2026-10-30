import api from './api';
import { ApiResponse, AuthResponse, User } from '../types';

export const authService = {
  login: async (username: string, password: string):Promise<AuthResponse> => {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', { username, password });
    if (res.data.data.token) {
      localStorage.setItem('cricketpulse_token', res.data.data.token);
      localStorage.setItem('cricketpulse_user', JSON.stringify(res.data.data));
    }
    return res.data.data;
  },

  register: async (username: string, email: string, password: string): Promise<User> => {
    const res = await api.post<ApiResponse<User>>('/auth/register', { username, email, password });
    return res.data.data;
  },

  getCurrentUser: async (): Promise<User> => {
    const res = await api.get<ApiResponse<User>>('/auth/me');
    return res.data.data;
  },

  logout: () => {
    localStorage.removeItem('cricketpulse_token');
    localStorage.removeItem('cricketpulse_user');
  },

  getStoredUser: (): AuthResponse | null => {
    const data = localStorage.getItem('cricketpulse_user');
    return data ? JSON.parse(data) : null;
  }
};

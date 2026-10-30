import React, { createContext, useContext, useState, useEffect } from 'react';
import { AuthResponse, Role } from '../types';
import { authService } from '../services/authService';

interface AuthContextType {
  user: AuthResponse | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isScorer: boolean;
  login: (token: string, user: AuthResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthResponse | null>(null);

  useEffect(() => {
    const stored = authService.getStoredUser();
    if (stored) {
      setUser(stored);
    }
  }, []);

  const login = (token: string, userData: AuthResponse) => {
    localStorage.setItem('cricketpulse_token', token);
    localStorage.setItem('cricketpulse_user', JSON.stringify(userData));
    setUser(userData);
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const isAuthenticated = !!user;
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isScorer = user?.role === 'ROLE_SCORER' || user?.role === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, isAdmin, isScorer, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

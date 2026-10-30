import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/layout/Navbar';
import { Footer } from './components/layout/Footer';
import { DashboardPage } from './pages/DashboardPage';
import { LiveMatchesPage } from './pages/LiveMatchesPage';
import { UpcomingMatchesPage } from './pages/UpcomingMatchesPage';
import { CompletedMatchesPage } from './pages/CompletedMatchesPage';
import { MatchDetailPage } from './pages/MatchDetailPage';
import { ScorerConsolePage } from './pages/ScorerConsolePage';
import { SimulatorPage } from './pages/SimulatorPage';
import { TeamsPage } from './pages/TeamsPage';
import { StatisticsPage } from './pages/StatisticsPage';
import { AdminPage } from './pages/AdminPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';

// Protected Route Guard for Scorers / Admins
const ScorerRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isScorer } = useAuth();
  if (!isScorer) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
};

// Protected Route Guard for Admin only
const AdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAdmin } = useAuth();
  if (!isAdmin) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
};

export const AppContent: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
      <Navbar />
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/matches/live" element={<LiveMatchesPage />} />
          <Route path="/matches/upcoming" element={<UpcomingMatchesPage />} />
          <Route path="/matches/completed" element={<CompletedMatchesPage />} />
          <Route path="/matches/:matchId" element={<MatchDetailPage />} />
          <Route
            path="/scoring"
            element={
              <ScorerRoute>
                <ScorerConsolePage />
              </ScorerRoute>
            }
          />
          <Route path="/simulator" element={<SimulatorPage />} />
          <Route path="/teams" element={<TeamsPage />} />
          <Route path="/statistics" element={<StatisticsPage />} />
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminPage />
              </AdminRoute>
            }
          />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
};

export default App;

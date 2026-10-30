import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { 
  Activity, 
  Calendar, 
  CheckCircle2, 
  Flame, 
  LayoutDashboard, 
  ShieldAlert, 
  Trophy, 
  Users, 
  PlayCircle, 
  BarChart3, 
  Menu, 
  X, 
  LogOut, 
  UserCircle 
} from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, isAdmin, isScorer, logout } = useAuth();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const isActive = (path: string) => location.pathname === path;

  const navLinks = [
    { name: 'Overview', path: '/', icon: LayoutDashboard },
    { name: 'Live', path: '/matches/live', icon: Flame, isLive: true },
    { name: 'Upcoming', path: '/matches/upcoming', icon: Calendar },
    { name: 'Results', path: '/matches/completed', icon: CheckCircle2 },
    { name: 'Teams', path: '/teams', icon: Users },
    { name: 'Statistics', path: '/statistics', icon: BarChart3 },
    { name: 'Simulator', path: '/simulator', icon: PlayCircle },
  ];

  return (
    <nav className="bg-slate-900 border-b border-slate-800 sticky top-0 z-50 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Brand */}
          <div className="flex items-center gap-3">
            <Link to="/" className="flex items-center gap-2 group">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-400 flex items-center justify-center shadow-md shadow-emerald-500/20 group-hover:scale-105 transition-transform">
                <Activity className="w-6 h-6 text-slate-950 font-bold" />
              </div>
              <div className="flex flex-col">
                <span className="text-xl font-black tracking-tight text-white flex items-center gap-1.5">
                  Cricket<span className="text-emerald-400">Pulse</span>
                </span>
                <span className="text-[10px] tracking-widest uppercase text-slate-400 font-semibold -mt-1">
                  Live Management
                </span>
              </div>
            </Link>

            {/* Desktop Navigation */}
            <div className="hidden lg:flex items-center ml-8 space-x-1">
              {navLinks.map((link) => {
                const Icon = link.icon;
                const active = isActive(link.path);
                return (
                  <Link
                    key={link.name}
                    to={link.path}
                    className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                      active
                        ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/60'
                    }`}
                  >
                    <Icon className={`w-4 h-4 ${active ? 'text-emerald-400' : 'text-slate-400'}`} />
                    <span>{link.name}</span>
                    {link.isLive && (
                      <span className="relative flex h-2 w-2 ml-0.5">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                      </span>
                    )}
                  </Link>
                );
              })}

              {/* Protected Scorer Console Link */}
              {isScorer && (
                <Link
                  to="/scoring"
                  className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                    isActive('/scoring')
                      ? 'bg-emerald-950/80 text-emerald-300 border border-emerald-500/50'
                      : 'text-emerald-400 hover:bg-emerald-950/40'
                  }`}
                >
                  <Trophy className="w-4 h-4" />
                  <span>Scoring Console</span>
                </Link>
              )}

              {/* Protected Admin Console Link */}
              {isAdmin && (
                <Link
                  to="/admin"
                  className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                    isActive('/admin')
                      ? 'bg-amber-950/80 text-amber-300 border border-amber-500/50'
                      : 'text-amber-400 hover:bg-amber-950/40'
                  }`}
                >
                  <ShieldAlert className="w-4 h-4" />
                  <span>Administration</span>
                </Link>
              )}
            </div>
          </div>

          {/* Right Action Menu: Auth */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-2 bg-slate-800/80 border border-slate-700/80 px-3 py-1.5 rounded-lg">
                  <UserCircle className="w-5 h-5 text-emerald-400" />
                  <div className="flex flex-col text-left">
                    <span className="text-xs font-semibold text-slate-200">{user?.username}</span>
                    <span className="text-[10px] text-slate-400 uppercase font-mono">
                      {user?.role.replace('ROLE_', '')}
                    </span>
                  </div>
                </div>
                <button
                  onClick={logout}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm text-rose-400 hover:bg-rose-950/40 border border-transparent hover:border-rose-900/50 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  <span>Logout</span>
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="px-3.5 py-1.5 rounded-lg text-sm font-medium text-slate-300 hover:text-white hover:bg-slate-800 transition-colors"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-1.5 rounded-lg text-sm font-semibold bg-emerald-500 text-slate-950 hover:bg-emerald-400 shadow-md shadow-emerald-500/20 transition-all"
                >
                  Register
                </Link>
              </div>
            )}
          </div>

          {/* Mobile menu hamburger */}
          <div className="flex lg:hidden">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 focus:outline-none"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu dropdown */}
      {mobileMenuOpen && (
        <div className="lg:hidden bg-slate-900 border-b border-slate-800 px-4 pt-2 pb-4 space-y-1">
          {navLinks.map((link) => (
            <Link
              key={link.name}
              to={link.path}
              onClick={() => setMobileMenuOpen(false)}
              className="block px-3 py-2 rounded-md text-base font-medium text-slate-300 hover:text-white hover:bg-slate-800"
            >
              {link.name}
            </Link>
          ))}
          {isScorer && (
            <Link
              to="/scoring"
              onClick={() => setMobileMenuOpen(false)}
              className="block px-3 py-2 rounded-md text-base font-medium text-emerald-400 hover:bg-slate-800"
            >
              Scoring Console
            </Link>
          )}
          {isAdmin && (
            <Link
              to="/admin"
              onClick={() => setMobileMenuOpen(false)}
              className="block px-3 py-2 rounded-md text-base font-medium text-amber-400 hover:bg-slate-800"
            >
              Administration
            </Link>
          )}
          <div className="pt-4 border-t border-slate-800">
            {isAuthenticated ? (
              <div className="flex items-center justify-between">
                <span className="text-sm text-slate-300">{user?.username} ({user?.role})</span>
                <button onClick={logout} className="text-sm text-rose-400 hover:underline">
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex gap-2">
                <Link to="/login" onClick={() => setMobileMenuOpen(false)} className="w-1/2 text-center py-2 text-sm bg-slate-800 rounded-lg">
                  Sign In
                </Link>
                <Link to="/register" onClick={() => setMobileMenuOpen(false)} className="w-1/2 text-center py-2 text-sm bg-emerald-500 text-slate-950 font-bold rounded-lg">
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </nav>
  );
};

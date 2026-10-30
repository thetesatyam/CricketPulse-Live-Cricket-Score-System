import React, { useEffect, useState } from 'react';
import { teamService } from '../services/teamService';
import { matchService } from '../services/matchService';
import { AuditLog, Role, Team, User } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { 
  ShieldAlert, 
  Users, 
  History, 
  PlusCircle, 
  Check, 
  AlertCircle 
} from 'lucide-react';

export const AdminPage: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [auditLogs, setAuditLogs] = useState<AuditLog[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);

  // Match scheduling form
  const [homeTeamId, setHomeTeamId] = useState<number | ''>('');
  const [awayTeamId, setAwayTeamId] = useState<number | ''>('');
  const [matchType, setMatchType] = useState<'T20' | 'ODI' | 'TEST' | 'T10'>('T20');
  const [overs, setOvers] = useState(20);
  const [startTime, setStartTime] = useState('');

  const [feedback, setFeedback] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadAdminData = async () => {
    try {
      const [uList, logs, tList] = await Promise.all([
        teamService.getAllUsers(),
        teamService.getAuditLogs(),
        teamService.getAllTeams()
      ]);
      setUsers(uList);
      setAuditLogs(logs);
      setTeams(tList);
      if (tList.length > 1) {
        setHomeTeamId(tList[0].id);
        setAwayTeamId(tList[1].id);
      }
    } catch (e) {
      console.error('Error loading admin data', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAdminData();
  }, []);

  const handleRoleChange = async (userId: number, newRole: Role) => {
    try {
      await teamService.updateUserRole(userId, newRole);
      setFeedback(`User role successfully updated to ${newRole}`);
      loadAdminData();
    } catch (e: any) {
      setError(e.response?.data?.message || 'Failed to update role');
    }
  };

  const handleCreateMatch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!homeTeamId || !awayTeamId || homeTeamId === awayTeamId) {
      setError('Please select two distinct teams');
      return;
    }

    try {
      await matchService.createMatch({
        homeTeamId: Number(homeTeamId),
        awayTeamId: Number(awayTeamId),
        matchType,
        oversPerInnings: Number(overs),
        scheduledStartTime: startTime || new Date().toISOString()
      });
      setFeedback('Match scheduled and squads initialized successfully!');
      loadAdminData();
    } catch (e: any) {
      setError(e.response?.data?.message || 'Error scheduling match');
    }
  };

  if (loading) {
    return <LoadingSpinner message="Accessing administrative vault..." />;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div>
        <h1 className="text-2xl font-black text-white flex items-center gap-2">
          <ShieldAlert className="w-6 h-6 text-amber-400" />
          <span>System Administration & Audit Controls</span>
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Manage user permissions, review system audit trail, and schedule official tournament fixtures
        </p>
      </div>

      {feedback && (
        <div className="bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 p-4 rounded-xl text-xs flex items-center gap-2">
          <Check className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{feedback}</span>
        </div>
      )}

      {error && (
        <div className="bg-rose-500/10 border border-rose-500/30 text-rose-300 p-4 rounded-xl text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Grid: User Management & Schedule Form */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* User Role Management Table (2 cols) */}
        <div className="lg:col-span-2 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-slate-800">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Users className="w-5 h-5 text-emerald-400" />
              <span>User Role Assignments</span>
            </h2>
            <span className="text-xs text-slate-500 font-mono">{users.length} Users</span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="text-slate-400 uppercase font-semibold border-b border-slate-800">
                  <th className="py-2.5 px-3">Username</th>
                  <th className="py-2.5 px-3">Email</th>
                  <th className="py-2.5 px-3">Current Role</th>
                  <th className="py-2.5 px-3 text-right">Assign Role</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {users.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-3 font-semibold text-white">{u.username}</td>
                    <td className="py-3 px-3 text-slate-400">{u.email}</td>
                    <td className="py-3 px-3">
                      <span className={`inline-flex px-2 py-0.5 rounded-full text-[10px] font-bold ${
                        u.role === 'ROLE_ADMIN'
                          ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                          : u.role === 'ROLE_SCORER'
                          ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                          : 'bg-slate-800 text-slate-400'
                      }`}>
                        {u.role.replace('ROLE_', '')}
                      </span>
                    </td>
                    <td className="py-3 px-3 text-right">
                      <select
                        value={u.role}
                        onChange={(e) => handleRoleChange(u.id, e.target.value as Role)}
                        className="bg-slate-950 border border-slate-700 text-white text-xs rounded-lg px-2 py-1"
                      >
                        <option value="ROLE_VIEWER">Viewer</option>
                        <option value="ROLE_SCORER">Scorer</option>
                        <option value="ROLE_ADMIN">Admin</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Schedule Match Form (1 col) */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
          <div className="flex items-center gap-2 pb-3 border-b border-slate-800">
            <PlusCircle className="w-5 h-5 text-emerald-400" />
            <h2 className="text-base font-bold text-white">Schedule New Match</h2>
          </div>

          <form onSubmit={handleCreateMatch} className="space-y-4 text-xs">
            <div>
              <label className="text-slate-300 font-semibold block mb-1">Home Team:</label>
              <select
                value={homeTeamId}
                onChange={(e) => setHomeTeamId(Number(e.target.value))}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-white"
              >
                {teams.map((t) => (
                  <option key={t.id} value={t.id}>{t.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-slate-300 font-semibold block mb-1">Away Team:</label>
              <select
                value={awayTeamId}
                onChange={(e) => setAwayTeamId(Number(e.target.value))}
                className="w-full bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-white"
              >
                {teams.map((t) => (
                  <option key={t.id} value={t.id}>{t.name}</option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-slate-300 font-semibold block mb-1">Format:</label>
                <select
                  value={matchType}
                  onChange={(e) => setMatchType(e.target.value as any)}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-white"
                >
                  <option value="T20">T20 (20 Overs)</option>
                  <option value="ODI">ODI (50 Overs)</option>
                  <option value="T10">T10 (10 Overs)</option>
                </select>
              </div>

              <div>
                <label className="text-slate-300 font-semibold block mb-1">Overs/Innings:</label>
                <input
                  type="number"
                  value={overs}
                  onChange={(e) => setOvers(Number(e.target.value))}
                  className="w-full bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-white"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm shadow-md transition-all"
            >
              Schedule Fixture
            </button>
          </form>
        </div>
      </div>

      {/* Audit Logs Trail */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="flex items-center justify-between pb-3 border-b border-slate-800">
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <History className="w-5 h-5 text-slate-400" />
            <span>Audit Trail & Activity Log</span>
          </h2>
          <span className="text-xs text-slate-500 font-mono">Immutable Event History</span>
        </div>

        <div className="overflow-x-auto max-h-72">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="text-slate-400 uppercase font-semibold border-b border-slate-800">
                <th className="py-2.5 px-3">Timestamp</th>
                <th className="py-2.5 px-3">Action</th>
                <th className="py-2.5 px-3">Entity</th>
                <th className="py-2.5 px-3">Performed By</th>
                <th className="py-2.5 px-3">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 font-mono text-[11px]">
              {auditLogs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-800/40">
                  <td className="py-2.5 px-3 text-slate-400">{log.createdAt}</td>
                  <td className="py-2.5 px-3 font-bold text-emerald-400">{log.action}</td>
                  <td className="py-2.5 px-3 text-slate-300">{log.entityName} #{log.entityId}</td>
                  <td className="py-2.5 px-3 text-amber-300">{log.performedBy}</td>
                  <td className="py-2.5 px-3 text-slate-300 font-sans">{log.details}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

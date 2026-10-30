import React, { useEffect, useState } from 'react';
import { teamService } from '../services/teamService';
import { PlayerStats } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { BarChart3, TrendingUp, ShieldCheck, Zap, Award } from 'lucide-react';

export const StatisticsPage: React.FC = () => {
  const [topScorers, setTopScorers] = useState<PlayerStats[]>([]);
  const [topWickets, setTopWickets] = useState<PlayerStats[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [scorers, wickets] = await Promise.all([
          teamService.getTopScorers(),
          teamService.getTopWickets()
        ]);
        setTopScorers(scorers);
        setTopWickets(wickets);
      } catch (e) {
        console.error('Error fetching statistics', e);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Calculating tournament analytics..." />;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div>
        <h1 className="text-2xl font-black text-white flex items-center gap-2">
          <BarChart3 className="w-6 h-6 text-emerald-400" />
          <span>Tournament Analytics & Leaderboards</span>
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Authoritative aggregate statistics, top run getters, wicket leaders, and economy rankings
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Top Run Scorers Table */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-slate-800">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-amber-400" />
              <span>Top Run Scorers (Orange Cap Race)</span>
            </h2>
            <Award className="w-4 h-4 text-amber-400" />
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="text-slate-400 uppercase font-semibold border-b border-slate-800">
                  <th className="py-2.5 px-3">#</th>
                  <th className="py-2.5 px-3">Player</th>
                  <th className="py-2.5 px-2">Team</th>
                  <th className="py-2.5 px-3 text-right font-mono">Runs</th>
                  <th className="py-2.5 px-3 text-right font-mono">Balls</th>
                  <th className="py-2.5 px-2 text-right font-mono">4s</th>
                  <th className="py-2.5 px-2 text-right font-mono">6s</th>
                  <th className="py-2.5 px-3 text-right font-mono">SR</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {topScorers.map((p, idx) => (
                  <tr key={p.playerId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-3 font-mono font-bold text-slate-400">{idx + 1}</td>
                    <td className="py-3 px-3 font-semibold text-white">{p.fullName}</td>
                    <td className="py-3 px-2 font-mono text-slate-400">{p.teamShortName}</td>
                    <td className="py-3 px-3 text-right font-mono font-bold text-amber-400">{p.totalRuns}</td>
                    <td className="py-3 px-3 text-right font-mono text-slate-400">{p.ballsFaced}</td>
                    <td className="py-3 px-2 text-right font-mono text-slate-400">{p.fours}</td>
                    <td className="py-3 px-2 text-right font-mono text-slate-400">{p.sixes}</td>
                    <td className="py-3 px-3 text-right font-mono text-emerald-400">{p.strikeRate.toFixed(1)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Top Wicket Takers Table */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-slate-800">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <ShieldCheck className="w-5 h-5 text-emerald-400" />
              <span>Top Wicket Takers (Purple Cap Race)</span>
            </h2>
            <Award className="w-4 h-4 text-emerald-400" />
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="text-slate-400 uppercase font-semibold border-b border-slate-800">
                  <th className="py-2.5 px-3">#</th>
                  <th className="py-2.5 px-3">Player</th>
                  <th className="py-2.5 px-2">Team</th>
                  <th className="py-2.5 px-3 text-right font-mono">Wkts</th>
                  <th className="py-2.5 px-3 text-right font-mono">Balls</th>
                  <th className="py-2.5 px-3 text-right font-mono">Runs</th>
                  <th className="py-2.5 px-3 text-right font-mono">Econ</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {topWickets.map((p, idx) => (
                  <tr key={p.playerId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-3 font-mono font-bold text-slate-400">{idx + 1}</td>
                    <td className="py-3 px-3 font-semibold text-white">{p.fullName}</td>
                    <td className="py-3 px-2 font-mono text-slate-400">{p.teamShortName}</td>
                    <td className="py-3 px-3 text-right font-mono font-bold text-emerald-400">{p.wickets}</td>
                    <td className="py-3 px-3 text-right font-mono text-slate-400">{p.legalBallsBowled}</td>
                    <td className="py-3 px-3 text-right font-mono text-slate-400">{p.runsConceded}</td>
                    <td className="py-3 px-3 text-right font-mono text-slate-300">{p.bowlingEconomy.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

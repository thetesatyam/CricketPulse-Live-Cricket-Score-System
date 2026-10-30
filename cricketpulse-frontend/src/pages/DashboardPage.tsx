import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { matchService } from '../services/matchService';
import { teamService } from '../services/teamService';
import { MatchSummary, PlayerStats } from '../types';
import { MatchCard } from '../components/matches/MatchCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { 
  Activity, 
  Flame, 
  Calendar, 
  CheckCircle2, 
  TrendingUp, 
  ChevronRight, 
  ShieldCheck, 
  PlayCircle 
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const [liveMatches, setLiveMatches] = useState<MatchSummary[]>([]);
  const [upcomingMatches, setUpcomingMatches] = useState<MatchSummary[]>([]);
  const [completedMatches, setCompletedMatches] = useState<MatchSummary[]>([]);
  const [topScorers, setTopScorers] = useState<PlayerStats[]>([]);
  const [topWickets, setTopWickets] = useState<PlayerStats[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [live, up, comp, scorers, wickets] = await Promise.all([
          matchService.getLiveMatches(),
          matchService.getUpcomingMatches(),
          matchService.getCompletedMatches(),
          teamService.getTopScorers(),
          teamService.getTopWickets()
        ]);
        setLiveMatches(live);
        setUpcomingMatches(up);
        setCompletedMatches(comp);
        setTopScorers(scorers);
        setTopWickets(wickets);
      } catch (err) {
        console.error('Error loading dashboard data', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Loading live cricket action..." />;
  }

  const featuredMatch = liveMatches.length > 0 ? liveMatches[0] : null;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Metrics Row */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center shrink-0">
            <Flame className="w-6 h-6 text-rose-500" />
          </div>
          <div>
            <span className="text-2xl font-black font-mono text-white">{liveMatches.length}</span>
            <p className="text-xs text-slate-400 font-medium">Live Matches</p>
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-blue-500/10 border border-blue-500/30 flex items-center justify-center shrink-0">
            <Calendar className="w-6 h-6 text-blue-500" />
          </div>
          <div>
            <span className="text-2xl font-black font-mono text-white">{upcomingMatches.length}</span>
            <p className="text-xs text-slate-400 font-medium">Upcoming Fixtures</p>
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center shrink-0">
            <CheckCircle2 className="w-6 h-6 text-emerald-500" />
          </div>
          <div>
            <span className="text-2xl font-black font-mono text-white">{completedMatches.length}</span>
            <p className="text-xs text-slate-400 font-medium">Completed Results</p>
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center shrink-0">
            <TrendingUp className="w-6 h-6 text-amber-500" />
          </div>
          <div>
            <span className="text-2xl font-black font-mono text-white">4</span>
            <p className="text-xs text-slate-400 font-medium">Participating Teams</p>
          </div>
        </div>
      </div>

      {/* Featured Live Match Banner */}
      {featuredMatch && (
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-slate-900 via-slate-850 to-emerald-950 border border-emerald-500/30 p-6 md:p-8 shadow-2xl">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div className="space-y-3">
              <div className="flex items-center gap-2">
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40">
                  <span className="w-2 h-2 rounded-full bg-rose-500 animate-ping"></span>
                  FEATURED LIVE MATCH
                </span>
                <span className="text-xs text-slate-400">
                  {featuredMatch.tournamentName} • {featuredMatch.matchType}
                </span>
              </div>
              
              <h2 className="text-2xl md:text-3xl font-black text-white">
                {featuredMatch.homeTeamName} vs {featuredMatch.awayTeamName}
              </h2>

              <div className="flex items-center gap-4 text-emerald-400 font-mono text-xl font-bold">
                <span>{featuredMatch.liveScore || 'Match in progress'}</span>
                <span className="text-xs font-sans text-slate-400 font-normal">
                  CRR: <strong className="text-white font-mono">{featuredMatch.currentRunRate.toFixed(2)}</strong>
                </span>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Link
                to={`/matches/${featuredMatch.id}`}
                className="px-6 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm shadow-lg shadow-emerald-500/20 transition-all flex items-center gap-2"
              >
                <span>Live Center</span>
                <ChevronRight className="w-4 h-4" />
              </Link>
              <Link
                to="/simulator"
                className="px-5 py-3 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-medium text-sm border border-slate-700 transition-colors flex items-center gap-2"
              >
                <PlayCircle className="w-4 h-4 text-emerald-400" />
                <span>Simulate</span>
              </Link>
            </div>
          </div>
        </div>
      )}

      {/* Two Column Layout: Matches & Leaderboards */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 Cols: Live & Recent Matches */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <Flame className="w-5 h-5 text-rose-400" />
              <span>Live & Ongoing Fixtures</span>
            </h3>
            <Link to="/matches/live" className="text-xs text-emerald-400 hover:underline">
              View All
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {liveMatches.length > 0 ? (
              liveMatches.map((m) => <MatchCard key={m.id} match={m} />)
            ) : (
              <div className="col-span-2 bg-slate-900 border border-slate-800 rounded-xl p-8 text-center text-slate-500">
                No matches currently live. Check upcoming fixtures or launch the Simulator!
              </div>
            )}
          </div>

          <div className="flex items-center justify-between pt-4">
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
              <span>Recent Results</span>
            </h3>
            <Link to="/matches/completed" className="text-xs text-emerald-400 hover:underline">
              View All
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {completedMatches.map((m) => (
              <MatchCard key={m.id} match={m} />
            ))}
          </div>
        </div>

        {/* Right 1 Col: Top Performers Widgets */}
        <div className="space-y-6">
          {/* Top Run Scorers */}
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-lg">
            <h3 className="text-sm font-bold text-white flex items-center gap-2 mb-4 pb-2 border-b border-slate-800">
              <TrendingUp className="w-4 h-4 text-amber-400" />
              <span>Top Run Scorers</span>
            </h3>

            <div className="space-y-3">
              {topScorers.slice(0, 5).map((p, idx) => (
                <div key={p.playerId} className="flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2.5">
                    <span className="w-5 h-5 rounded-full bg-slate-800 text-slate-400 flex items-center justify-center font-mono font-bold text-[10px]">
                      {idx + 1}
                    </span>
                    <div>
                      <p className="font-semibold text-slate-200">{p.fullName}</p>
                      <p className="text-[10px] text-slate-500">{p.teamShortName}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="font-mono font-bold text-amber-400 text-sm">{p.totalRuns}</span>
                    <p className="text-[10px] text-slate-500">SR {p.strikeRate.toFixed(1)}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Top Wicket Takers */}
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-lg">
            <h3 className="text-sm font-bold text-white flex items-center gap-2 mb-4 pb-2 border-b border-slate-800">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              <span>Top Wicket Takers</span>
            </h3>

            <div className="space-y-3">
              {topWickets.slice(0, 5).map((p, idx) => (
                <div key={p.playerId} className="flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2.5">
                    <span className="w-5 h-5 rounded-full bg-slate-800 text-slate-400 flex items-center justify-center font-mono font-bold text-[10px]">
                      {idx + 1}
                    </span>
                    <div>
                      <p className="font-semibold text-slate-200">{p.fullName}</p>
                      <p className="text-[10px] text-slate-500">{p.teamShortName}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="font-mono font-bold text-emerald-400 text-sm">{p.wickets}</span>
                    <p className="text-[10px] text-slate-500">Econ {p.bowlingEconomy.toFixed(2)}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

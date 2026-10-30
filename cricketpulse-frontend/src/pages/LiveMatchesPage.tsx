import React, { useEffect, useState } from 'react';
import { matchService } from '../services/matchService';
import { MatchSummary } from '../types';
import { MatchCard } from '../components/matches/MatchCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Flame, PlayCircle } from 'lucide-react';
import { Link } from 'react-router-dom';

export const LiveMatchesPage: React.FC = () => {
  const [matches, setMatches] = useState<MatchSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLive = async () => {
      try {
        const live = await matchService.getLiveMatches();
        setMatches(live);
      } catch (e) {
        console.error('Error loading live matches', e);
      } finally {
        setLoading(false);
      }
    };
    fetchLive();

    const interval = setInterval(fetchLive, 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return <LoadingSpinner message="Scanning for live cricket games..." />;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-black text-white flex items-center gap-2">
            <Flame className="w-6 h-6 text-rose-500 animate-pulse" />
            <span>Live Cricket Matches</span>
          </h1>
          <p className="text-xs text-slate-400 mt-1">Real-time ball-by-ball updates and scores</p>
        </div>

        <Link
          to="/simulator"
          className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 hover:bg-emerald-500/20 text-xs font-bold transition-colors"
        >
          <PlayCircle className="w-4 h-4" />
          <span>Launch Match Simulator</span>
        </Link>
      </div>

      {matches.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {matches.map((match) => (
            <MatchCard key={match.id} match={match} />
          ))}
        </div>
      ) : (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-12 text-center max-w-xl mx-auto space-y-4">
          <div className="w-16 h-16 rounded-2xl bg-slate-800 flex items-center justify-center mx-auto text-slate-400">
            <Flame className="w-8 h-8 text-slate-500" />
          </div>
          <h3 className="text-lg font-bold text-white">No Live Matches Right Now</h3>
          <p className="text-sm text-slate-400">
            There are no ongoing live games at this moment. You can start the autonomous match simulator to simulate live deliveries in real-time.
          </p>
          <Link
            to="/simulator"
            className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm shadow-md transition-all"
          >
            <PlayCircle className="w-4 h-4" />
            <span>Start Live Simulation</span>
          </Link>
        </div>
      )}
    </div>
  );
};

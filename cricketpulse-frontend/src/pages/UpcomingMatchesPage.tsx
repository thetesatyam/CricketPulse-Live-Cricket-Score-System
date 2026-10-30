import React, { useEffect, useState } from 'react';
import { matchService } from '../services/matchService';
import { MatchSummary } from '../types';
import { MatchCard } from '../components/matches/MatchCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Calendar } from 'lucide-react';

export const UpcomingMatchesPage: React.FC = () => {
  const [matches, setMatches] = useState<MatchSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        const list = await matchService.getUpcomingMatches();
        setMatches(list);
      } catch (e) {
        console.error('Error fetching upcoming fixtures', e);
      } finally {
        setLoading(false);
      }
    };
    fetchUpcoming();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Retrieving upcoming fixtures..." />;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-black text-white flex items-center gap-2">
          <Calendar className="w-6 h-6 text-blue-400" />
          <span>Upcoming Cricket Fixtures</span>
        </h1>
        <p className="text-xs text-slate-400 mt-1">Scheduled matches across international and domestic tournaments</p>
      </div>

      {matches.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {matches.map((m) => (
            <MatchCard key={m.id} match={m} />
          ))}
        </div>
      ) : (
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-12 text-center text-slate-500">
          No upcoming matches scheduled. Administrators can schedule fixtures from the Admin Console.
        </div>
      )}
    </div>
  );
};

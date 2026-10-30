import React, { useEffect, useState } from 'react';
import { matchService } from '../services/matchService';
import { MatchSummary } from '../types';
import { MatchCard } from '../components/matches/MatchCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { CheckCircle2 } from 'lucide-react';

export const CompletedMatchesPage: React.FC = () => {
  const [matches, setMatches] = useState<MatchSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCompleted = async () => {
      try {
        const list = await matchService.getCompletedMatches();
        setMatches(list);
      } catch (e) {
        console.error('Error fetching completed results', e);
      } finally {
        setLoading(false);
      }
    };
    fetchCompleted();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Retrieving match results archive..." />;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-black text-white flex items-center gap-2">
          <CheckCircle2 className="w-6 h-6 text-emerald-400" />
          <span>Completed Matches & Results</span>
        </h1>
        <p className="text-xs text-slate-400 mt-1">Historical scorecards, victory margins, and match summaries</p>
      </div>

      {matches.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {matches.map((m) => (
            <MatchCard key={m.id} match={m} />
          ))}
        </div>
      ) : (
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-12 text-center text-slate-500">
          No completed matches recorded in the archive yet.
        </div>
      )}
    </div>
  );
};

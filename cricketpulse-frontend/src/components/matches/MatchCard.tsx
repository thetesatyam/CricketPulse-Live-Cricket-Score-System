import React from 'react';
import { Link } from 'react-router-dom';
import { MatchSummary } from '../../types';
import { StatusPill } from '../common/StatusPill';
import { ChevronRight, MapPin } from 'lucide-react';

interface MatchCardProps {
  match: MatchSummary;
}

export const MatchCard: React.FC<MatchCardProps> = ({ match }) => {
  const isLive = match.status === 'LIVE' || match.status === 'INNINGS_BREAK';

  return (
    <Link
      to={`/matches/${match.id}`}
      className="group block bg-slate-900/90 hover:bg-slate-850 border border-slate-800 hover:border-emerald-500/40 rounded-xl p-5 shadow-lg transition-all duration-200 hover:-translate-y-0.5"
    >
      {/* Header Info */}
      <div className="flex items-center justify-between gap-2 pb-3 mb-4 border-b border-slate-800/80 text-xs text-slate-400">
        <span className="font-semibold text-slate-300 truncate">
          {match.tournamentName || 'T20 International'} • {match.matchType}
        </span>
        <div className="flex items-center gap-2 shrink-0">
          <StatusPill status={match.status} />
        </div>
      </div>

      {/* Teams & Scores */}
      <div className="space-y-3">
        {/* Home Team */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-xs text-slate-200">
              {match.homeTeamShortName}
            </div>
            <span className="font-bold text-base text-white group-hover:text-emerald-400 transition-colors">
              {match.homeTeamName}
            </span>
          </div>
          <span className="font-mono font-bold text-base text-slate-200">
            {match.innings1Score || '-'}
          </span>
        </div>

        {/* Away Team */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-xs text-slate-200">
              {match.awayTeamShortName}
            </div>
            <span className="font-bold text-base text-white group-hover:text-emerald-400 transition-colors">
              {match.awayTeamName}
            </span>
          </div>
          <span className="font-mono font-bold text-base text-slate-200">
            {match.innings2Score || '-'}
          </span>
        </div>
      </div>

      {/* Footer Info: Result / Run Rate / Venue */}
      <div className="mt-4 pt-3 border-t border-slate-800/80 flex items-center justify-between text-xs">
        <div className="truncate mr-2">
          {match.result ? (
            <span className="font-semibold text-emerald-400">{match.result}</span>
          ) : isLive ? (
            <span className="text-slate-300 flex items-center gap-2">
              <span>CRR: <strong className="text-white font-mono">{match.currentRunRate.toFixed(2)}</strong></span>
              {match.requiredRunRate && (
                <span>• RRR: <strong className="text-amber-400 font-mono">{match.requiredRunRate.toFixed(2)}</strong></span>
              )}
            </span>
          ) : (
            <span className="text-slate-400 flex items-center gap-1">
              <MapPin className="w-3.5 h-3.5 text-slate-500" />
              {match.venueName ? `${match.venueName}, ${match.venueCity}` : 'Venue TBD'}
            </span>
          )}
        </div>
        <div className="flex items-center text-emerald-400 font-medium shrink-0 group-hover:translate-x-0.5 transition-transform">
          <span>Details</span>
          <ChevronRight className="w-4 h-4 ml-0.5" />
        </div>
      </div>
    </Link>
  );
};

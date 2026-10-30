import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { matchService } from '../services/matchService';
import { websocketService } from '../services/websocketService';
import { Commentary, DeliveryResponse, MatchDetail, Innings } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { StatusPill } from '../components/common/StatusPill';
import { ScorecardTable } from '../components/scorecard/ScorecardTable';
import { RunRateChart } from '../components/charts/RunRateChart';
import { useAuth } from '../context/AuthContext';
import { 
  Activity, 
  Clock, 
  MapPin, 
  ShieldCheck, 
  Trophy, 
  PlayCircle, 
  Radio, 
  BarChart2, 
  FileText, 
  Info 
} from 'lucide-react';

export const MatchDetailPage: React.FC = () => {
  const { matchId } = useParams<{ matchId: string }>();
  const id = Number(matchId);
  const { isScorer } = useAuth();

  const [matchDetail, setMatchDetail] = useState<MatchDetail | null>(null);
  const [commentary, setCommentary] = useState<Commentary[]>([]);
  const [activeTab, setActiveTab] = useState<'commentary' | 'scorecard' | 'analytics' | 'info'>('commentary');
  const [loading, setLoading] = useState(true);
  const [wsConnected, setWsConnected] = useState(false);

  useEffect(() => {
    if (!id) return;

    const loadData = async () => {
      try {
        const [detail, comm] = await Promise.all([
          matchService.getMatchDetails(id),
          matchService.getCommentary(id)
        ]);
        setMatchDetail(detail);
        setCommentary(comm);
      } catch (e) {
        console.error('Error fetching match details', e);
      } finally {
        setLoading(false);
      }
    };

    loadData();

    // Subscribe to STOMP live score updates
    const unsubscribe = websocketService.subscribeToMatch(
      id,
      (scoreUpdate: DeliveryResponse) => {
        setWsConnected(true);
        // Refresh full match state to keep scorecards and commentary in sync
        loadData();
      },
      (commUpdate: DeliveryResponse) => {
        setWsConnected(true);
        loadData();
      }
    );

    return () => {
      if (unsubscribe) unsubscribe();
    };
  }, [id]);

  if (loading || !matchDetail) {
    return <LoadingSpinner message="Connecting to live match arena..." />;
  }

  const { summary } = matchDetail;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Live Match Arena Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 md:p-8 shadow-xl relative overflow-hidden">
        {/* Top Info Bar */}
        <div className="flex flex-wrap items-center justify-between gap-3 pb-4 mb-6 border-b border-slate-800 text-xs text-slate-400">
          <div className="flex items-center gap-2">
            <span className="font-bold text-slate-200">{summary.tournamentName}</span>
            <span>•</span>
            <span>{summary.matchType} format</span>
            <span>•</span>
            <span className="flex items-center gap-1">
              <MapPin className="w-3.5 h-3.5" />
              {summary.venueName}, {summary.venueCity}
            </span>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1.5 text-[11px] bg-slate-950 px-2.5 py-1 rounded-full border border-slate-800">
              <span className={`w-2 h-2 rounded-full ${wsConnected ? 'bg-emerald-400 animate-ping' : 'bg-slate-500'}`}></span>
              <span className={wsConnected ? 'text-emerald-400 font-semibold' : 'text-slate-400'}>
                {wsConnected ? 'Real-Time Connected' : 'Live Polling'}
              </span>
            </div>
            <StatusPill status={summary.status} />
          </div>
        </div>

        {/* Scoreboard Big Banner */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-center">
          {/* Teams & Score Display */}
          <div className="space-y-4">
            {/* Home Team Line */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center font-black text-sm text-white">
                  {summary.homeTeamShortName}
                </div>
                <div>
                  <span className="text-xl font-black text-white">{summary.homeTeamName}</span>
                  {summary.status === 'LIVE' && summary.currentInningsNumber === 1 && (
                    <span className="ml-2 text-xs text-emerald-400 font-bold animate-pulse">● Batting</span>
                  )}
                </div>
              </div>
              <span className="text-2xl font-black font-mono text-white">
                {summary.innings1Score || '-'}
              </span>
            </div>

            {/* Away Team Line */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center font-black text-sm text-white">
                  {summary.awayTeamShortName}
                </div>
                <div>
                  <span className="text-xl font-black text-white">{summary.awayTeamName}</span>
                  {summary.status === 'LIVE' && summary.currentInningsNumber === 2 && (
                    <span className="ml-2 text-xs text-emerald-400 font-bold animate-pulse">● Batting</span>
                  )}
                </div>
              </div>
              <span className="text-2xl font-black font-mono text-white">
                {summary.innings2Score || '-'}
              </span>
            </div>

            {/* Match Status / Target Sentence */}
            <div className="pt-2 text-sm font-semibold">
              {summary.result ? (
                <span className="text-emerald-400">{summary.result}</span>
              ) : matchDetail.target ? (
                <span className="text-amber-300">
                  Target: <strong className="font-mono text-white">{matchDetail.target}</strong>. 
                  Needs <strong className="font-mono text-emerald-400">{matchDetail.runsNeeded}</strong> runs in <strong className="font-mono text-white">{matchDetail.ballsRemaining}</strong> balls (RRR: {matchDetail.requiredRunRate?.toFixed(2)})
                </span>
              ) : (
                <span className="text-slate-400">
                  Current Run Rate: <strong className="font-mono text-white">{summary.currentRunRate.toFixed(2)}</strong>
                </span>
              )}
            </div>
          </div>

          {/* Action & Scorer Controls */}
          <div className="flex flex-col md:items-end justify-center gap-3">
            <div className="flex items-center gap-2">
              {isScorer && (
                <Link
                  to={`/scoring?matchId=${id}`}
                  className="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs flex items-center gap-2 shadow-lg transition-all"
                >
                  <Trophy className="w-4 h-4" />
                  <span>Open Scoring Console</span>
                </Link>
              )}
              <Link
                to={`/simulator?matchId=${id}`}
                className="px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-medium text-xs border border-slate-700 flex items-center gap-2 transition-colors"
              >
                <PlayCircle className="w-4 h-4 text-emerald-400" />
                <span>Simulate Match</span>
              </Link>
            </div>

            {/* Recent Balls Strip */}
            {matchDetail.recentBalls && matchDetail.recentBalls.length > 0 && (
              <div className="flex items-center gap-1.5 mt-2 bg-slate-950/70 p-2 rounded-xl border border-slate-800">
                <span className="text-[11px] font-semibold text-slate-400 mr-1">Recent:</span>
                {matchDetail.recentBalls.map((b: string, idx: number) => {
                  const isW = b === 'W';
                  const is4 = b === '4';
                  const is6 = b === '6';
                  return (
                    <span
                      key={idx}
                      className={`w-6 h-6 rounded-full flex items-center justify-center font-mono text-xs font-bold ${
                        isW
                          ? 'bg-rose-500 text-white'
                          : is6
                          ? 'bg-purple-600 text-white'
                          : is4
                          ? 'bg-blue-600 text-white'
                          : 'bg-slate-800 text-slate-300'
                      }`}
                    >
                      {b}
                    </span>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        {/* Live Batsmen & Bowler HUD */}
        {(matchDetail.striker || matchDetail.currentBowler) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6 pt-6 border-t border-slate-800/80 text-xs">
            {/* Batters */}
            <div className="bg-slate-950/60 p-3.5 rounded-xl border border-slate-800 space-y-2">
              <span className="text-[10px] font-bold tracking-wider uppercase text-slate-500">Batting</span>
              {matchDetail.striker && (
                <div className="flex items-center justify-between text-slate-200">
                  <span className="font-bold flex items-center gap-1.5 text-white">
                    <span className="text-emerald-400">►</span> {matchDetail.striker.playerName} *
                  </span>
                  <span className="font-mono">
                    <strong className="text-white text-sm">{matchDetail.striker.runs}</strong> ({matchDetail.striker.balls}b, {matchDetail.striker.fours}x4, {matchDetail.striker.sixes}x6)
                  </span>
                </div>
              )}
              {matchDetail.nonStriker && (
                <div className="flex items-center justify-between text-slate-300">
                  <span>{matchDetail.nonStriker.playerName}</span>
                  <span className="font-mono">
                    <strong className="text-white">{matchDetail.nonStriker.runs}</strong> ({matchDetail.nonStriker.balls}b)
                  </span>
                </div>
              )}
            </div>

            {/* Bowler */}
            {matchDetail.currentBowler && (
              <div className="bg-slate-950/60 p-3.5 rounded-xl border border-slate-800 space-y-2">
                <span className="text-[10px] font-bold tracking-wider uppercase text-slate-500">Bowling</span>
                <div className="flex items-center justify-between text-slate-200">
                  <span className="font-bold text-white">{matchDetail.currentBowler.playerName}</span>
                  <span className="font-mono">
                    <strong className="text-emerald-400">{matchDetail.currentBowler.wickets}</strong>/{matchDetail.currentBowler.runs} ({matchDetail.currentBowler.overs} ov, Econ: {matchDetail.currentBowler.economy.toFixed(2)})
                  </span>
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Navigation Tabs */}
      <div className="flex border-b border-slate-800 space-x-6 text-sm font-semibold">
        <button
          onClick={() => setActiveTab('commentary')}
          className={`pb-3 flex items-center gap-2 border-b-2 transition-all ${
            activeTab === 'commentary'
              ? 'border-emerald-400 text-emerald-400'
              : 'border-transparent text-slate-400 hover:text-white'
          }`}
        >
          <Radio className="w-4 h-4" />
          <span>Commentary</span>
        </button>

        <button
          onClick={() => setActiveTab('scorecard')}
          className={`pb-3 flex items-center gap-2 border-b-2 transition-all ${
            activeTab === 'scorecard'
              ? 'border-emerald-400 text-emerald-400'
              : 'border-transparent text-slate-400 hover:text-white'
          }`}
        >
          <FileText className="w-4 h-4" />
          <span>Full Scorecard</span>
        </button>

        <button
          onClick={() => setActiveTab('analytics')}
          className={`pb-3 flex items-center gap-2 border-b-2 transition-all ${
            activeTab === 'analytics'
              ? 'border-emerald-400 text-emerald-400'
              : 'border-transparent text-slate-400 hover:text-white'
          }`}
        >
          <BarChart2 className="w-4 h-4" />
          <span>Run Rate Analytics</span>
        </button>
      </div>

      {/* Tab Panels */}
      {activeTab === 'commentary' && (
        <div className="space-y-3">
          {commentary.length > 0 ? (
            commentary.map((c) => (
              <div
                key={c.deliveryId}
                className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex gap-4 items-start shadow-md hover:border-slate-700 transition-colors"
              >
                <div className="shrink-0 w-14 font-mono font-bold text-sm text-emerald-400 bg-slate-950 px-2 py-1 rounded-lg border border-slate-800 text-center">
                  {c.overBallFormatted}
                </div>
                <div className="space-y-1 flex-1">
                  <div className="flex items-center justify-between text-xs text-slate-400">
                    <span className="font-semibold text-slate-200">
                      {c.bowlerName} to {c.strikerName}
                    </span>
                    <span className="font-mono font-bold text-slate-300">
                      {c.wicket ? 'WICKET' : `${c.batterRuns + c.extraRuns} runs`}
                    </span>
                  </div>
                  <p className="text-sm text-slate-200">{c.commentary}</p>
                </div>
              </div>
            ))
          ) : (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-8 text-center text-slate-500">
              No deliveries bowled yet. Ball-by-ball commentary will appear once the game begins.
            </div>
          )}
        </div>
      )}

      {activeTab === 'scorecard' && (
        <div>
          {matchDetail.innings.map((inn: Innings) => (
            <ScorecardTable key={inn.id} innings={inn} />
          ))}
        </div>
      )}

      {activeTab === 'analytics' && (
        <div>
          <RunRateChart innings={matchDetail.innings} />
        </div>
      )}
    </div>
  );
};

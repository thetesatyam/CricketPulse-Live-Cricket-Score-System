import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { matchService } from '../services/matchService';
import { simulationService } from '../services/simulationService';
import { websocketService } from '../services/websocketService';
import { DeliveryResponse, MatchDetail, MatchSummary } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { 
  Play, 
  Pause, 
  Square, 
  FastForward, 
  Activity, 
  Flame, 
  CheckCircle2, 
  ShieldCheck, 
  Cpu 
} from 'lucide-react';

export const SimulatorPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const initialMatchId = searchParams.get('matchId') ? Number(searchParams.get('matchId')) : null;

  const [matches, setMatches] = useState<MatchSummary[]>([]);
  const [selectedMatchId, setSelectedMatchId] = useState<number | null>(initialMatchId);
  const [matchDetail, setMatchDetail] = useState<MatchDetail | null>(null);

  const [isRunning, setIsRunning] = useState(false);
  const [speedInterval, setSpeedInterval] = useState(2000); // 2000ms default
  const [liveStreamEvents, setLiveStreamEvents] = useState<DeliveryResponse[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const list = await matchService.getAllMatches();
        setMatches(list);
        if (!selectedMatchId && list.length > 0) {
          const liveOrSched = list.find((m: MatchSummary) => m.status === 'LIVE' || m.status === 'SCHEDULED') || list[0];
          setSelectedMatchId(liveOrSched.id);
        }
      } catch (e) {
        console.error('Error fetching matches', e);
      }
    };
    fetchMatches();
  }, []);

  useEffect(() => {
    if (!selectedMatchId) return;

    const loadMatchData = async () => {
      setLoading(true);
      try {
        const [detail, statusRes] = await Promise.all([
          matchService.getMatchDetails(selectedMatchId),
          simulationService.getStatus(selectedMatchId)
        ]);
        setMatchDetail(detail);
        setIsRunning(statusRes.running);
      } catch (e) {
        console.error('Error loading simulation data', e);
      } finally {
        setLoading(false);
      }
    };

    loadMatchData();

    // Subscribe to STOMP WebSocket for this match
    const unsubscribe = websocketService.subscribeToMatch(
      selectedMatchId,
      (delivery: DeliveryResponse) => {
        setLiveStreamEvents((prev) => [delivery, ...prev.slice(0, 24)]);
        // Update local score
        setMatchDetail((prev: MatchDetail | null) => {
          if (!prev) return prev;
          return {
            ...prev,
            summary: {
              ...prev.summary,
              liveScore: `${delivery.totalRuns}/${delivery.wickets} (${delivery.oversFormatted} ov)`,
              currentRunRate: delivery.currentRunRate,
              result: delivery.matchResult || prev.summary.result,
            }
          };
        });

        if (delivery.isMatchComplete) {
          setIsRunning(false);
        }
      }
    );

    return () => {
      if (unsubscribe) unsubscribe();
    };
  }, [selectedMatchId]);

  const handleStart = async () => {
    if (!selectedMatchId) return;
    try {
      await simulationService.start(selectedMatchId, speedInterval);
      setIsRunning(true);
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error starting simulation');
    }
  };

  const handlePause = async () => {
    if (!selectedMatchId) return;
    try {
      await simulationService.pause(selectedMatchId);
      setIsRunning(false);
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error pausing simulation');
    }
  };

  const handleStop = async () => {
    if (!selectedMatchId) return;
    try {
      await simulationService.stop(selectedMatchId);
      setIsRunning(false);
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error stopping simulation');
    }
  };

  if (loading && !matchDetail) {
    return <LoadingSpinner message="Connecting to simulation engine..." />;
  }

  const summary = matchDetail?.summary;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl flex flex-col md:flex-row items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-white flex items-center gap-2">
            <Cpu className="w-6 h-6 text-emerald-400" />
            <span>Real-Time Match Simulator</span>
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Autonomous ball-by-ball match engine feeding events directly into the authoritative scoring service
          </p>
        </div>

        {/* Match Selector Dropdown */}
        <div className="flex items-center gap-3">
          <label className="text-xs text-slate-400 font-semibold">Select Match:</label>
          <select
            value={selectedMatchId || ''}
            onChange={(e) => setSelectedMatchId(Number(e.target.value))}
            className="bg-slate-950 border border-slate-700 text-white text-xs rounded-xl px-3 py-2 font-medium focus:ring-1 focus:ring-emerald-400 focus:outline-none"
          >
            {matches.map((m) => (
              <option key={m.id} value={m.id}>
                {m.homeTeamShortName} vs {m.awayTeamShortName} ({m.status})
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Control Dashboard */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Controls Panel */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex items-center justify-between pb-3 border-b border-slate-800">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Simulator State</span>
            <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold ${
              isRunning 
                ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30' 
                : 'bg-slate-800 text-slate-400 border border-slate-700'
            }`}>
              <span className={`w-2 h-2 rounded-full ${isRunning ? 'bg-emerald-400 animate-ping' : 'bg-slate-500'}`}></span>
              {isRunning ? 'RUNNING' : 'STOPPED'}
            </span>
          </div>

          {/* Speed Controls */}
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-2">Delivery Pace (Interval):</label>
            <div className="grid grid-cols-3 gap-2">
              {[
                { label: 'Fast (1s)', val: 1000 },
                { label: 'Normal (2s)', val: 2000 },
                { label: 'Relaxed (3s)', val: 3000 }
              ].map((s) => (
                <button
                  key={s.val}
                  onClick={() => setSpeedInterval(s.val)}
                  className={`py-2 px-3 rounded-xl text-xs font-bold border transition-colors ${
                    speedInterval === s.val
                      ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/50'
                      : 'bg-slate-950 text-slate-400 border-slate-800 hover:text-white'
                  }`}
                >
                  {s.label}
                </button>
              ))}
            </div>
          </div>

          {/* Play / Pause / Stop Buttons */}
          <div className="space-y-3">
            {!isRunning ? (
              <button
                onClick={handleStart}
                className="w-full py-3.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm flex items-center justify-center gap-2 shadow-lg shadow-emerald-500/20 transition-all"
              >
                <Play className="w-4 h-4 fill-current" />
                <span>Start Simulation</span>
              </button>
            ) : (
              <button
                onClick={handlePause}
                className="w-full py-3.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-sm flex items-center justify-center gap-2 shadow-lg shadow-amber-500/20 transition-all"
              >
                <Pause className="w-4 h-4 fill-current" />
                <span>Pause Simulation</span>
              </button>
            )}

            <button
              onClick={handleStop}
              className="w-full py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold text-xs border border-slate-700 flex items-center justify-center gap-2 transition-colors"
            >
              <Square className="w-3.5 h-3.5 fill-current" />
              <span>Stop & Reset</span>
            </button>
          </div>

          {summary && (
            <div className="bg-slate-950/60 rounded-xl p-4 border border-slate-800/80 space-y-2 text-xs">
              <span className="font-semibold text-slate-400">Match Overview:</span>
              <div className="flex justify-between text-white font-bold">
                <span>{summary.homeTeamShortName} vs {summary.awayTeamShortName}</span>
                <span className="font-mono text-emerald-400">{summary.liveScore || '-'}</span>
              </div>
              {summary.result && (
                <p className="text-emerald-400 font-medium">{summary.result}</p>
              )}
            </div>
          )}
        </div>

        {/* Real-time Simulated Telemetry Feed */}
        <div className="lg:col-span-2 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-lg flex flex-col">
          <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-800">
            <h3 className="text-sm font-bold text-white flex items-center gap-2">
              <Activity className="w-4 h-4 text-emerald-400" />
              <span>Live Simulator Telemetry Stream</span>
            </h3>
            <span className="text-xs text-slate-500 font-mono">STOMP WebSockets Active</span>
          </div>

          <div className="flex-1 overflow-y-auto max-h-[460px] space-y-3 pr-1">
            {liveStreamEvents.length > 0 ? (
              liveStreamEvents.map((event) => (
                <div
                  key={event.id}
                  className="bg-slate-950 border border-slate-800/90 rounded-xl p-4 flex gap-4 items-start shadow-sm"
                >
                  <div className="shrink-0 w-12 text-center font-mono font-bold text-xs bg-slate-900 text-emerald-400 px-2 py-1 rounded-lg border border-slate-800">
                    {event.overNumber}.{event.ballNumber}
                  </div>
                  <div className="flex-1 space-y-1">
                    <div className="flex items-center justify-between text-xs">
                      <span className="font-semibold text-slate-200">
                        {event.bowlerName} to {event.strikerName}
                      </span>
                      <span className="font-mono font-bold text-emerald-400">
                        {event.wicket ? 'WICKET!' : `${event.batterRuns + event.extraRuns} run(s)`}
                      </span>
                    </div>
                    <p className="text-xs text-slate-400">{event.commentary}</p>
                    <div className="text-[10px] text-slate-500 font-mono">
                      Innings Total: {event.totalRuns}/{event.wickets} ({event.oversFormatted} ov)
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="h-64 flex flex-col items-center justify-center text-center text-slate-500 space-y-2">
                <Play className="w-8 h-8 text-slate-600" />
                <p className="text-sm font-medium">Simulator is ready.</p>
                <p className="text-xs text-slate-600 max-w-xs">
                  Press Start Simulation to watch the engine generate live ball deliveries and broadcast scores in real-time.
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

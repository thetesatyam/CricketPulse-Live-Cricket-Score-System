import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { matchService } from '../services/matchService';
import { scoringService } from '../services/scoringService';
import { teamService } from '../services/teamService';
import { ExtraType, MatchDetail, MatchSummary, Player, WicketType } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Trophy, AlertCircle, RotateCcw, Check, Sparkles } from 'lucide-react';

export const ScorerConsolePage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const initialMatchId = searchParams.get('matchId') ? Number(searchParams.get('matchId')) : null;

  const [matches, setMatches] = useState<MatchSummary[]>([]);
  const [selectedMatchId, setSelectedMatchId] = useState<number | null>(initialMatchId);
  const [matchDetail, setMatchDetail] = useState<MatchDetail | null>(null);

  const [battingPlayers, setBattingPlayers] = useState<Player[]>([]);
  const [bowlingPlayers, setBowlingPlayers] = useState<Player[]>([]);

  const [strikerId, setStrikerId] = useState<number | null>(null);
  const [nonStrikerId, setNonStrikerId] = useState<number | null>(null);
  const [bowlerId, setBowlerId] = useState<number | null>(null);

  const [commentaryText, setCommentaryText] = useState('');
  const [customExtraRuns, setCustomExtraRuns] = useState(0);

  // Wicket modal state
  const [showWicketModal, setShowWicketModal] = useState(false);
  const [wicketType, setWicketType] = useState<WicketType>('CAUGHT');
  const [dismissedId, setDismissedId] = useState<number | null>(null);
  const [fielderId, setFielderId] = useState<number | null>(null);

  // Correction state
  const [showCorrectionModal, setShowCorrectionModal] = useState(false);
  const [correctionReason, setCorrectionReason] = useState('');
  const [lastDeliveryId, setLastDeliveryId] = useState<number | null>(null);

  const [loading, setLoading] = useState(false);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const list = await matchService.getAllMatches();
        setMatches(list);
        if (!selectedMatchId && list.length > 0) {
          const live = list.find((m: MatchSummary) => m.status === 'LIVE') || list[0];
          setSelectedMatchId(live.id);
        }
      } catch (err) {
        console.error('Error fetching matches', err);
      }
    };
    fetchMatches();
  }, []);

  useEffect(() => {
    if (!selectedMatchId) return;

    const loadMatchDetails = async () => {
      setLoading(true);
      try {
        const detail = await matchService.getMatchDetails(selectedMatchId);
        setMatchDetail(detail);

        if (detail.innings && detail.innings.length > 0) {
          const activeInnings = detail.innings[detail.innings.length - 1];
          const [batSquad, bowlSquad] = await Promise.all([
            teamService.getTeamPlayers(activeInnings.battingTeamId),
            teamService.getTeamPlayers(activeInnings.bowlingTeamId)
          ]);

          setBattingPlayers(batSquad);
          setBowlingPlayers(bowlSquad);

          if (detail.striker) setStrikerId(detail.striker.playerId);
          else if (batSquad.length > 0) setStrikerId(batSquad[0].id);

          if (detail.nonStriker) setNonStrikerId(detail.nonStriker.playerId);
          else if (batSquad.length > 1) setNonStrikerId(batSquad[1].id);

          if (detail.currentBowler) setBowlerId(detail.currentBowler.playerId);
          else if (bowlSquad.length > 0) setBowlerId(bowlSquad[bowlSquad.length - 1].id);
        }
      } catch (e) {
        console.error('Error loading match details', e);
      } finally {
        setLoading(false);
      }
    };

    loadMatchDetails();
  }, [selectedMatchId]);

  const handleRecordBall = async (
    batterRuns: number,
    extraType: ExtraType = 'NONE',
    extraRuns: number = 0,
    isWicket: boolean = false
  ) => {
    if (!matchDetail || !matchDetail.innings || matchDetail.innings.length === 0) return;
    const activeInnings = matchDetail.innings[matchDetail.innings.length - 1];

    if (!strikerId || !nonStrikerId || !bowlerId) {
      setErrorMessage('Please select Striker, Non-Striker, and Bowler');
      return;
    }

    setErrorMessage(null);
    setFeedbackMessage(null);

    try {
      const response = await scoringService.recordDelivery(activeInnings.id, {
        strikerId,
        nonStrikerId,
        bowlerId,
        batterRuns,
        extraType,
        extraRuns,
        isWicket,
        wicketType: isWicket ? wicketType : undefined,
        dismissedPlayerId: isWicket ? (dismissedId || strikerId) : undefined,
        fielderId: isWicket ? fielderId || undefined : undefined,
        commentary: commentaryText || undefined,
      });

      setLastDeliveryId(response.id);
      setCommentaryText('');
      setShowWicketModal(false);

      setFeedbackMessage(
        `Recorded: ${isWicket ? 'WICKET (' + wicketType + ')' : batterRuns + ' run(s)' + (extraType !== 'NONE' ? ` + ${extraType}` : '')} | Score: ${response.totalRuns}/${response.wickets} (${response.oversFormatted} ov)`
      );

      // Auto update striker & non-striker rotation from response
      if (response.nextStrikerId) setStrikerId(response.nextStrikerId);
      if (response.nextNonStrikerId) setNonStrikerId(response.nextNonStrikerId);

      // Refresh match state
      const updated = await matchService.getMatchDetails(selectedMatchId!);
      setMatchDetail(updated);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Error recording delivery');
    }
  };

  const handleUndoCorrection = async () => {
    if (!lastDeliveryId || !correctionReason) {
      setErrorMessage('Please provide a reason for the delivery correction');
      return;
    }

    try {
      await scoringService.applyCorrection(lastDeliveryId, correctionReason);
      setShowCorrectionModal(false);
      setCorrectionReason('');
      setFeedbackMessage('Delivery reversed successfully! Innings score recomputed.');

      const updated = await matchService.getMatchDetails(selectedMatchId!);
      setMatchDetail(updated);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Error reversing delivery');
    }
  };

  if (loading && !matchDetail) {
    return <LoadingSpinner message="Preparing scoring console..." />;
  }

  const currentInnings = matchDetail?.innings?.[matchDetail.innings.length - 1];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Console Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl flex flex-col md:flex-row items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-white flex items-center gap-2">
            <Trophy className="w-6 h-6 text-emerald-400" />
            <span>Authoritative Scoring Console</span>
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Certified ball-by-ball scoring with live strike rotation and persistent event logs
          </p>
        </div>

        {/* Match Selector Dropdown */}
        <div className="flex items-center gap-3">
          <label className="text-xs text-slate-400 font-semibold">Active Match:</label>
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

      {feedbackMessage && (
        <div className="bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 p-4 rounded-xl text-xs flex items-center gap-2">
          <Check className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{feedbackMessage}</span>
        </div>
      )}

      {errorMessage && (
        <div className="bg-rose-500/10 border border-rose-500/30 text-rose-300 p-4 rounded-xl text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      {/* Live Match State Bar */}
      {currentInnings && (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-lg flex flex-wrap items-center justify-between gap-6">
          <div>
            <span className="text-xs font-semibold text-slate-400">Batting Team</span>
            <h2 className="text-2xl font-black text-white">{currentInnings.battingTeamName}</h2>
            <p className="text-xs text-slate-400">Innings {currentInnings.inningsNumber} • vs {currentInnings.bowlingTeamName}</p>
          </div>

          <div className="text-center">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Score</span>
            <div className="text-4xl font-black font-mono text-emerald-400">
              {currentInnings.totalRuns}/{currentInnings.wickets}
            </div>
          </div>

          <div className="text-right">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Overs Bowled</span>
            <div className="text-3xl font-black font-mono text-white">
              {currentInnings.oversFormatted}
            </div>
            <p className="text-xs text-slate-400">CRR: <strong className="text-white font-mono">{currentInnings.runRate.toFixed(2)}</strong></p>
          </div>
        </div>
      )}

      {/* Player Selection Dropdowns */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Striker */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 space-y-2">
          <label className="text-xs font-bold text-emerald-400 flex items-center gap-1">
            <span>Striker (On Strike) *</span>
          </label>
          <select
            value={strikerId || ''}
            onChange={(e) => setStrikerId(Number(e.target.value))}
            className="w-full bg-slate-950 border border-slate-700 text-white text-sm rounded-lg px-3 py-2 font-medium"
          >
            {battingPlayers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.fullName} ({p.playingRole})
              </option>
            ))}
          </select>
        </div>

        {/* Non-Striker */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 space-y-2">
          <label className="text-xs font-bold text-slate-300">Non-Striker *</label>
          <select
            value={nonStrikerId || ''}
            onChange={(e) => setNonStrikerId(Number(e.target.value))}
            className="w-full bg-slate-950 border border-slate-700 text-white text-sm rounded-lg px-3 py-2 font-medium"
          >
            {battingPlayers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.fullName} ({p.playingRole})
              </option>
            ))}
          </select>
        </div>

        {/* Bowler */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 space-y-2">
          <label className="text-xs font-bold text-slate-300">Bowler *</label>
          <select
            value={bowlerId || ''}
            onChange={(e) => setBowlerId(Number(e.target.value))}
            className="w-full bg-slate-950 border border-slate-700 text-white text-sm rounded-lg px-3 py-2 font-medium"
          >
            {bowlingPlayers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.fullName} ({p.playingRole})
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Main Scoring Keypad */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-6">
        <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider">
          Scoring Pad (Click to record delivery)
        </h3>

        {/* Runs Buttons */}
        <div className="grid grid-cols-3 sm:grid-cols-6 gap-3">
          {[0, 1, 2, 3, 4, 6].map((run) => (
            <button
              key={run}
              onClick={() => handleRecordBall(run, 'NONE', 0, false)}
              className={`py-4 rounded-xl font-mono text-xl font-black shadow-md transition-all hover:scale-[1.02] ${
                run === 4
                  ? 'bg-blue-600 hover:bg-blue-500 text-white'
                  : run === 6
                  ? 'bg-purple-600 hover:bg-purple-500 text-white'
                  : 'bg-slate-800 hover:bg-slate-700 text-white'
              }`}
            >
              {run}
            </button>
          ))}
        </div>

        {/* Extras & Wicket Keypad */}
        <div className="grid grid-cols-2 sm:grid-cols-5 gap-3 pt-2">
          <button
            onClick={() => handleRecordBall(0, 'WIDE', 1, false)}
            className="py-3 px-4 rounded-xl bg-amber-500/10 border border-amber-500/30 hover:bg-amber-500/20 text-amber-300 font-bold text-sm transition-all"
          >
            Wide (1 extra)
          </button>

          <button
            onClick={() => handleRecordBall(0, 'NO_BALL', 1, false)}
            className="py-3 px-4 rounded-xl bg-orange-500/10 border border-orange-500/30 hover:bg-orange-500/20 text-orange-300 font-bold text-sm transition-all"
          >
            No-Ball (1 extra)
          </button>

          <button
            onClick={() => handleRecordBall(0, 'BYE', 1, false)}
            className="py-3 px-4 rounded-xl bg-slate-800 border border-slate-700 hover:bg-slate-700 text-slate-200 font-bold text-sm transition-all"
          >
            Bye (1 run)
          </button>

          <button
            onClick={() => handleRecordBall(0, 'LEG_BYE', 1, false)}
            className="py-3 px-4 rounded-xl bg-slate-800 border border-slate-700 hover:bg-slate-700 text-slate-200 font-bold text-sm transition-all"
          >
            Leg Bye (1 run)
          </button>

          <button
            onClick={() => {
              setDismissedId(strikerId);
              setShowWicketModal(true);
            }}
            className="py-3 px-4 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-black text-sm col-span-2 sm:col-span-1 shadow-md transition-all"
          >
            WICKET
          </button>
        </div>

        {/* Optional Commentary Box */}
        <div className="pt-2">
          <label className="text-xs font-semibold text-slate-400">Optional Ball Commentary:</label>
          <input
            type="text"
            value={commentaryText}
            onChange={(e) => setCommentaryText(e.target.value)}
            placeholder="e.g. Slashed through backward point for a boundary!"
            className="w-full mt-1.5 bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-sm text-slate-200 focus:ring-1 focus:ring-emerald-400 focus:outline-none"
          />
        </div>

        {/* Undo / Correction Control */}
        {lastDeliveryId && (
          <div className="pt-4 border-t border-slate-800 flex justify-end">
            <button
              onClick={() => setShowCorrectionModal(true)}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-bold text-rose-300 border border-slate-700 transition-colors"
            >
              <RotateCcw className="w-4 h-4" />
              <span>Correct / Revert Last Delivery</span>
            </button>
          </div>
        )}
      </div>

      {/* Wicket Modal */}
      {showWicketModal && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <span className="w-3 h-3 rounded-full bg-rose-500"></span>
              <span>Record Wicket Event</span>
            </h3>

            <div className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-slate-300">Dismissal Type:</label>
                <select
                  value={wicketType}
                  onChange={(e) => setWicketType(e.target.value as WicketType)}
                  className="w-full mt-1 bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-sm text-white"
                >
                  <option value="CAUGHT">Caught</option>
                  <option value="BOWLED">Bowled</option>
                  <option value="LBW">LBW</option>
                  <option value="RUN_OUT">Run Out</option>
                  <option value="STUMPED">Stumped</option>
                  <option value="HIT_WICKET">Hit Wicket</option>
                </select>
              </div>

              <div>
                <label className="font-semibold text-slate-300">Dismissed Player:</label>
                <select
                  value={dismissedId || ''}
                  onChange={(e) => setDismissedId(Number(e.target.value))}
                  className="w-full mt-1 bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-sm text-white"
                >
                  {battingPlayers.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.fullName}
                    </option>
                  ))}
                </select>
              </div>

              {(wicketType === 'CAUGHT' || wicketType === 'RUN_OUT' || wicketType === 'STUMPED') && (
                <div>
                  <label className="font-semibold text-slate-300">Fielder Involved:</label>
                  <select
                    value={fielderId || ''}
                    onChange={(e) => setFielderId(Number(e.target.value))}
                    className="w-full mt-1 bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-sm text-white"
                  >
                    <option value="">None / Unassisted</option>
                    {bowlingPlayers.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName}
                      </option>
                    ))}
                  </select>
                </div>
              )}
            </div>

            <div className="flex gap-3 pt-4 border-t border-slate-800">
              <button
                onClick={() => setShowWicketModal(false)}
                className="w-1/2 py-2.5 rounded-xl bg-slate-800 text-slate-300 font-semibold text-sm hover:bg-slate-700"
              >
                Cancel
              </button>
              <button
                onClick={() => handleRecordBall(0, 'NONE', 0, true)}
                className="w-1/2 py-2.5 rounded-xl bg-rose-600 text-white font-bold text-sm hover:bg-rose-500 shadow-md"
              >
                Confirm Wicket
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Correction / Undo Modal */}
      {showCorrectionModal && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <RotateCcw className="w-5 h-5 text-rose-400" />
              <span>Reverse Delivery #{lastDeliveryId}</span>
            </h3>

            <p className="text-xs text-slate-400">
              This action will mark the delivery as reversed in the audit trail and automatically recompute the innings total, extras, and player statistics without losing audit history.
            </p>

            <div>
              <label className="text-xs font-semibold text-slate-300">Auditable Reason for Correction *:</label>
              <input
                type="text"
                value={correctionReason}
                onChange={(e) => setCorrectionReason(e.target.value)}
                placeholder="e.g., Umpire signaled leg-bye instead of batter runs"
                className="w-full mt-1.5 bg-slate-950 border border-slate-700 rounded-xl p-2.5 text-sm text-white"
              />
            </div>

            <div className="flex gap-3 pt-4 border-t border-slate-800">
              <button
                onClick={() => setShowCorrectionModal(false)}
                className="w-1/2 py-2.5 rounded-xl bg-slate-800 text-slate-300 font-semibold text-sm hover:bg-slate-700"
              >
                Cancel
              </button>
              <button
                onClick={handleUndoCorrection}
                className="w-1/2 py-2.5 rounded-xl bg-rose-600 text-white font-bold text-sm hover:bg-rose-500 shadow-md"
              >
                Apply Reversal
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

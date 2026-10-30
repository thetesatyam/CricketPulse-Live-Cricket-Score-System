import React from 'react';
import { Innings } from '../../types';

interface ScorecardTableProps {
  innings: Innings;
}

export const ScorecardTable: React.FC<ScorecardTableProps> = ({ innings }) => {
  return (
    <div className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-lg mb-6">
      {/* Innings Header */}
      <div className="bg-slate-800/80 px-6 py-4 flex flex-wrap items-center justify-between border-b border-slate-700/80">
        <div>
          <h3 className="text-lg font-bold text-white flex items-center gap-2">
            <span>{innings.battingTeamName} Innings</span>
            <span className="text-xs font-mono font-normal text-slate-400">
              (vs {innings.bowlingTeamName})
            </span>
          </h3>
        </div>
        <div className="text-right">
          <span className="text-2xl font-black font-mono text-emerald-400">
            {innings.totalRuns}/{innings.wickets}
          </span>
          <span className="text-xs text-slate-400 ml-2 font-mono">
            ({innings.oversFormatted} ov, CRR: {innings.runRate.toFixed(2)})
          </span>
        </div>
      </div>

      {/* Batting Scorecard Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse text-sm">
          <thead>
            <tr className="bg-slate-950/50 text-slate-400 text-xs font-semibold uppercase tracking-wider border-b border-slate-800">
              <th className="py-3 px-6">Batter</th>
              <th className="py-3 px-4">Dismissal</th>
              <th className="py-3 px-3 text-right font-mono">R</th>
              <th className="py-3 px-3 text-right font-mono">B</th>
              <th className="py-3 px-3 text-right font-mono">4s</th>
              <th className="py-3 px-3 text-right font-mono">6s</th>
              <th className="py-3 px-4 text-right font-mono">SR</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {innings.batters && innings.batters.length > 0 ? (
              innings.batters.map((b) => (
                <tr key={b.playerId} className="hover:bg-slate-800/30 transition-colors">
                  <td className="py-3 px-6 font-semibold text-white">
                    {b.playerName}
                  </td>
                  <td className="py-3 px-4 text-xs text-slate-400">
                    {b.dismissalInfo}
                  </td>
                  <td className="py-3 px-3 text-right font-mono font-bold text-slate-100">
                    {b.runs}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-400">
                    {b.balls}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-400">
                    {b.fours}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-400">
                    {b.sixes}
                  </td>
                  <td className="py-3 px-4 text-right font-mono text-emerald-400 font-medium">
                    {b.strikeRate.toFixed(2)}
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={7} className="py-4 text-center text-slate-500">
                  No batting data recorded yet
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Extras & Totals Bar */}
      <div className="bg-slate-950/40 px-6 py-3 border-t border-slate-800 flex flex-wrap items-center justify-between text-xs text-slate-400">
        <div>
          <span className="font-semibold text-slate-300">Extras: </span>
          <span className="font-mono text-white font-bold">{innings.extras}</span>
          <span className="ml-1 text-slate-500">
            (b {innings.byeRuns}, lb {innings.legByeRuns}, w {innings.wideRuns}, nb {innings.noBallRuns})
          </span>
        </div>
        <div>
          <span className="font-semibold text-slate-300">Total: </span>
          <span className="font-mono text-white font-bold">
            {innings.totalRuns}/{innings.wickets}
          </span>
          <span className="ml-1 text-slate-500 font-mono">({innings.oversFormatted} Overs)</span>
        </div>
      </div>

      {/* Bowling Scorecard Table */}
      <div className="overflow-x-auto border-t border-slate-800">
        <table className="w-full text-left border-collapse text-sm">
          <thead>
            <tr className="bg-slate-950/50 text-slate-400 text-xs font-semibold uppercase tracking-wider border-b border-slate-800">
              <th className="py-3 px-6">Bowler</th>
              <th className="py-3 px-3 text-right font-mono">O</th>
              <th className="py-3 px-3 text-right font-mono">M</th>
              <th className="py-3 px-3 text-right font-mono">R</th>
              <th className="py-3 px-3 text-right font-mono">W</th>
              <th className="py-3 px-4 text-right font-mono">ECON</th>
              <th className="py-3 px-3 text-right font-mono">WD</th>
              <th className="py-3 px-4 text-right font-mono">NB</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {innings.bowlers && innings.bowlers.length > 0 ? (
              innings.bowlers.map((bw) => (
                <tr key={bw.playerId} className="hover:bg-slate-800/30 transition-colors">
                  <td className="py-3 px-6 font-semibold text-white">
                    {bw.playerName}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-300">
                    {bw.overs}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-400">
                    {bw.maidens}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-300">
                    {bw.runs}
                  </td>
                  <td className="py-3 px-3 text-right font-mono font-bold text-emerald-400">
                    {bw.wickets}
                  </td>
                  <td className="py-3 px-4 text-right font-mono text-slate-300 font-medium">
                    {bw.economy.toFixed(2)}
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-400">
                    {bw.wides}
                  </td>
                  <td className="py-3 px-4 text-right font-mono text-slate-400">
                    {bw.noBalls}
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={8} className="py-4 text-center text-slate-500">
                  No bowling data recorded yet
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

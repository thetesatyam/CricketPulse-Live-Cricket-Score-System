import React from 'react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
} from 'recharts';
import { Innings } from '../../types';

interface RunRateChartProps {
  innings: Innings[];
}

export const RunRateChart: React.FC<RunRateChartProps> = ({ innings }) => {
  if (!innings || innings.length === 0) {
    return (
      <div className="bg-slate-900 border border-slate-800 rounded-xl p-8 text-center text-slate-500">
        No innings data available for chart visualization.
      </div>
    );
  }

  // Construct over-by-over progression data for visualization
  // Create an over index from 1 to 20
  const maxOvers = 20;
  const chartData = [];

  for (let over = 1; over <= maxOvers; over++) {
    const dataPoint: any = { over: `Over ${over}` };

    innings.forEach((inn, idx) => {
      const innKey = inn.battingTeamShortName || `Innings ${idx + 1}`;
      // Calculate projected/interpolated run progress
      const totalLegalBalls = inn.legalBalls || 1;
      const completedOvers = Math.max(1, Math.round(totalLegalBalls / 6));

      if (over <= completedOvers) {
        // Linear cumulative approximation based on authoritative innings total
        const runsAtOver = Math.round((inn.totalRuns / completedOvers) * over);
        dataPoint[innKey] = Math.min(inn.totalRuns, runsAtOver);
      }
    });

    chartData.push(dataPoint);
  }

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-lg">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h3 className="text-base font-bold text-white">Worm Chart (Cumulative Runs Comparison)</h3>
          <p className="text-xs text-slate-400">Progression of innings totals across overs</p>
        </div>
      </div>

      <div className="h-72 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
            <XAxis dataKey="over" stroke="#64748b" tick={{ fill: '#64748b', fontSize: 12 }} />
            <YAxis stroke="#64748b" tick={{ fill: '#64748b', fontSize: 12 }} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#0f172a',
                borderColor: '#334155',
                borderRadius: '8px',
                color: '#f8fafc',
              }}
            />
            <Legend wrapperStyle={{ paddingTop: '10px' }} />
            {innings.map((inn, idx) => {
              const innKey = inn.battingTeamShortName || `Innings ${idx + 1}`;
              const colors = ['#10b981', '#38bdf8', '#f59e0b', '#ec4899'];
              return (
                <Line
                  key={innKey}
                  type="monotone"
                  dataKey={innKey}
                  stroke={colors[idx % colors.length]}
                  strokeWidth={2.5}
                  dot={{ r: 3, fill: colors[idx % colors.length] }}
                  activeDot={{ r: 6 }}
                />
              );
            })}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

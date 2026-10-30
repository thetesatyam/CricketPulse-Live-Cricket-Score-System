import React, { useEffect, useState } from 'react';
import { teamService } from '../services/teamService';
import { Player, Team } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Users, Shield, Flag } from 'lucide-react';

export const TeamsPage: React.FC = () => {
  const [teams, setTeams] = useState<Team[]>([]);
  const [selectedTeamId, setSelectedTeamId] = useState<number | null>(null);
  const [squad, setSquad] = useState<Player[]>([]);
  const [loading, setLoading] = useState(true);
  const [squadLoading, setSquadLoading] = useState(false);

  useEffect(() => {
    const fetchTeams = async () => {
      try {
        const list = await teamService.getAllTeams();
        setTeams(list);
        if (list.length > 0) {
          setSelectedTeamId(list[0].id);
        }
      } catch (e) {
        console.error('Error fetching teams', e);
      } finally {
        setLoading(false);
      }
    };
    fetchTeams();
  }, []);

  useEffect(() => {
    if (!selectedTeamId) return;

    const fetchSquad = async () => {
      setSquadLoading(true);
      try {
        const players = await teamService.getTeamPlayers(selectedTeamId);
        setSquad(players);
      } catch (e) {
        console.error('Error fetching squad', e);
      } finally {
        setSquadLoading(false);
      }
    };
    fetchSquad();
  }, [selectedTeamId]);

  if (loading) {
    return <LoadingSpinner message="Loading team directories..." />;
  }

  const selectedTeam = teams.find((t) => t.id === selectedTeamId);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div>
        <h1 className="text-2xl font-black text-white flex items-center gap-2">
          <Users className="w-6 h-6 text-emerald-400" />
          <span>Teams & Player Rosters</span>
        </h1>
        <p className="text-xs text-slate-400 mt-1">Official national squads, player batting/bowling styles, and roles</p>
      </div>

      {/* Teams Tab Bar */}
      <div className="flex flex-wrap gap-3">
        {teams.map((t) => {
          const active = t.id === selectedTeamId;
          return (
            <button
              key={t.id}
              onClick={() => setSelectedTeamId(t.id)}
              className={`flex items-center gap-3 px-5 py-3 rounded-xl border text-sm font-bold transition-all ${
                active
                  ? 'bg-slate-800 text-white border-emerald-500 shadow-md shadow-emerald-500/10'
                  : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white hover:bg-slate-850'
              }`}
            >
              <div className="w-6 h-6 rounded-full bg-slate-950 flex items-center justify-center text-[10px] text-emerald-400 font-mono">
                {t.shortName}
              </div>
              <span>{t.name}</span>
            </button>
          );
        })}
      </div>

      {/* Selected Team Profile & Squad Roster */}
      {selectedTeam && (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 md:p-8 shadow-xl space-y-6">
          <div className="flex items-center justify-between pb-4 border-b border-slate-800">
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-2xl bg-slate-950 border border-slate-800 flex items-center justify-center font-black text-xl text-emerald-400 font-mono">
                {selectedTeam.shortName}
              </div>
              <div>
                <h2 className="text-2xl font-black text-white">{selectedTeam.name}</h2>
                <p className="text-xs text-slate-400 flex items-center gap-1.5 mt-0.5">
                  <Flag className="w-3.5 h-3.5 text-slate-500" />
                  <span>{selectedTeam.country}</span>
                </p>
              </div>
            </div>

            <span className="text-xs text-slate-500 font-mono">
              {squad.length} Players in Squad
            </span>
          </div>

          {/* Squad Grid */}
          {squadLoading ? (
            <LoadingSpinner message="Loading squad roster..." />
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {squad.map((p) => (
                <div
                  key={p.id}
                  className="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex items-center gap-3 hover:border-slate-700 transition-colors"
                >
                  <div className="w-10 h-10 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-center text-xs font-bold text-slate-300 shrink-0">
                    {p.fullName.charAt(0)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-bold text-sm text-white truncate">{p.fullName}</p>
                    <div className="flex items-center gap-2 text-[11px] text-slate-400 mt-0.5">
                      <span className="text-emerald-400 font-semibold">{p.playingRole.replace('_', ' ')}</span>
                      <span>•</span>
                      <span className="truncate">{p.battingStyle || 'Right-hand'}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

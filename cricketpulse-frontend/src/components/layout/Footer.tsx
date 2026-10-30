import React from 'react';
import { Activity, Code2, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-slate-950 border-t border-slate-900 text-slate-400 py-8 px-4 sm:px-6 lg:px-8 mt-auto">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-lg bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center">
            <Activity className="w-4 h-4 text-emerald-400" />
          </div>
          <span className="font-semibold text-slate-200">CricketPulse</span>
          <span className="text-xs bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full border border-slate-700">v1.0.0</span>
        </div>
        
        <p className="text-xs text-slate-400 text-center">
          Real-Time Cricket Score Management & Autonomous Match Simulation Engine
        </p>

        <div className="flex items-center gap-4 text-xs text-slate-400">
          <span>Spring Boot 3.4</span>
          <span>•</span>
          <span>React 19 & TypeScript</span>
          <span>•</span>
          <span>STOMP WebSockets</span>
        </div>
      </div>
    </footer>
  );
};

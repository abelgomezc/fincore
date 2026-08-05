import React from 'react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-card-50 border-t border-surface-200 px-6 py-4 mt-auto shadow-sm">
      <div className="flex items-center justify-between text-sm text-surface-500">
        <span>© 2026 Abel Gomez · FinCore Banking System</span>
        <div className="flex items-center space-x-4">
          <span className="text-surface-400">v1.0.0</span>
          <span className="text-surface-300">•</span>
          <div className="flex items-center space-x-1 text-success-600">
            <span className="w-2 h-2 bg-success-500 rounded-full animate-pulse"></span>
            <span>Sistema en línea</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

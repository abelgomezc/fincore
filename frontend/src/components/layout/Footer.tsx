import React from 'react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-surface-900 border-t border-surface-700 px-6 py-4 mt-auto">
      <div className="flex items-center justify-between text-sm text-surface-500">
        <span>© 2026 Abel Gomez · FinCore Banking System</span>
        <div className="flex items-center space-x-4">
          <span>v1.0.0</span>
          <span>•</span>
          <span className="flex items-center space-x-1">
            <span className="w-2 h-2 bg-banking-success rounded-full animate-pulse"></span>
            <span>Sistema en línea</span>
          </span>
        </div>
      </div>
    </footer>
  );
};

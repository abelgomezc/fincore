import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { clsx } from '@/lib/utils';

export const Header: React.FC = () => {
  const { user } = useAuthStore();
  const navigate = useNavigate();

  return (
    <header className="bg-surface-900 border-b border-surface-700 px-6 py-3 flex items-center justify-between">
      <button
        onClick={() => navigate(-1)}
        className="text-surface-400 hover:text-surface-200 transition-colors"
      >
        ←
      </button>

      <div className="flex items-center space-x-4">
        <div className="text-right">
          <div className="text-sm font-medium text-surface-100">{user?.nombreCompleto || 'Usuario'}</div>
          <div className="text-xs text-surface-500">{user?.email || ''}</div>
        </div>
        <div className="w-10 h-10 rounded-full bg-primary-600 flex items-center justify-center text-white font-bold">
          {user?.nombreCompleto?.charAt(0) || 'U'}
        </div>
      </div>
    </header>
  );
};

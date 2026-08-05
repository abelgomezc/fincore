import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { clsx } from '@/lib/utils';
import { ChevronLeft, Bell, Sun, Moon, User } from 'lucide-react';

export const Header: React.FC = () => {
  const { user } = useAuthStore();
  const navigate = useNavigate();

  return (
    <header className="bg-card-50 border-b border-surface-200 px-6 py-3 flex items-center justify-between shadow-sm sticky top-0 z-10">
      <button
        onClick={() => navigate(-1)}
        className="p-1.5 rounded-lg text-surface-500 hover:text-dark-500 hover:bg-surface-100 transition-colors"
      >
        <ChevronLeft className="w-5 h-5" />
      </button>

      <div className="flex items-center space-x-4">
        <button className="p-1.5 rounded-lg text-surface-500 hover:text-dark-500 hover:bg-surface-100 transition-colors relative">
          <Bell className="w-5 h-5" />
          <span className="absolute -top-0.5 -right-0.5 w-2 h-2 bg-danger-500 rounded-full"></span>
        </button>

        <button className="p-1.5 rounded-lg text-surface-500 hover:text-dark-500 hover:bg-surface-100 transition-colors">
          <Sun className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-3">
          <div className="text-right">
            <div className="text-sm font-medium text-dark-500">
              {user?.nombreCompleto || 'Usuario'}
            </div>
            <div className="text-xs text-surface-400 flex items-center">
              <User className="w-3 h-3 mr-1" />
              {user?.email || ''}
            </div>
          </div>
          <div className="w-10 h-10 rounded-full bg-gradient-to-r from-primary-500 to-primary-700 flex items-center justify-center text-white font-bold">
            <User className="w-5 h-5" />
          </div>
        </div>
      </div>
    </header>
  );
};

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { clsx } from '@/lib/utils';
import { IconChevronLeft, IconBell, IconSun, IconMoon, IconUser } from '@tabler/icons-react';

export const Header: React.FC = () => {
  const { user, darkMode, toggleDarkMode } = useAuthStore();
  const navigate = useNavigate();

  return (
    <header className={clsx(
      'px-6 py-3 flex items-center justify-between shadow-sm sticky top-0 z-10 border-b',
      darkMode ? 'bg-slate-900 border-slate-700' : 'bg-white border-slate-200'
    )}>
      <button
        onClick={() => navigate(-1)}
        className={clsx(
          'p-1.5 rounded-lg transition-colors',
          darkMode ? 'text-slate-400 hover:text-slate-200 hover:bg-slate-800' : 'text-slate-500 hover:text-slate-800 hover:bg-slate-100'
        )}
      >
        <IconChevronLeft className="w-5 h-5" />
      </button>

      <div className="flex items-center space-x-4">
        <button
          onClick={toggleDarkMode}
          className={clsx(
            'p-1.5 rounded-lg transition-colors',
            darkMode ? 'text-slate-400 hover:text-yellow-400 hover:bg-slate-800' : 'text-slate-500 hover:text-slate-800 hover:bg-slate-100'
          )}
        >
          {darkMode ? <IconSun className="w-5 h-5" /> : <IconMoon className="w-5 h-5" />}
        </button>

        <button className={clsx(
          'p-1.5 rounded-lg transition-colors relative',
          darkMode ? 'text-slate-400 hover:text-slate-200 hover:bg-slate-800' : 'text-slate-500 hover:text-slate-800 hover:bg-slate-100'
        )}>
          <IconBell className="w-5 h-5" />
          <span className="absolute -top-0.5 -right-0.5 w-2 h-2 bg-red-500 rounded-full"></span>
        </button>

        <div className="flex items-center space-x-3">
          <div className={clsx('text-right', darkMode ? 'text-slate-200' : 'text-slate-800')}>
            <div className="text-sm font-medium">
              {user?.nombreCompleto || 'Usuario'}
            </div>
            <div className="text-xs flex items-center" style={{ color: darkMode ? '#94a3b8' : '#64748b' }}>
              <IconUser className="w-3 h-3 mr-1" />
              {user?.email || ''}
            </div>
          </div>
          <div className={clsx(
            'w-10 h-10 rounded-full flex items-center justify-center font-bold',
            darkMode ? 'bg-gradient-to-r from-blue-600 to-blue-800 text-white' : 'bg-gradient-to-r from-blue-600 to-blue-800 text-white'
          )}>
            <IconUser className="w-5 h-5" />
          </div>
        </div>
      </div>
    </header>
  );
};

import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { clsx } from '@/lib/utils';

const navigation = [
  { name: 'Dashboard', href: '/', icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z' },
  { name: 'Transferencias', href: '/transfers', icon: 'M12 8v4l3 3' },
  { name: 'Cuentas', href: '/accounts', icon: 'M3 7h18M3 12h18M3 17h18' },
  { name: 'Auditoría', href: '/audit', icon: 'M9 12h6m2 0a9 9 0 11-18 0 9 9 0 0118 0z' },
];

const adminNavigation = [
  { name: 'Backoffice', href: '/backoffice', icon: 'M12 8c-2.21 0-4.21.72-5.76 1.94A7.96 7.96 0 003 12v6a2 2 0 002 2h14a2 2 0 002-2v-6c0-1.69-.93-3.16-2.24-3.86A7.93 7.93 0 0013 7a7.96 7.96 0 01-1 3.54 4 4 0 01-5.5 2 4.01 4.01 0 01-.5-1.5A4 4 0 006 11a4 4 0 006-4c0-.19-.01-.38-.03-.56A5.99 5.99 0 0118 11v4a2 2 0 01-2 2H8a4 4 0 01-.03-5.68 5 5 0 004.03-3.32z' },
];

export const Sidebar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('SUPER_ADMIN');

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const allNav = isAdmin ? [...navigation, ...adminNavigation] : navigation;

  return (
    <aside className="fixed inset-y-0 left-0 w-64 bg-surface-900 text-surface-100 flex flex-col overflow-y-auto">
      <div className="p-4 border-b border-surface-700">
        <h1 className="text-xl font-bold text-primary-400">FinCore Banking</h1>
      </div>

      <nav className="flex-1 px-2 py-4 space-y-1">
        {allNav.map((item) => {
          const isActive = location.pathname === item.href || location.pathname.startsWith(item.href + '/');
          return (
            <Link
              key={item.name}
              to={item.href}
              className={clsx(
                'flex items-center space-x-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary-600 text-white'
                  : 'text-surface-300 hover:bg-surface-800 hover:text-white'
              )}
            >
              <svg className="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={item.icon} />
              </svg>
              <span>{item.name}</span>
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t border-surface-700">
        <button
          onClick={handleLogout}
          className="flex items-center space-x-3 w-full px-3 py-2 text-sm font-medium text-surface-300 hover:bg-surface-800 hover:text-white rounded-lg transition-colors"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a2 2 0 11-2 2v-1m-2-6a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <span>Cerrar Sesión</span>
        </button>
      </div>
    </aside>
  );
};

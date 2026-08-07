import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { useAccountStore } from '@/store/accountStore';
import { clsx } from '@/lib/utils';
import { Badge } from '@/components/ui/Badge';
import { motion, AnimatePresence } from 'framer-motion';
import {
  IconLayoutDashboard,
  IconCreditCard,
  IconTransfer,
  IconFileText,
  IconShieldCheck,
  IconAlertTriangle,
  IconBuildingBank,
  IconSettings,
  IconLogout,
  IconChevronDown,
  IconWallet,
} from '@tabler/icons-react';

interface NavItem {
  name: string;
  href: string;
  icon: any;
}

const navigation: NavItem[] = [
  { name: 'Dashboard', href: '/', icon: IconLayoutDashboard },
  { name: 'Cuentas', href: '/accounts', icon: IconCreditCard },
  { name: 'Transferencias', href: '/transfers', icon: IconTransfer },
  { name: 'Extracto', href: '/accounts', icon: IconFileText },
  { name: 'Auditoría', href: '/audit', icon: IconShieldCheck },
];

const adminNavigation: NavItem[] = [
  { name: 'Fraude', href: '/fraud', icon: IconAlertTriangle },
  { name: 'BackOffice', href: '/backoffice', icon: IconBuildingBank },
  { name: 'Configuración', href: '/settings', icon: IconSettings },
];

export const Sidebar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const { cuentas, selectedCuentaId, setSelectedCuenta, fetchCuentas } = useAccountStore();
  const [showAccountSelector, setShowAccountSelector] = useState(false);

  useEffect(() => {
    if (user?.id && cuentas.length === 0) {
      fetchCuentas(user.id);
    }
  }, [user?.id, cuentas.length, fetchCuentas]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const selectedCuenta = cuentas.find((c) => c.id === selectedCuentaId);

  const getRoleLabel = (roles: string[]): string => {
    if (roles.includes('SUPER_ADMIN')) return 'Super Admin';
    if (roles.includes('ADMIN')) return 'Administrador';
    return 'Usuario';
  };

  const getRoleBadgeVariant = (roles: string[]): 'primary' | 'warning' => {
    if (roles.includes('SUPER_ADMIN') || roles.includes('ADMIN')) return 'primary';
    return 'warning';
  };

  const allNav = isAdmin ? [...navigation, ...adminNavigation] : navigation;

  return (
    <aside className="fixed inset-y-0 left-0 w-64 bg-white border-r border-slate-200 flex flex-col overflow-y-auto shadow-lg z-20">
      <motion.div
        initial={{ x: -30, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        transition={{ duration: 0.4, delay: 0.1 }}
        className="p-6 border-b border-slate-200"
      >
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-900 to-blue-700 flex items-center justify-center shadow-lg">
            <IconBuildingBank className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-blue-900">FinCore Banking</h1>
            <p className="text-xs text-slate-500">Sistema Financiero Moderno</p>
          </div>
        </div>
      </motion.div>

      {user && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.2 }}
          className="px-6 py-3 border-b border-slate-200"
        >
          <div className="flex items-center space-x-3 mb-2">
            <div className="w-8 h-8 rounded-full bg-gradient-to-r from-blue-600 to-blue-800 flex items-center justify-center text-white font-bold text-sm">
              {user.nombreCompleto?.charAt(0) || 'U'}
            </div>
            <div>
              <div className="font-medium text-sm text-slate-800">{user.nombreCompleto || user.username}</div>
              <div className="text-xs text-slate-500">{user.email}</div>
            </div>
          </div>
          <Badge variant={getRoleBadgeVariant(user.roles)} size="sm">
            {getRoleLabel(user.roles)}
          </Badge>
        </motion.div>
      )}

      {cuentas.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.25 }}
          className="px-6 py-3 border-b border-slate-200"
        >
          <label className="block text-xs font-medium text-slate-500 mb-1">Cuenta activa</label>
          <div className="relative">
            <button
              onClick={() => setShowAccountSelector(!showAccountSelector)}
              className="w-full flex items-center justify-between px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm hover:bg-slate-100 transition-colors"
            >
              <div className="flex items-center space-x-2">
                <IconWallet className="w-4 h-4 text-slate-400" />
                <span className="font-mono text-slate-800">
                  {selectedCuenta?.numeroCuenta || 'Seleccionar'}
                </span>
              </div>
              <IconChevronDown className={clsx('w-4 h-4 text-slate-400 transition-transform', showAccountSelector && 'rotate-180')} />
            </button>

            <AnimatePresence>
              {showAccountSelector && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  className="absolute z-30 w-full mt-1 bg-white border border-slate-200 rounded-lg shadow-lg overflow-hidden"
                >
                  {cuentas.map((cuenta) => (
                    <button
                      key={cuenta.id}
                      onClick={() => {
                        setSelectedCuenta(cuenta.id);
                        setShowAccountSelector(false);
                      }}
                      className={clsx(
                        'w-full text-left px-3 py-2 text-sm hover:bg-blue-50 transition-colors',
                        cuenta.id === selectedCuentaId && 'bg-blue-50 text-blue-900'
                      )}
                    >
                      <div className="font-mono text-xs">{cuenta.numeroCuenta}</div>
                      <div className="text-xs text-slate-500">
                        {cuenta.tipo} · {cuenta.moneda}
                      </div>
                    </button>
                  ))}
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </motion.div>
      )}

      <nav className="flex-1 px-3 py-4 space-y-1">
        {allNav.map((item, index) => {
          const isActive = location.pathname === item.href || location.pathname.startsWith(item.href + '/');
          const Icon = item.icon;
          return (
            <motion.div
              key={item.name}
              initial={{ x: -20, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              transition={{ duration: 0.2, delay: 0.1 + index * 0.03 }}
            >
              <Link
                to={item.href}
                className={clsx(
                  'flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all',
                  'hover:pl-4',
                  isActive
                    ? 'bg-blue-900 text-white rounded-xl border-l-4 border-blue-300'
                    : 'text-slate-600 hover:bg-blue-50'
                )}
              >
                <Icon className={clsx('w-5 h-5 flex-shrink-0 transition-transform', isActive ? 'w-6 h-6' : '')} />
                <span>{item.name}</span>
              </Link>
            </motion.div>
          );
        })}
      </nav>

      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3, delay: 0.5 }}
        className="p-4 border-t border-slate-200"
      >
        <button
          onClick={handleLogout}
          className="flex items-center space-x-3 w-full px-3 py-2.5 text-sm font-medium text-slate-600 hover:bg-blue-50 rounded-lg transition-all"
        >
          <IconLogout className="w-5 h-5 flex-shrink-0" />
          <span>Cerrar Sesión</span>
        </button>
      </motion.div>
    </aside>
  );
};

import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAuthStore } from '@/store/authStore';
import { motion, AnimatePresence } from 'framer-motion';

import { LoginPage } from '@/pages/LoginPage';
import { DashboardPage } from '@/pages/DashboardPage';
import { TransferPage } from '@/pages/TransferPage';
import { AccountDetailPage } from '@/pages/AccountDetailPage';
import { AuditPage } from '@/pages/AuditPage';
import { BackofficePage } from '@/pages/BackofficePage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30000,
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const darkMode = useAuthStore((state) => state.darkMode);

  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [darkMode]);

  return <>{children}</>;
};

const RequireAuth = () => {
  const { isAuthenticated, checkAuth } = useAuthStore();
  const isAuthed = checkAuth() || isAuthenticated;
  if (!isAuthed) return <Navigate to="/login" replace />;
  return <Outlet />;
};

const RequireAdmin = () => {
  const { user, isAuthenticated, checkAuth } = useAuthStore();
  const isAuthed = checkAuth() || isAuthenticated;
  if (!isAuthed) return <Navigate to="/login" replace />;
  const isAdmin = user?.roles?.some((r) => r === 'ADMIN' || r === 'SUPER_ADMIN' || r === 'AFRICANO');
  if (!isAdmin) return <Navigate to="/" replace />;
  return <Outlet />;
};

const AnimatedRoutes: React.FC = () => {
  const location = useLocation();
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/login" element={<LoginPage />} />

        <Route element={<RequireAuth />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/transfers" element={<TransferPage />} />
          <Route path="/accounts" element={<DashboardPage />} />
          <Route path="/accounts/:id" element={<AccountDetailPage />} />
          <Route path="/extracto" element={<DashboardPage />} />
          <Route path="/audit" element={<AuditPage />} />
        </Route>

        <Route element={<RequireAdmin />}>
          <Route path="/fraud" element={<BackofficePage />} />
          <Route path="/backoffice" element={<BackofficePage />} />
          <Route path="/settings" element={<BackofficePage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AnimatePresence>
  );
};

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <ThemeProvider>
          <div className="min-h-screen bg-slate-50 text-slate-800 font-sans dark:bg-slate-900 dark:text-slate-100">
            <AnimatedRoutes />
          </div>
        </ThemeProvider>
      </Router>
    </QueryClientProvider>
  );
}

export default App;

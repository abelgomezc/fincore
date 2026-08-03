import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAuthStore } from '@/store/authStore';

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

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="min-h-screen bg-surface-950 text-surface-100 font-sans">
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<RequireAuth />}>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/transfers" element={<TransferPage />} />
              <Route path="/accounts" element={<DashboardPage />} />
              <Route path="/accounts/:id" element={<AccountDetailPage />} />
              <Route path="/audit" element={<AuditPage />} />
            </Route>

            <Route element={<RequireAdmin />}>
              <Route path="/backoffice" element={<BackofficePage />} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;

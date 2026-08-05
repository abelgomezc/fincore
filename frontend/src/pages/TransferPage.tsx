import React, { useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { TransferDemoLive } from '@/components/transfer/TransferDemoLive';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { IconTransfer } from '@tabler/icons-react';

export const TransferPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) return null;

  return (
    <div className="min-h-screen bg-slate-50 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden ml-64">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4 }}
              className="flex items-center justify-between mb-6"
            >
              <h1 className="text-3xl font-bold text-slate-800 flex items-center">
                <IconTransfer className="w-7 h-7 mr-3 text-blue-600" />
                Transferencias
              </h1>
            </motion.div>
            <TransferDemoLive />
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};

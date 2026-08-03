import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { TransferForm } from '@/components/transfer/TransferForm';
import { TransferDemoLive } from '@/components/transfer/TransferDemoLive';
import { TransferTimeline } from '@/components/transfer/TransferTimeline';
import { useAuthStore } from '@/store/authStore';
import { useTransferStore } from '@/store/transferStore';
import { useNavigate } from 'react-router-dom';

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
    <div className="min-h-screen bg-surface-950 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <h1 className="text-2xl font-bold text-surface-100 mb-6">Transferencias</h1>
            <TransferDemoLive />
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};

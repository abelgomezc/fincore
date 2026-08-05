import React from 'react';
import ReactDOM from 'react-dom/client';
import { Toaster } from 'react-hot-toast';
import App from './App';
import '@/index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
    <Toaster
      position="top-right"
      toastOptions={{
        duration: 4000,
        style: {
          background: '#ffffff',
          color: '#2C3E50',
          border: '1px solid #e5e7eb',
          borderRadius: '0.75rem',
          boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        },
        success: {
          icon: '✅',
          style: {
            background: '#ffffff',
            color: '#2C3E50',
            border: '1px solid #d1fae5',
            borderRadius: '0.75rem',
          },
        },
        error: {
          icon: '❌',
          style: {
            background: '#ffffff',
            color: '#2C3E50',
            border: '1px solid #fee2e2',
            borderRadius: '0.75rem',
          },
          duration: 7000,
        },
        loading: {
          icon: '⏳',
          style: {
            background: '#ffffff',
            color: '#2C3E50',
            border: '1px solid #dbeafe',
            borderRadius: '0.75rem',
          },
          duration: Infinity,
        },
      }}
    />
  </React.StrictMode>,
);

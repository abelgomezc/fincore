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
        className: 'toast',
        success: {
          icon: '✅',
          className: 'toast',
          style: {
            border: '1px solid #d1fae5',
          },
        },
        error: {
          icon: '❌',
          className: 'toast',
          style: {
            border: '1px solid #fee2e2',
          },
          duration: 7000,
        },
        loading: {
          icon: '⏳',
          className: 'toast',
          style: {
            border: '1px solid #dbeafe',
          },
          duration: Infinity,
        },
      }}
    />
  </React.StrictMode>,
);

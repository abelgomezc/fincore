import React from 'react';
import { clsx } from '@/lib/utils';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
}

export const Input: React.FC<InputProps> = ({ label, error, icon, className, ...props }) => {
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-dark-500 dark:text-slate-200 mb-2">{label}</label>
      )}
      <div className="relative">
        {icon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-500 dark:text-slate-400 pointer-events-none">
            {icon}
          </div>
        )}
        <input
          className={clsx(
            'w-full rounded-lg border text-dark-500 dark:text-slate-100 placeholder-surface-400 dark:placeholder-slate-500 transition-colors',
            icon ? 'pl-10 pr-3 py-2.5' : 'px-3 py-2.5',
            'bg-card-50 dark:bg-slate-800',
            error
              ? 'border-danger-300 focus:border-danger-500'
              : 'border-surface-300 dark:border-slate-600 focus:border-primary-500 focus:outline-none',
            className
          )}
          {...props}
        />
      </div>
      {error && <p className="text-danger-500 text-xs mt-1">{error}</p>}
    </div>
  );
};

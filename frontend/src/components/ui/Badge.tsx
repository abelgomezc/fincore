import React from 'react';
import { clsx } from '@/lib/utils';

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary';
  size?: 'sm' | 'md';
}

const variantClasses = {
  success: 'bg-success-100 dark:bg-success-900/40 text-success-700 dark:text-success-300 border border-success-300 dark:border-success-700',
  warning: 'bg-warning-100 dark:bg-warning-900/40 text-warning-700 dark:text-warning-300 border border-warning-300 dark:border-warning-700',
  danger: 'bg-danger-100 dark:bg-danger-900/40 text-danger-700 dark:text-danger-300 border border-danger-300 dark:border-danger-700',
  info: 'bg-primary-100 dark:bg-primary-900/40 text-primary-700 dark:text-primary-300 border border-primary-300 dark:border-primary-700',
  neutral: 'bg-surface-200 dark:bg-slate-700 text-surface-600 dark:text-slate-300 border border-surface-300 dark:border-slate-600',
  primary: 'bg-primary-100 dark:bg-primary-900/40 text-primary-700 dark:text-primary-300 border border-primary-300 dark:border-primary-700',
};

const sizeClasses = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-3 py-1 text-sm',
};

export const Badge: React.FC<BadgeProps> = ({
  variant = 'neutral',
  size = 'md',
  children,
  className,
  ...props
}) => {
  return (
    <span
      className={clsx(
        'inline-flex items-center rounded-full font-medium',
        variantClasses[variant],
        sizeClasses[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  );
};

import React from 'react';
import { clsx } from '@/lib/utils';

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'success';
export type ButtonSize = 'sm' | 'md' | 'lg';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  icon?: React.ReactNode;
  loading?: boolean;
}

const baseClasses = 'inline-flex items-center justify-center rounded-lg font-medium transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:pointer-events-none disabled:opacity-50';

const variantClasses: Record<ButtonVariant, string> = {
  primary: 'bg-primary-500 hover:bg-primary-600 text-white shadow-md hover:shadow-lg focus:ring-primary-500',
  secondary: 'bg-surface-200 dark:bg-slate-700 hover:bg-surface-300 dark:hover:bg-slate-600 text-dark-500 dark:text-slate-200 focus:ring-surface-500',
  outline: 'border border-surface-400 dark:border-slate-600 hover:bg-surface-100 dark:hover:bg-slate-700 text-dark-500 dark:text-slate-200 focus:ring-surface-500',
  ghost: 'hover:bg-surface-100 dark:hover:bg-slate-700 text-surface-600 dark:text-slate-300 focus:ring-surface-500',
  danger: 'bg-danger-500 hover:bg-danger-600 text-white focus:ring-danger-500',
  success: 'bg-success-500 hover:bg-success-600 text-white focus:ring-success-500',
};

const sizeClasses: Record<ButtonSize, string> = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-sm',
  lg: 'px-6 py-3 text-base',
};

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  icon,
  children,
  className,
  loading,
  disabled,
  ...props
}) => {
  return (
    <button
      className={clsx(baseClasses, variantClasses[variant], sizeClasses[size], className)}
      disabled={disabled || loading}
      {...props}
    >
      {icon && !loading && <span className="mr-2 flex-shrink-0">{icon}</span>}
      {loading && <span className="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />}
      {children}
    </button>
  );
};

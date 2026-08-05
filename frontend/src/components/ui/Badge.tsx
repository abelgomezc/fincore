import React from 'react';
import { clsx } from '@/lib/utils';

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary';
  size?: 'sm' | 'md';
}

const variantClasses = {
  success: 'bg-success-100 text-success-700 border border-success-300',
  warning: 'bg-warning-100 text-warning-700 border border-warning-300',
  danger: 'bg-danger-100 text-danger-700 border border-danger-300',
  info: 'bg-primary-100 text-primary-700 border border-primary-300',
  neutral: 'bg-surface-200 text-surface-600 border border-surface-300',
  primary: 'bg-primary-100 text-primary-700 border border-primary-300',
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

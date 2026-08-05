import React from 'react';
import { clsx } from '@/lib/utils';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  title?: string;
  subtitle?: string;
  icon?: React.ReactNode;
  footer?: React.ReactNode;
  noPadding?: boolean;
}

export const Card: React.FC<CardProps> = ({
  title,
  subtitle,
  icon,
  footer,
  noPadding = false,
  children,
  className,
  ...props
}) => {
  return (
    <div
      className={clsx(
        'bg-card-50 rounded-xl border border-surface-200',
        'transition-all duration-200 hover:shadow-md',
        className
      )}
      {...props}
    >
      {(title || subtitle || icon) && (
        <div className="px-6 py-4 border-b border-surface-200">
          <div className="flex items-center space-x-3">
            {icon && <span className="text-primary-500 flex-shrink-0">{icon}</span>}
            <div>
              {title && <h2 className="text-lg font-semibold text-dark-500">{title}</h2>}
              {subtitle && <p className="text-sm text-surface-500">{subtitle}</p>}
            </div>
          </div>
        </div>
      )}
      <div className={noPadding ? '' : 'px-6 py-4'}>{children}</div>
      {footer && <div className="px-6 py-4 border-t border-surface-200 bg-surface-100/50">{footer}</div>}
    </div>
  );
};

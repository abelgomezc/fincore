import React from 'react';
import { clsx } from '@/lib/utils';

export interface StatItem {
  label: string;
  value: string | number;
  icon: React.ReactNode;
  trend?: 'up' | 'down' | 'neutral';
  trendValue?: string;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'neutral';
}

interface StatsGridProps {
  items: StatItem[];
  cols?: 2 | 3 | 4;
}

const colorMap = {
  primary: 'text-primary-500',
  success: 'text-success-500',
  warning: 'text-warning-500',
  error: 'text-danger-500',
  neutral: 'text-surface-500',
};

export const StatsGrid: React.FC<StatsGridProps> = ({ items, cols = 4 }) => {
  const gridCols = {
    2: 'grid-cols-1 sm:grid-cols-2',
    3: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3',
    4: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-4',
  };

  return (
    <div className={clsx('grid gap-4', gridCols[cols])}>
      {items.map((item, index) => (
        <div
          key={item.label + index}
          className="bg-card-50 rounded-xl p-4 border border-surface-200 shadow-card hover:shadow-card-hover transition-shadow"
        >
          <div className="flex items-center justify-between mb-2">
            <div className="p-2 bg-surface-100 rounded-lg">{item.icon}</div>
            {item.trend && (
              <span
                className={clsx(
                  'text-xs font-medium flex items-center',
                  item.trend === 'up' ? 'text-success-600' :
                  item.trend === 'down' ? 'text-danger-600' : 'text-surface-500'
                )}
              >
                {item.trend === 'up' && '▲'}
                {item.trend === 'down' && '▼'}
                {item.trendValue}
              </span>
            )}
          </div>
          <div className="text-2xl font-bold text-dark-500">{item.value}</div>
          <div className="text-sm text-surface-500 mt-1">{item.label}</div>
        </div>
      ))}
    </div>
  );
};

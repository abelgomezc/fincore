import React from 'react';
import { Saldo } from '@/types/account';
import { formatCurrency } from '@/lib/utils';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Area,
  AreaChart,
} from 'recharts';

interface BalanceChartProps {
  saldo?: Saldo;
  movimientos?: Array<{ fecha: string; saldoNuevo: number }>;
  isLoading?: boolean;
}

export const BalanceChart: React.FC<BalanceChartProps> = ({ saldo, movimientos, isLoading }) => {
  if (isLoading || !movimientos || movimientos.length === 0) {
    return (
      <div className="bg-surface-100 rounded-xl p-6 animate-pulse">
        <div className="h-4 bg-surface-300 rounded w-1/4 mb-4"></div>
        <div className="h-64 bg-surface-300 rounded w-full"></div>
      </div>
    );
  }

  const data = movimientos.map((m) => ({
    fecha: new Date(m.fecha).toLocaleTimeString('es-EC', { hour: '2-digit', minute: '2-digit' }),
    saldo: m.saldoNuevo,
  }));

  return (
    <div className="bg-card-50 rounded-xl p-6 border border-surface-200">
      <h3 className="text-lg font-semibold text-dark-500 mb-4">Evolución del Saldo</h3>
      <div className="h-64 w-full">
      <ResponsiveContainer>
          <AreaChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="fecha" stroke="#9ca3af" fontSize={12} />
            <YAxis
              stroke="#9ca3af"
              fontSize={12}
              tickFormatter={(value) => formatCurrency(value)}
            />
            <Tooltip
              contentStyle={{ backgroundColor: '#ffffff', border: '1px solid #e5e7eb' }}
              formatter={(value: number) => [formatCurrency(value), 'Saldo']}
              labelClassName="text-dark-500"
            />
            <Area
              type="monotone"
              dataKey="saldo"
              stroke="#1B4F8A"
              fill="url(#colorBalance)"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
            <defs>
              <linearGradient id="colorBalance" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#1B4F8A" stopOpacity={0.3} />
                <stop offset="95%" stopColor="#1B4F8A" stopOpacity={0} />
              </linearGradient>
            </defs>
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export const TOKEN_KEY = 'fincore_access_token';
export const REFRESH_TOKEN_KEY = 'fincore_refresh_token';

export const getToken = (): string | null => localStorage.getItem(TOKEN_KEY);
export const setToken = (token: string): void => localStorage.setItem(TOKEN_KEY, token);
export const removeToken = (): void => localStorage.removeItem(TOKEN_KEY);

export const getRefreshToken = (): string | null => localStorage.getItem(REFRESH_TOKEN_KEY);
export const setRefreshToken = (token: string): void => localStorage.setItem(REFRESH_TOKEN_KEY, token);
export const removeRefreshToken = (): void => localStorage.removeItem(REFRESH_TOKEN_KEY);

export const formatCurrency = (amount: number, currency = 'USD'): string => {
  return new Intl.NumberFormat('es-EC', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
  }).format(amount);
};

export const formatDate = (date: string): string => {
  return new Intl.DateTimeFormat('es-EC', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(date));
};

export const formatDateRelative = (date: string): string => {
  const now = new Date();
  const then = new Date(date);
  const diffMs = now.getTime() - then.getTime();
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHr = Math.floor(diffMin / 60);

  if (diffSec < 60) return `${diffSec}s`;
  if (diffMin < 60) return `${diffMin}m`;
  if (diffHr < 24) return `${diffHr}h`;
  return formatDate(date);
};

export const clsx = (...classes: (string | false | undefined)[]) => {
  return classes.filter(Boolean).join(' ');
};

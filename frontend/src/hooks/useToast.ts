import toast, { ToastOptions } from 'react-hot-toast';

const baseToast: ToastOptions = {
  style: {
    background: '#ffffff',
    color: '#2C3E50',
    border: '1px solid #e5e7eb',
    borderRadius: '0.75rem',
    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
  },
  duration: 5000,
  position: 'top-right',
};

export const useToast = () => {
  const toastSuccess = (title: string, message?: string) => {
    toast.success(message ? `${title}: ${message}` : title, {
      ...baseToast,
      icon: '✅',
      style: {
        ...baseToast.style,
        border: '1px solid #d1fae5',
      },
    });
  };

  const toastError = (title: string, message?: string) => {
    toast.error(message ? `${title}: ${message}` : title, {
      ...baseToast,
      icon: '❌',
      style: {
        ...baseToast.style,
        border: '1px solid #fee2e2',
      },
      duration: 7000,
    });
  };

  const toastLoading = (title: string) => {
    return toast.loading(title, {
      ...baseToast,
      icon: '⏳',
      style: {
        ...baseToast.style,
        border: '1px solid #dbeafe',
      },
      duration: Infinity,
    });
  };

  const toastInfo = (title: string, message?: string) => {
    toast(message ? `${title}: ${message}` : title, {
      ...baseToast,
      icon: 'ℹ️',
      style: {
        ...baseToast.style,
        border: '1px solid #dbeafe',
      },
    });
  };

  return { toastSuccess, toastError, toastLoading, toastInfo };
};

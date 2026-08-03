import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { jwtDecode } from 'jwt-decode';
import { authApi } from '@/api/authApi';
import { Usuario, AuthResponse, JwtPayload } from '@/types';
import { getToken, setToken, removeToken, getRefreshToken, setRefreshToken, removeRefreshToken } from '@/lib/utils';

interface AuthState {
  user: Usuario | null;
  accessToken: string | null;
  refreshTokenValue: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  tokenExpiresAt: number | null;
}

interface AuthActions {
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  refreshAccessToken: () => Promise<void>;
  clearError: () => void;
  checkAuth: () => boolean;
}

export const useAuthStore = create<AuthState & AuthActions>()(
  persist(
    (set, get) => ({
      user: null,
      accessToken: null,
      refreshTokenValue: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      tokenExpiresAt: null,

      login: async (username: string, password: string) => {
        set({ isLoading: true, error: null });
        try {
          const response: AuthResponse = await authApi.login({ username, password });
          const tokenPayload = jwtDecode<JwtPayload>(response.accessToken);

          setToken(response.accessToken);
          setRefreshToken(response.refreshToken);

          set({
            user: response.usuario,
            accessToken: response.accessToken,
            refreshTokenValue: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
            tokenExpiresAt: tokenPayload.exp * 1000,
          });
        } catch (error: any) {
          set({
            error: error.response?.data?.message || 'Error de autenticación',
            isLoading: false,
            isAuthenticated: false,
          });
        }
      },

      logout: () => {
        removeToken();
        removeRefreshToken();
        set({
          user: null,
          accessToken: null,
          refreshTokenValue: null,
          isAuthenticated: false,
          tokenExpiresAt: null,
        });
      },

      refreshAccessToken: async () => {
        const storedRefresh = getRefreshToken();
        if (!storedRefresh) {
          get().logout();
          return;
        }

        set({ isLoading: true });
        try {
          const response: AuthResponse = await authApi.refresh(storedRefresh);
          const tokenPayload = jwtDecode<JwtPayload>(response.accessToken);

          setToken(response.accessToken);
          setRefreshToken(response.refreshToken);

          set({
            accessToken: response.accessToken,
            refreshTokenValue: response.refreshToken,
            tokenExpiresAt: tokenPayload.exp * 1000,
            isLoading: false,
            user: response.usuario,
          });
        } catch {
          get().logout();
        }
      },

      clearError: () => set({ error: null }),

      checkAuth: () => {
        const token = getToken();
        const expiresAt = get().tokenExpiresAt;
        if (!token || !expiresAt) return false;
        return Date.now() < expiresAt;
      },
    }),
    {
      name: 'fincore-auth-storage',
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshTokenValue: state.refreshTokenValue,
        user: state.user,
        isAuthenticated: state.isAuthenticated,
        tokenExpiresAt: state.tokenExpiresAt,
      }),
    }
  )
);

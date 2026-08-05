import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { IconFingerprint, IconLock, IconArrowRight, IconSun, IconMoon } from '@tabler/icons-react';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';

export const LoginPage = () => {
  const navigate = useNavigate();
  const { login, darkMode, toggleDarkMode } = useAuthStore();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [typedText, setTypedText] = useState('');

  useEffect(() => {
    const text = 'Sistema Bancario Enterprise...';
    let index = 0;
    const interval = setInterval(() => {
      if (index < text.length) {
        setTypedText(text.slice(0, index + 1));
        index++;
      } else {
        clearInterval(interval);
      }
    }, 50);
    return () => clearInterval(interval);
  }, []);

  const particles = Array.from({ length: 20 }, (_, i) => ({
    id: i,
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    delay: `${Math.random() * 5}s`,
    duration: `${5 + Math.random() * 5}s`,
    size: `${3 + Math.random() * 4}px`,
  }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      await login(username, password);
      const { isAuthenticated } = useAuthStore.getState();
      if (isAuthenticated) {
        navigate('/');
      } else {
        setError('Credenciales incorrectas');
      }
    } catch (err: any) {
      setError(err?.message || 'Error de conexión. Intenta nuevamente.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-950 to-slate-900 flex items-center justify-center relative overflow-hidden">
      <div className="login-particles">
        {particles.map(p => (
          <div
            key={p.id}
            className="login-particle"
            style={{
              left: p.left,
              top: p.top,
              animationDelay: p.delay,
              animationDuration: p.duration,
              width: p.size,
              height: p.size,
            }}
          />
        ))}
      </div>

      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md relative z-10"
      >
        <div className="text-center mb-8">
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ duration: 0.5, delay: 0.2 }}
            className="w-16 h-16 rounded-full bg-blue-900 text-white flex items-center justify-center mx-auto mb-4 shadow-lg"
          >
            <IconFingerprint className="w-8 h-8" />
          </motion.div>
          <h1 className="text-2xl font-bold text-slate-800 mb-2">
            FinCore Banking
          </h1>
          <p className="text-sm text-slate-500 h-6">
            {typedText}
            <span className="inline-block w-0.5 h-4 bg-blue-900 ml-0.5 animate-pulse" />
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Usuario
            </label>
            <div className="relative">
              <IconFingerprint className="absolute left-3 top-3.5 w-4 h-4 text-slate-400" />
              <input
                type="text"
                placeholder="Nombre de usuario"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full px-4 py-3 pl-10 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 placeholder-slate-400"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Contraseña
            </label>
            <div className="relative">
              <IconLock className="absolute left-3 top-3.5 w-4 h-4 text-slate-400" />
              <input
                type={showPassword ? 'text' : 'password'}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 pl-10 pr-12 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 placeholder-slate-400"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-[14px] text-slate-400 hover:text-slate-600 transition-colors"
              >
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

          {error && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-red-50 border border-red-200 text-red-700 rounded-xl px-4 py-3 text-sm"
            >
              {error}
            </motion.div>
          )}

          <motion.button
            type="submit"
            disabled={isLoading}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className="w-full py-3 px-4 rounded-xl bg-blue-900 hover:bg-blue-800 text-white font-semibold transition-all duration-200 flex items-center justify-center gap-2 shadow-lg hover:shadow-xl"
          >
            {isLoading ? (
              <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent" />
            ) : (
              <>
                <span>Iniciar Sesión</span>
                <IconArrowRight className="w-4 h-4" />
              </>
            )}
          </motion.button>
        </form>

        <div className="mt-6 flex items-center justify-center gap-2 text-xs text-slate-400">
          <IconLock className="w-3 h-3" />
          <span>Conexión segura SSL · Sesión cifrada AES-256</span>
        </div>

        <div className="mt-4 p-3 bg-blue-50 rounded-xl border border-blue-200">
          <p className="text-xs font-medium text-blue-900 mb-1">Credenciales de prueba:</p>
          <p className="text-xs text-blue-700">cliente / password123</p>
          <p className="text-xs text-blue-700">supervisor / password123</p>
          <p className="text-xs text-blue-700">auditor / password123</p>
          <p className="text-xs text-blue-700">admin / password123</p>
        </div>

        <div className="mt-4 flex items-center justify-center">
          <button
            type="button"
            onClick={toggleDarkMode}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-600 text-sm transition-colors"
          >
            {darkMode ? <IconSun className="w-4 h-4" /> : <IconMoon className="w-4 h-4" />}
            {darkMode ? 'Modo claro' : 'Modo oscuro'}
          </button>
        </div>
      </motion.div>
    </div>
  );
};

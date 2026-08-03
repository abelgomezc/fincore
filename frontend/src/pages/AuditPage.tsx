import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { useAuthStore } from '@/store/authStore';
import { auditApi } from '@/api/auditApi';
import { RegistroAuditoria } from '@/types/audit';
import { useNavigate } from 'react-router-dom';
import { formatDate } from '@/lib/utils';
import { clsx } from '@/lib/utils';

export const AuditPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const [trail, setTrail] = useState<RegistroAuditoria[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [traceIdFilter, setTraceIdFilter] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
  }, [isAuthenticated, navigate]);

  const cargarTrail = async () => {
    setIsLoading(true);
    try {
      const data = await auditApi.getTrail({ traceId: traceIdFilter || undefined });
      setTrail(data);
    } catch (error) {
      console.error('Error cargando auditoría:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      cargarTrail();
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) return null;

  return (
    <div className="min-h-screen bg-surface-950 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-2xl font-bold text-surface-100">Auditoría y Trazabilidad</h1>

              <div className="flex items-center space-x-2">
                <input
                  type="text"
                  placeholder="Filtrar por Trace ID..."
                  value={traceIdFilter}
                  onChange={(e) => setTraceIdFilter(e.target.value)}
                  className="px-3 py-2 bg-surface-800 border border-surface-600 rounded-lg text-surface-100 placeholder-surface-500"
                />
                <button
                  onClick={cargarTrail}
                  disabled={isLoading}
                  className="px-4 py-2 bg-primary-600 hover:bg-primary-700 disabled:opacity-50 rounded-lg text-white transition-colors"
                >
                  Filtrar
                </button>
              </div>
            </div>

            {isLoading ? (
              <div className="text-center py-8 text-surface-500">Cargando...</div>
            ) : trail.length === 0 ? (
              <div className="text-center py-12 text-surface-500">
                No se encontraron registros de auditoría
              </div>
            ) : (
              <div className="space-y-3">
                {trail.map((registro) => (
                  <div
                    key={registro.id}
                    className="bg-surface-800 rounded-lg p-4 border border-surface-700"
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <span className="font-medium text-surface-100">{registro.accion}</span>
                        <span className="text-sm text-surface-500 ml-2">— {registro.servicio}</span>
                      </div>
                      <span className={clsx(
                        'px-2 py-1 rounded-full text-xs font-medium',
                        registro.resultado === 'EXITOSO'
                          ? 'bg-banking-success/20 text-banking-success'
                          : 'bg-banking-error/20 text-banking-error'
                      )}>
                        {registro.resultado}
                      </span>
                    </div>
                    <div className="text-sm text-surface-400 mt-2">
                      <span className="font-mono text-xs bg-surface-900 px-2 py-1 rounded">
                        {registro.traceId}
                      </span>
                      <span className="ml-2">{formatDate(registro.fechaCreacion)}</span>
                    </div>
                    {registro.detalle && (
                      <p className="text-xs text-surface-500 mt-2">{registro.detalle}</p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};

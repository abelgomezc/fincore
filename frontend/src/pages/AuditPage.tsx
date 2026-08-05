import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { useAuthStore } from '@/store/authStore';
import { auditApi } from '@/api/auditApi';
import { RegistroAuditoria } from '@/types/audit';
import { useNavigate } from 'react-router-dom';
import { formatDate } from '@/lib/utils';
import { Card, Button, Badge } from '@/components/ui';
import {
  IconSearch,
  IconFilter,
  IconActivity,
  IconCheck,
  IconX,
  IconRefresh,
  IconCalendar,
} from '@tabler/icons-react';
import { motion } from 'framer-motion';

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
    <div className="min-h-screen bg-slate-50 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden ml-64">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-7xl mx-auto">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex items-center justify-between mb-6"
            >
              <h1 className="text-3xl font-bold text-slate-800 flex items-center">
                <IconActivity className="w-7 h-7 mr-3 text-blue-600" />
                Auditoría y Trazabilidad
              </h1>
            </motion.div>

            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
              <Card icon={<IconSearch className="w-5 h-5 text-blue-600" />} title="Filtros de búsqueda" className="mb-6">
                <div className="flex items-center space-x-3">
                  <div className="relative flex-1">
                    <IconSearch className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                    <input
                      type="text"
                      placeholder="Filtrar por Trace ID..."
                      value={traceIdFilter}
                      onChange={(e) => setTraceIdFilter(e.target.value)}
                      className="w-full pl-10 pr-3 py-2.5 border border-slate-200 rounded-xl text-slate-800 placeholder-slate-400 bg-white focus:border-blue-500 focus:outline-none"
                    />
                  </div>
                  <Button
                    variant="primary"
                    size="md"
                    icon={<IconFilter className="w-4 h-4" />}
                    onClick={cargarTrail}
                    disabled={isLoading}
                  >
                    Filtrar
                  </Button>
                  <Button
                    variant="outline"
                    size="md"
                    icon={<IconRefresh className="w-4 h-4" />}
                    onClick={() => { setTraceIdFilter(''); cargarTrail(); }}
                    disabled={isLoading}
                  >
                    Limpiar
                  </Button>
                </div>
              </Card>
            </motion.div>

            {isLoading ? (
              <div className="text-center py-12 text-slate-400">
                <IconRefresh className="w-6 h-6 animate-spin mx-auto mb-2" />
                Cargando registros de auditoría...
              </div>
            ) : trail.length === 0 ? (
              <Card className="text-center py-12">
                <IconActivity className="w-12 h-12 mx-auto mb-3 text-slate-300" />
                <p className="text-slate-500">No se encontraron registros de auditoría</p>
              </Card>
            ) : (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2 }}
                className="space-y-3"
              >
                {trail.map((registro, index) => (
                  <motion.div
                    key={registro.id}
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.1 + index * 0.03 }}
                  >
                    <Card className="p-4">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-3">
                          <div className="p-1.5 bg-slate-100 rounded-lg">
                            {registro.resultado === 'EXITOSO' ? (
                              <IconCheck className="w-5 h-5 text-green-600" />
                            ) : (
                              <IconX className="w-5 h-5 text-red-600" />
                            )}
                          </div>
                          <div>
                            <span className="font-medium text-slate-800">{registro.accion}</span>
                            <span className="text-sm text-slate-500 ml-2">— {registro.servicio}</span>
                          </div>
                        </div>
                        <Badge variant={registro.resultado === 'EXITOSO' ? 'success' : 'danger'} size="sm">
                          {registro.resultado}
                        </Badge>
                      </div>

                      <div className="mt-3 flex items-center space-x-4 text-sm text-slate-500">
                        <div className="flex items-center space-x-1">
                          <IconCalendar className="w-4 h-4" />
                          <span>{formatDate(registro.fechaCreacion)}</span>
                        </div>
                        <span className="font-mono text-xs bg-slate-100 px-2 py-1 rounded text-slate-600">
                          {registro.traceId}
                        </span>
                      </div>

                      {registro.detalle && (
                        <p className="text-xs text-slate-500 mt-2">{registro.detalle}</p>
                      )}
                    </Card>
                  </motion.div>
                ))}
              </motion.div>
            )}
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};

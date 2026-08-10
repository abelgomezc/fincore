import React, { useEffect, useState } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { TransferReviewList, FraudAlertList, ReportsPanel } from '@/components/backoffice';
import { useAuthStore } from '@/store/authStore';
import { backofficeApi } from '@/api/backofficeApi';
import { authApi } from '@/api/authApi';
import { clienteApi } from '@/api/authApi';
import { Card, Badge, Button } from '@/components/ui';
import { Transferencia } from '@/types/transfer';
import { EvaluacionFraude } from '@/types/fraud';
import { UsuarioBackoffice, Cliente } from '@/types';
import { useNavigate } from 'react-router-dom';
import {
  IconShieldCheck,
  IconAlertTriangle,
  IconReportMoney,
  IconRefresh,
  IconTrendingUp,
  IconTrendingDown,
  IconClock,
  IconUsers,
  IconUserPlus,
  IconPlayerPause,
  IconPlayerPlay,
  IconTrash,
  IconEye,
} from '@tabler/icons-react';
import { motion, AnimatePresence } from 'framer-motion';

type Tab = 'dashboard' | 'usuarios' | 'clientes';

export const BackofficePage: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthStore();
  const isAdmin = user?.roles?.some((r) => r === 'ADMIN' || r === 'SUPER_ADMIN' || r === 'AFRICANO');

  const [tab, setTab] = useState<Tab>('dashboard');
  const [transferencias, setTransferencias] = useState<Transferencia[]>([]);
  const [fraudAlerts, setFraudAlerts] = useState<EvaluacionFraude[]>([]);
  const [conciliacion, setConciliacion] = useState<Record<string, unknown> | null>(null);
  const [usuarios, setUsuarios] = useState<UsuarioBackoffice[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UsuarioBackoffice | null>(null);
  const [selectedCliente, setSelectedCliente] = useState<Cliente | null>(null);

  useEffect(() => {
    if (!isAuthenticated || !isAdmin) {
      navigate('/');
      return;
    }
    cargarDatos();
  }, [isAuthenticated, isAdmin, navigate]);

  const cargarDatos = async () => {
    setIsLoading(true);
    try {
      const [transfers, frauds, reporte, usuariosData, clientesData] = await Promise.all([
        backofficeApi.getTransferenciasEnRevision(),
        backofficeApi.getFraudAlerts(),
        backofficeApi.getReporteConciliacion(new Date().toISOString().split('T')[0]),
        authApi.listarUsuarios(),
        backofficeApi.getClientes(),
      ]);
      setTransferencias(transfers);
      setFraudAlerts(frauds);
      setConciliacion(reporte as Record<string, unknown>);
      setUsuarios(usuariosData);
      setClientes(clientesData);
    } catch (error) {
      console.error('Error cargando datos de backoffice:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isAuthenticated || !isAdmin) return null;

  const fraudAlertCount = fraudAlerts.filter((f) => f.decision === 'RECHAZADO' || f.decision === 'EN_REVISION').length;
  const transferReviewCount = transferencias.length;

  const handleCambiarEstadoUsuario = async (id: number, estado: string) => {
    try {
      await backofficeApi.cambiarEstadoUsuarioSistema(id, estado);
      setUsuarios(prev => prev.map(u => u.id === id ? { ...u, estado: estado as UsuarioBackoffice['estado'] } : u));
    } catch (error) {
      console.error('Error cambiando estado:', error);
    }
  };

  const handleCambiarEstadoCliente = async (id: number, estado: string) => {
    try {
      if (estado === 'SUSPENDIDO') {
        await clienteApi.suspenderCliente(id, 'Suspendido por administrador');
      } else if (estado === 'ACTIVO') {
        await clienteApi.reactivarCliente(id);
      } else if (estado === 'ELIMINADO') {
        await clienteApi.eliminarCliente(id);
      }
      setClientes(prev => prev.map(c => c.id === id ? { ...c, estado: estado as Cliente['estado'] } : c));
    } catch (error) {
      console.error('Error cambiando estado del cliente:', error);
    }
  };

  const estadoBadgeVariant = (estado: string): 'primary' | 'warning' | 'success' | 'danger' | 'neutral' => {
    switch (estado) {
      case 'ACTIVO': return 'success';
      case 'INACTIVO': return 'neutral';
      case 'BLOQUEADO': return 'danger';
      case 'SUSPENDIDO': return 'warning';
      case 'ELIMINADO': return 'danger';
      default: return 'neutral';
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 flex">
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
              <h1 className="text-3xl font-bold text-slate-800 dark:text-slate-100 flex items-center">
                <IconShieldCheck className="w-7 h-7 mr-3 text-blue-600 dark:text-blue-400" />
                Backoffice Administrativo
              </h1>
              <div className="flex items-center gap-2">
                <Button
                  variant={tab === 'dashboard' ? 'primary' : 'outline'}
                  size="sm"
                  onClick={() => setTab('dashboard')}
                >
                  Dashboard
                </Button>
                <Button
                  variant={tab === 'usuarios' ? 'primary' : 'outline'}
                  size="sm"
                  icon={<IconUsers className="w-4 h-4" />}
                  onClick={() => setTab('usuarios')}
                >
                  Usuarios
                </Button>
                <Button
                  variant={tab === 'clientes' ? 'primary' : 'outline'}
                  size="sm"
                  icon={<IconUserPlus className="w-4 h-4" />}
                  onClick={() => setTab('clientes')}
                >
                  Clientes
                </Button>
              </div>
            </motion.div>

            <AnimatePresence mode="wait">
              {tab === 'dashboard' && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  transition={{ duration: 0.3 }}
                >
                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.1 }}
                    className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8"
                  >
                    <Card className="text-center p-4">
                      <div className="text-3xl font-bold text-amber-600 dark:text-amber-400">{fraudAlertCount}</div>
                      <div className="text-sm text-slate-500 dark:text-slate-400 mt-1 flex items-center justify-center">
                        <IconAlertTriangle className="w-4 h-4 mr-1" />
                        Alertas de Fraude
                      </div>
                    </Card>
                    <Card className="text-center p-4">
                      <div className="text-3xl font-bold text-blue-600 dark:text-blue-400">{transferReviewCount}</div>
                      <div className="text-sm text-slate-500 dark:text-slate-400 mt-1 flex items-center justify-center">
                        <IconClock className="w-4 h-4 mr-1" />
                        Transferencias en Revisión
                      </div>
                    </Card>
                    <Card className="text-center p-4">
                      <div className="text-3xl font-bold text-green-600 dark:text-green-400">
                        {conciliacion ? '100%' : '--'}
                      </div>
                      <div className="text-sm text-slate-500 dark:text-slate-400 mt-1 flex items-center justify-center">
                        <IconTrendingUp className="w-4 h-4 mr-1" />
                        Conciliación
                      </div>
                    </Card>
                  </motion.div>

                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.2 }}
                    className="mb-8"
                  >
                    <Card title="Reporte de Conciliación" icon={<IconReportMoney className="w-5 h-5 text-blue-600 dark:text-blue-400" />}>
                      <ReportsPanel conciliacion={conciliacion ?? undefined} isLoading={isLoading} />
                    </Card>
                  </motion.div>

                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.3 }}
                    className="grid grid-cols-1 lg:grid-cols-2 gap-6"
                  >
                    <Card title="Transferencias en Revisión" icon={<IconClock className="w-5 h-5 text-blue-600 dark:text-blue-400" />}>
                      <TransferReviewList transferencias={transferencias} isLoading={isLoading} />
                    </Card>
                    <Card title="Alertas de Fraude" icon={<IconAlertTriangle className="w-5 h-5 text-amber-600 dark:text-amber-400" />}>
                      <FraudAlertList evaluaciones={fraudAlerts} isLoading={isLoading} />
                    </Card>
                  </motion.div>
                </motion.div>
              )}

              {tab === 'usuarios' && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  transition={{ duration: 0.3 }}
                >
                  <Card title="Usuarios del Sistema" icon={<IconUsers className="w-5 h-5 text-blue-600 dark:text-blue-400" />}>
                    <div className="overflow-x-auto">
                      <table className="w-full text-sm">
                        <thead>
                          <tr className="border-b border-slate-200 dark:border-slate-700">
                            <th className="text-left py-3 px-4">ID</th>
                            <th className="text-left py-3 px-4">Usuario</th>
                            <th className="text-left py-3 px-4">Email</th>
                            <th className="text-left py-3 px-4">Nombre</th>
                            <th className="text-left py-3 px-4">Estado</th>
                            <th className="text-left py-3 px-4">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {usuarios.map((u) => (
                            <tr key={u.id} className="border-b border-slate-100 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800/50">
                              <td className="py-3 px-4 font-mono text-xs">{u.id}</td>
                              <td className="py-3 px-4 font-medium">{u.username}</td>
                              <td className="py-3 px-4 text-slate-600 dark:text-slate-400">{u.email}</td>
                              <td className="py-3 px-4 text-slate-600 dark:text-slate-400">{u.nombreCompleto}</td>
                              <td className="py-3 px-4">
                                <Badge variant={estadoBadgeVariant(u.estado)} size="sm">
                                  {u.estado}
                                </Badge>
                              </td>
                              <td className="py-3 px-4">
                                <div className="flex items-center gap-2">
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    icon={<IconEye className="w-4 h-4" />}
                                    onClick={() => setSelectedUser(u)}
                                  />
                                  {u.estado === 'ACTIVO' && (
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      icon={<IconPlayerPause className="w-4 h-4 text-amber-600" />}
                                      onClick={() => handleCambiarEstadoUsuario(u.id, 'SUSPENDIDO')}
                                    />
                                  )}
                                  {(u.estado === 'SUSPENDIDO' || u.estado === 'INACTIVO') && (
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      icon={<IconPlayerPlay className="w-4 h-4 text-green-600" />}
                                      onClick={() => handleCambiarEstadoUsuario(u.id, 'ACTIVO')}
                                    />
                                  )}
                                  {u.estado !== 'ELIMINADO' && (
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      icon={<IconTrash className="w-4 h-4 text-red-600" />}
                                      onClick={() => handleCambiarEstadoUsuario(u.id, 'ELIMINADO')}
                                    />
                                  )}
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </Card>
                </motion.div>
              )}

              {tab === 'clientes' && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  transition={{ duration: 0.3 }}
                >
                  <Card title="Clientes" icon={<IconUserPlus className="w-5 h-5 text-blue-600 dark:text-blue-400" />}>
                    <div className="overflow-x-auto">
                      <table className="w-full text-sm">
                        <thead>
                          <tr className="border-b border-slate-200 dark:border-slate-700">
                            <th className="text-left py-3 px-4">ID</th>
                            <th className="text-left py-3 px-4">Nombre</th>
                            <th className="text-left py-3 px-4">Email</th>
                            <th className="text-left py-3 px-4">Tipo</th>
                            <th className="text-left py-3 px-4">Estado</th>
                            <th className="text-left py-3 px-4">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {clientes.map((c) => (
                            <tr key={c.id} className="border-b border-slate-100 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800/50">
                              <td className="py-3 px-4 font-mono text-xs">{c.id}</td>
                              <td className="py-3 px-4 font-medium">{c.nombreCompleto}</td>
                              <td className="py-3 px-4 text-slate-600 dark:text-slate-400">{c.email}</td>
                              <td className="py-3 px-4 text-slate-600 dark:text-slate-400">{c.tipoCliente}</td>
                              <td className="py-3 px-4">
                                <Badge variant={estadoBadgeVariant(c.estado)} size="sm">
                                  {c.estado}
                                </Badge>
                              </td>
                              <td className="py-3 px-4">
                                <div className="flex items-center gap-2">
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    icon={<IconEye className="w-4 h-4" />}
                                    onClick={() => setSelectedCliente(c)}
                                  />
                                  {c.estado === 'ACTIVO' && (
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      icon={<IconPlayerPause className="w-4 h-4 text-amber-600" />}
                                      onClick={() => handleCambiarEstadoCliente(c.id, 'SUSPENDIDO')}
                                    />
                                  )}
                                  {(c.estado === 'SUSPENDIDO' || c.estado === 'INACTIVO') && (
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      icon={<IconPlayerPlay className="w-4 h-4 text-green-600" />}
                                      onClick={() => handleCambiarEstadoCliente(c.id, 'ACTIVO')}
                                    />
                                  )}
                                  {c.estado !== 'ELIMINADO' && (
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      icon={<IconTrash className="w-4 h-4 text-red-600" />}
                                      onClick={() => handleCambiarEstadoCliente(c.id, 'ELIMINADO')}
                                    />
                                  )}
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </Card>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </main>
        <Footer />
      </div>

      {/* Modal de detalle de usuario */}
      <AnimatePresence>
        {selectedUser && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
            onClick={() => setSelectedUser(null)}
          >
            <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl p-6 w-full max-w-lg"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-slate-800 dark:text-slate-100">Detalle de Usuario</h3>
                <Button variant="ghost" size="sm" onClick={() => setSelectedUser(null)}>Cerrar</Button>
              </div>
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Usuario</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedUser.username}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Email</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedUser.email}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Nombre completo</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedUser.nombreCompleto}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Estado</span>
                  <Badge variant={estadoBadgeVariant(selectedUser.estado)}>{selectedUser.estado}</Badge>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Creado</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedUser.fechaCreacion}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Actualizado</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedUser.fechaActualizacion}</p>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Modal de detalle de cliente */}
      <AnimatePresence>
        {selectedCliente && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
            onClick={() => setSelectedCliente(null)}
          >
            <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl p-6 w-full max-w-lg"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-slate-800 dark:text-slate-100">Detalle de Cliente</h3>
                <Button variant="ghost" size="sm" onClick={() => setSelectedCliente(null)}>Cerrar</Button>
              </div>
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Nombre</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedCliente.nombreCompleto}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Email</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedCliente.email}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Tipo</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedCliente.tipoCliente}</p>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Estado</span>
                  <Badge variant={estadoBadgeVariant(selectedCliente.estado)}>{selectedCliente.estado}</Badge>
                </div>
                <div>
                  <span className="text-sm text-slate-500 dark:text-slate-400">Fecha de registro</span>
                  <p className="font-medium text-slate-800 dark:text-slate-100">{selectedCliente.fechaCreacion}</p>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

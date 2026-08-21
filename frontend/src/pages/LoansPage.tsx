import React, { useState, useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { loanApi } from '@/api/loanApi';
import { Card, Badge, Button } from '@/components/ui';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  IconCash,
  IconCheck,
  IconX,
  IconRefresh,
  IconPlus,
  IconEye,
} from '@tabler/icons-react';

export const LoansPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthStore();
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [selectedSolicitud, setSelectedSolicitud] = useState<any>(null);

  const [form, setForm] = useState({
    idCliente: 1,
    tipoPrestamo: 'PERSONAL',
    montoSolicitado: 10000,
    plazoMeses: 24,
    tasaInteresAnual: 18.0,
  });

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    cargarSolicitudes();
  }, [isAuthenticated, navigate]);

  const cargarSolicitudes = async () => {
    setLoading(true);
    try {
      const data = await loanApi.listarSolicitudes();
      setSolicitudes(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleCrearSolicitud = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await loanApi.crearSolicitud(form);
      setShowForm(false);
      cargarSolicitudes();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleAccion = async (id: number, accion: string) => {
    setLoading(true);
    try {
      switch (accion) {
        case 'validar':
          await loanApi.validarIdentidad(id);
          break;
        case 'buro':
          await loanApi.consultarBuro(id);
          break;
        case 'evaluar':
          await loanApi.evaluarRiesgo({ idSolicitud: id, scoreBuro: 750, scoreInterno: 80 });
          break;
        case 'aprobar':
          await loanApi.aprobarSolicitud(id, 'Aprobado');
          break;
        case 'rechazar':
          await loanApi.rechazarSolicitud(id, 'Rechazado');
          break;
        case 'contrato':
          await loanApi.generarContrato(id);
          break;
        case 'firmar':
          await loanApi.enviarContratoAFirmar(id);
          break;
        case 'desembolsar':
          await loanApi.desembolsar(id);
          break;
        default:
          break;
      }
      cargarSolicitudes();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const estadoBadge = (estado: string) => {
    switch (estado) {
      case 'ENVIADA': return 'info';
      case 'IDENTIDAD_VERIFICADA': return 'info';
      case 'BURO_CONSULTADO': return 'info';
      case 'EVALUANDO_RIESGO': return 'warning';
      case 'APROBADA': return 'success';
      case 'RECHAZADA': return 'danger';
      case 'CONTRATO_GENERADO': return 'primary';
      case 'CONTRATO_FIRMADO': return 'success';
      case 'DESEMBOLSADA': return 'success';
      default: return 'neutral';
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 flex">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden ml-64">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="flex items-center justify-between mb-6">
            <h1 className="text-2xl font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
              <IconCash className="w-6 h-6" /> Préstamos en Línea
            </h1>
            <Button icon={<IconPlus className="w-4 h-4" />} onClick={() => setShowForm(!showForm)}>
              Nueva Solicitud
            </Button>
          </div>

          {showForm && (
            <Card className="mb-6">
              <form onSubmit={handleCrearSolicitud} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tipo de préstamo</label>
                  <select className="w-full rounded-lg border border-slate-300 px-3 py-2" value={form.tipoPrestamo} onChange={(e) => setForm({ ...form, tipoPrestamo: e.target.value })}>
                    <option value="PERSONAL">Personal</option>
                    <option value="HIPOTECARIO">Hipotecario</option>
                    <option value="AUTOMOTOR">Automotor</option>
                    <option value="COMERCIAL">Comercial</option>
                    <option value="MICROCREDITO">Microcrédito</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Monto solicitado</label>
                  <input type="number" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={form.montoSolicitado} onChange={(e) => setForm({ ...form, montoSolicitado: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Plazo (meses)</label>
                  <input type="number" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={form.plazoMeses} onChange={(e) => setForm({ ...form, plazoMeses: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tasa interés anual (%)</label>
                  <input type="number" step="0.1" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={form.tasaInteresAnual} onChange={(e) => setForm({ ...form, tasaInteresAnual: Number(e.target.value) })} />
                </div>
                <div className="md:col-span-2 flex justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setShowForm(false)}>Cancelar</Button>
                  <Button type="submit" loading={loading}>Crear Solicitud</Button>
                </div>
              </form>
            </Card>
          )}

          <Card>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-200 dark:border-slate-700">
                    <th className="text-left py-3 px-4">Solicitud</th>
                    <th className="text-left py-3 px-4">Tipo</th>
                    <th className="text-left py-3 px-4">Monto</th>
                    <th className="text-left py-3 px-4">Plazo</th>
                    <th className="text-left py-3 px-4">Estado</th>
                    <th className="text-left py-3 px-4">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {loading && solicitudes.length === 0 && (
                    <tr><td colSpan={6} className="py-4 text-center text-slate-500">Cargando...</td></tr>
                  )}
                  {!loading && solicitudes.length === 0 && (
                    <tr><td colSpan={6} className="py-4 text-center text-slate-500">Sin solicitudes</td></tr>
                  )}
                  {solicitudes.map((s) => (
                    <tr key={s.id} className="border-b border-slate-100 dark:border-slate-800">
                      <td className="py-3 px-4 font-medium">{s.numeroSolicitud}</td>
                      <td className="py-3 px-4">{s.tipoPrestamo}</td>
                      <td className="py-3 px-4">${Number(s.montoSolicitado).toFixed(2)}</td>
                      <td className="py-3 px-4">{s.plazoMeses} meses</td>
                      <td className="py-3 px-4"><Badge variant={estadoBadge(s.estado) as any}>{s.estado}</Badge></td>
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-2">
                          <Button variant="ghost" size="sm" icon={<IconEye className="w-4 h-4" />} onClick={() => setSelectedSolicitud(s)} />
                          {s.estado === 'ENVIADA' && (
                            <Button variant="ghost" size="sm" icon={<IconCheck className="w-4 h-4 text-green-600" />} onClick={() => handleAccion(s.id, 'validar')} />
                          )}
                          {s.estado === 'IDENTIDAD_VERIFICADA' && (
                            <Button variant="ghost" size="sm" icon={<IconRefresh className="w-4 h-4 text-blue-600" />} onClick={() => handleAccion(s.id, 'buro')} />
                          )}
                          {s.estado === 'BURO_CONSULTADO' && (
                            <Button variant="ghost" size="sm" icon={<IconRefresh className="w-4 h-4 text-blue-600" />} onClick={() => handleAccion(s.id, 'evaluar')} />
                          )}
                          {s.estado === 'EVALUANDO_RIESGO' && (
                            <>
                              <Button variant="ghost" size="sm" icon={<IconCheck className="w-4 h-4 text-green-600" />} onClick={() => handleAccion(s.id, 'aprobar')} />
                              <Button variant="ghost" size="sm" icon={<IconX className="w-4 h-4 text-red-600" />} onClick={() => handleAccion(s.id, 'rechazar')} />
                            </>
                          )}
                          {s.estado === 'APROBADA' && (
                            <Button variant="ghost" size="sm" icon={<IconRefresh className="w-4 h-4 text-blue-600" />} onClick={() => handleAccion(s.id, 'contrato')} />
                          )}
                          {s.estado === 'CONTRATO_GENERADO' && (
                            <Button variant="ghost" size="sm" icon={<IconRefresh className="w-4 h-4 text-blue-600" />} onClick={() => handleAccion(s.id, 'firmar')} />
                          )}
                          {s.estado === 'CONTRATO_FIRMADO' && (
                            <Button variant="ghost" size="sm" icon={<IconCheck className="w-4 h-4 text-green-600" />} onClick={() => handleAccion(s.id, 'desembolsar')} />
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>

          {selectedSolicitud && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
              onClick={() => setSelectedSolicitud(null)}
            >
              <motion.div
                initial={{ scale: 0.95, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl p-6 w-full max-w-lg"
                onClick={(e) => e.stopPropagation()}
              >
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-xl font-bold text-slate-800 dark:text-slate-100">Detalle Solicitud</h3>
                  <Button variant="ghost" size="sm" onClick={() => setSelectedSolicitud(null)}>Cerrar</Button>
                </div>
                <div className="space-y-3">
                  <div><span className="text-sm text-slate-500">Número</span><p className="font-medium">{selectedSolicitud.numeroSolicitud}</p></div>
                  <div><span className="text-sm text-slate-500">Estado</span><Badge variant={estadoBadge(selectedSolicitud.estado) as any}>{selectedSolicitud.estado}</Badge></div>
                  <div><span className="text-sm text-slate-500">Monto</span><p className="font-medium">${Number(selectedSolicitud.montoSolicitado).toFixed(2)}</p></div>
                  <div><span className="text-sm text-slate-500">Plazo</span><p className="font-medium">{selectedSolicitud.plazoMeses} meses</p></div>
                  <div><span className="text-sm text-slate-500">Tasa</span><p className="font-medium">{selectedSolicitud.tasaInteresAnual}%</p></div>
                  {selectedSolicitud.motivoRechazo && (
                    <div><span className="text-sm text-slate-500">Motivo rechazo</span><p className="font-medium text-red-600">{selectedSolicitud.motivoRechazo}</p></div>
                  )}
                </div>
              </motion.div>
            </motion.div>
          )}
        </main>
        <Footer />
      </div>
    </div>
  );
};

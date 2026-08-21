import React, { useState, useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { Card, Badge, Button } from '@/components/ui';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  IconFingerprint,
  IconCheck,
  IconRefresh,
  IconEye,
} from '@tabler/icons-react';

export const KycPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const [validaciones, setValidaciones] = useState<any[]>([]);
  const [biometrias, setBiometrias] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [showValidacionForm, setShowValidacionForm] = useState(false);
  const [showBiometriaForm, setShowBiometriaForm] = useState(false);

  const [validacionForm, setValidacionForm] = useState({ idCliente: 1, tipoValidacion: 'DOCUMENTO', proveedor: 'mock-provider', detalle: '' });
  const [biometriaForm, setBiometriaForm] = useState({ idCliente: 1, tipoBiometria: 'FACIAL', proveedor: 'mock-provider', idSesionProveedor: '' });

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    cargarDatos();
  }, [isAuthenticated, navigate]);

  const cargarDatos = async () => {
    setLoading(true);
    try {
      // TODO: reemplazar por llamadas reales a customer-service cuando existan los mappers en el frontend
      // Por ahora se mantiene vacío para no usar `any` sin origen.
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleValidarIdentidad = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      // await api.validarIdentidad(validacionForm);
      setShowValidacionForm(false);
      cargarDatos();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleIniciarBiometria = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      // await api.iniciarBiometria(biometriaForm);
      setShowBiometriaForm(false);
      cargarDatos();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleFinalizarBiometria = async (id: number) => {
    setLoading(true);
    try {
      // await api.finalizarBiometria(id, 'APROBADO', 95, 'Biometría aprobada');
      cargarDatos();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
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
              <IconFingerprint className="w-6 h-6" /> KYC / Biometría
            </h1>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => { setShowValidacionForm(!showValidacionForm); setShowBiometriaForm(false); }}>Validar Identidad</Button>
              <Button onClick={() => { setShowBiometriaForm(!showBiometriaForm); setShowValidacionForm(false); }}>Iniciar Biometría</Button>
            </div>
          </div>

          {showValidacionForm && (
            <Card className="mb-6">
              <form onSubmit={handleValidarIdentidad} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">ID Cliente</label>
                  <input type="number" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={validacionForm.idCliente} onChange={(e) => setValidacionForm({ ...validacionForm, idCliente: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tipo validación</label>
                  <select className="w-full rounded-lg border border-slate-300 px-3 py-2" value={validacionForm.tipoValidacion} onChange={(e) => setValidacionForm({ ...validacionForm, tipoValidacion: e.target.value })}>
                    <option value="DOCUMENTO">Documento</option>
                    <option value="OTP">OTP</option>
                    <option value="FACIAL">Facial</option>
                    <option value="HUELLA">Huella</option>
                  </select>
                </div>
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-slate-700 mb-1">Detalle</label>
                  <input type="text" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={validacionForm.detalle} onChange={(e) => setValidacionForm({ ...validacionForm, detalle: e.target.value })} />
                </div>
                <div className="md:col-span-2 flex justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setShowValidacionForm(false)}>Cancelar</Button>
                  <Button type="submit" loading={loading}>Validar</Button>
                </div>
              </form>
            </Card>
          )}

          {showBiometriaForm && (
            <Card className="mb-6">
              <form onSubmit={handleIniciarBiometria} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">ID Cliente</label>
                  <input type="number" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={biometriaForm.idCliente} onChange={(e) => setBiometriaForm({ ...biometriaForm, idCliente: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tipo biometría</label>
                  <select className="w-full rounded-lg border border-slate-300 px-3 py-2" value={biometriaForm.tipoBiometria} onChange={(e) => setBiometriaForm({ ...biometriaForm, tipoBiometria: e.target.value })}>
                    <option value="FACIAL">Facial</option>
                    <option value="HUELLA">Huella dactilar</option>
                    <option value="VOZ">Voz</option>
                  </select>
                </div>
                <div className="md:col-span-2 flex justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setShowBiometriaForm(false)}>Cancelar</Button>
                  <Button type="submit" loading={loading}>Iniciar</Button>
                </div>
              </form>
            </Card>
          )}

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <h3 className="text-lg font-semibold mb-4">Validaciones de Identidad</h3>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-slate-200 dark:border-slate-700">
                      <th className="text-left py-2 px-3">ID</th>
                      <th className="text-left py-2 px-3">Tipo</th>
                      <th className="text-left py-2 px-3">Estado</th>
                      <th className="text-left py-2 px-3">Proveedor</th>
                      <th className="text-left py-2 px-3">Fecha</th>
                    </tr>
                  </thead>
                  <tbody>
                    {validaciones.length === 0 && (
                      <tr><td colSpan={5} className="py-4 text-center text-slate-500">Sin registros</td></tr>
                    )}
                    {validaciones.map((v) => (
                      <tr key={v.id} className="border-b border-slate-100 dark:border-slate-800">
                        <td className="py-2 px-3">{v.id}</td>
                        <td className="py-2 px-3">{v.tipoValidacion}</td>
                        <td className="py-2 px-3"><Badge variant={v.estado === 'APROBADO' ? 'success' : 'warning'} size="sm">{v.estado}</Badge></td>
                        <td className="py-2 px-3">{v.proveedor}</td>
                        <td className="py-2 px-3">{v.fechaValidacion ? new Date(v.fechaValidacion).toLocaleString() : '--'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </Card>

            <Card>
              <h3 className="text-lg font-semibold mb-4">Sesiones Biométricas</h3>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-slate-200 dark:border-slate-700">
                      <th className="text-left py-2 px-3">ID</th>
                      <th className="text-left py-2 px-3">Tipo</th>
                      <th className="text-left py-2 px-3">Estado</th>
                      <th className="text-left py-2 px-3">Proveedor</th>
                      <th className="text-left py-2 px-3">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {biometrias.length === 0 && (
                      <tr><td colSpan={5} className="py-4 text-center text-slate-500">Sin registros</td></tr>
                    )}
                    {biometrias.map((b) => (
                      <tr key={b.id} className="border-b border-slate-100 dark:border-slate-800">
                        <td className="py-2 px-3">{b.id}</td>
                        <td className="py-2 px-3">{b.tipoBiometria}</td>
                        <td className="py-2 px-3"><Badge variant={b.estado === 'APROBADO' ? 'success' : b.estado === 'RECHAZADO' ? 'danger' : 'warning'} size="sm">{b.estado}</Badge></td>
                        <td className="py-2 px-3">{b.proveedor}</td>
                        <td className="py-2 px-3">
                          {b.estado === 'INICIADA' && (
                            <Button variant="ghost" size="sm" icon={<IconCheck className="w-4 h-4 text-green-600" />} onClick={() => handleFinalizarBiometria(b.id)} />
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </Card>
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
};

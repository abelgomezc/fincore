import React, { useState, useEffect } from 'react';
import { Header, Sidebar, Footer } from '@/components/layout';
import { documentApi } from '@/api/documentApi';
import { Card, Badge, Button } from '@/components/ui';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  IconFileDescription,
  IconPlus,
  IconEye,
  IconSend,
  IconCheck,
} from '@tabler/icons-react';

export const DocumentsPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const [plantillas, setPlantillas] = useState<any[]>([]);
  const [documentos, setDocumentos] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [showPlantillaForm, setShowPlantillaForm] = useState(false);
  const [showDocumentoForm, setShowDocumentoForm] = useState(false);
  const [selectedDoc, setSelectedDoc] = useState<any>(null);

  const [plantillaForm, setPlantillaForm] = useState({ tipoDocumento: 'CONTRATO_PRESTAMO', nombre: '', descripcion: '', contenidoHtml: '<p>Contrato</p>' });
  const [documentoForm, setDocumentoForm] = useState({ idPlantilla: 1, tipoDocumento: 'CONTRATO_PRESTAMO', entidad: 'prestamo', idEntidad: '1', nombreArchivo: 'contrato.pdf' });

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    cargarDatos();
  }, [isAuthenticated, navigate]);

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [plantillasData, docsData] = await Promise.all([
        documentApi.listarPlantillas(),
        documentApi.consultarDocumentos('prestamo', '1'),
      ]);
      setPlantillas(plantillasData);
      setDocumentos(docsData);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleCrearPlantilla = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await documentApi.crearPlantilla(plantillaForm);
      setShowPlantillaForm(false);
      cargarDatos();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerarDocumento = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await documentApi.generarDocumento(documentoForm);
      setShowDocumentoForm(false);
      cargarDatos();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleEnviarAFirmar = async (id: number) => {
    setLoading(true);
    try {
      await documentApi.enviarAFirmar({ idDocumento: id, idFirmante: 'cliente1', nombreFirmante: 'Cliente Demo' });
      cargarDatos();
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const estadoBadge = (estado: string) => {
    switch (estado) {
      case 'BORRADOR': return 'neutral';
      case 'GENERADO': return 'info';
      case 'ENVIADO': return 'warning';
      case 'FIRMADO': return 'success';
      case 'RECHAZADO': return 'danger';
      case 'VENCIDO': return 'danger';
      case 'CANCELADO': return 'neutral';
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
              <IconFileDescription className="w-6 h-6" /> Documentos
            </h1>
            <div className="flex gap-2">
              <Button variant="outline" icon={<IconPlus className="w-4 h-4" />} onClick={() => { setShowPlantillaForm(!showPlantillaForm); setShowDocumentoForm(false); }}>Nueva Plantilla</Button>
              <Button icon={<IconPlus className="w-4 h-4" />} onClick={() => { setShowDocumentoForm(!showDocumentoForm); setShowPlantillaForm(false); }}>Generar Documento</Button>
            </div>
          </div>

          {showPlantillaForm && (
            <Card className="mb-6">
              <form onSubmit={handleCrearPlantilla} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tipo</label>
                  <select className="w-full rounded-lg border border-slate-300 px-3 py-2" value={plantillaForm.tipoDocumento} onChange={(e) => setPlantillaForm({ ...plantillaForm, tipoDocumento: e.target.value })}>
                    <option value="CONTRATO_PRESTAMO">Contrato Préstamo</option>
                    <option value="PAGARE">Pagaré</option>
                    <option value="DECLARACION_ORIGEN_FONDOS">Declaración Origen Fondos</option>
                    <option value="AUTORIZACION_DEBITO">Autorización Débito</option>
                    <option value="CONTRATO_SEGURO">Contrato Seguro</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
                  <input type="text" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={plantillaForm.nombre} onChange={(e) => setPlantillaForm({ ...plantillaForm, nombre: e.target.value })} required />
                </div>
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-slate-700 mb-1">Contenido HTML</label>
                  <textarea className="w-full rounded-lg border border-slate-300 px-3 py-2" rows={4} value={plantillaForm.contenidoHtml} onChange={(e) => setPlantillaForm({ ...plantillaForm, contenidoHtml: e.target.value })} required />
                </div>
                <div className="md:col-span-2 flex justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setShowPlantillaForm(false)}>Cancelar</Button>
                  <Button type="submit" loading={loading}>Crear Plantilla</Button>
                </div>
              </form>
            </Card>
          )}

          {showDocumentoForm && (
            <Card className="mb-6">
              <form onSubmit={handleGenerarDocumento} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Plantilla</label>
                  <select className="w-full rounded-lg border border-slate-300 px-3 py-2" value={documentoForm.idPlantilla} onChange={(e) => setDocumentoForm({ ...documentoForm, idPlantilla: Number(e.target.value) })}>
                    {plantillas.map((p) => (
                      <option key={p.id} value={p.id}>{p.nombre}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tipo</label>
                  <select className="w-full rounded-lg border border-slate-300 px-3 py-2" value={documentoForm.tipoDocumento} onChange={(e) => setDocumentoForm({ ...documentoForm, tipoDocumento: e.target.value })}>
                    <option value="CONTRATO_PRESTAMO">Contrato Préstamo</option>
                    <option value="PAGARE">Pagaré</option>
                    <option value="DECLARACION_ORIGEN_FONDOS">Declaración Origen Fondos</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Entidad</label>
                  <input type="text" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={documentoForm.entidad} onChange={(e) => setDocumentoForm({ ...documentoForm, entidad: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">ID Entidad</label>
                  <input type="text" className="w-full rounded-lg border border-slate-300 px-3 py-2" value={documentoForm.idEntidad} onChange={(e) => setDocumentoForm({ ...documentoForm, idEntidad: e.target.value })} />
                </div>
                <div className="md:col-span-2 flex justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setShowDocumentoForm(false)}>Cancelar</Button>
                  <Button type="submit" loading={loading}>Generar Documento</Button>
                </div>
              </form>
            </Card>
          )}

          <Card>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-200 dark:border-slate-700">
                    <th className="text-left py-3 px-4">ID</th>
                    <th className="text-left py-3 px-4">Tipo</th>
                    <th className="text-left py-3 px-4">Nombre</th>
                    <th className="text-left py-3 px-4">Estado</th>
                    <th className="text-left py-3 px-4">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {loading && documentos.length === 0 && (
                    <tr><td colSpan={5} className="py-4 text-center text-slate-500">Cargando...</td></tr>
                  )}
                  {!loading && documentos.length === 0 && (
                    <tr><td colSpan={5} className="py-4 text-center text-slate-500">Sin documentos</td></tr>
                  )}
                  {documentos.map((d) => (
                    <tr key={d.id} className="border-b border-slate-100 dark:border-slate-800">
                      <td className="py-3 px-4">{d.id}</td>
                      <td className="py-3 px-4">{d.tipoDocumento}</td>
                      <td className="py-3 px-4">{d.nombreArchivo}</td>
                      <td className="py-3 px-4"><Badge variant={estadoBadge(d.estado) as any}>{d.estado}</Badge></td>
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-2">
                          <Button variant="ghost" size="sm" icon={<IconEye className="w-4 h-4" />} onClick={() => setSelectedDoc(d)} />
                          {d.estado === 'GENERADO' && (
                            <Button variant="ghost" size="sm" icon={<IconSend className="w-4 h-4 text-blue-600" />} onClick={() => handleEnviarAFirmar(d.id)} />
                          )}
                          {d.estado === 'ENVIADO' && (
                            <Button variant="ghost" size="sm" icon={<IconCheck className="w-4 h-4 text-green-600" />} onClick={() => documentApi.finalizarFirma(1, 'FIRMADO').then(cargarDatos)} />
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>

          {selectedDoc && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
              onClick={() => setSelectedDoc(null)}
            >
              <motion.div
                initial={{ scale: 0.95, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl p-6 w-full max-w-lg"
                onClick={(e) => e.stopPropagation()}
              >
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-xl font-bold text-slate-800 dark:text-slate-100">Detalle Documento</h3>
                  <Button variant="ghost" size="sm" onClick={() => setSelectedDoc(null)}>Cerrar</Button>
                </div>
                <div className="space-y-3">
                  <div><span className="text-sm text-slate-500">Nombre</span><p className="font-medium">{selectedDoc.nombreArchivo}</p></div>
                  <div><span className="text-sm text-slate-500">Tipo</span><p className="font-medium">{selectedDoc.tipoDocumento}</p></div>
                  <div><span className="text-sm text-slate-500">Estado</span><Badge variant={estadoBadge(selectedDoc.estado) as any}>{selectedDoc.estado}</Badge></div>
                  <div><span className="text-sm text-slate-500">URL</span><p className="font-medium break-all">{selectedDoc.urlArchivo}</p></div>
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

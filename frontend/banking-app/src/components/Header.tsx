/**
 * Encabezado superior de la aplicación bancaria
 * Muestra el nombre de la aplicación y elementos de navegación del usuario
 */
function Header() {
  return (
    <header className="bg-white border-b border-slate-200 h-16 flex items-center justify-between px-6 shadow-sm">
      <div className="flex items-center gap-3">
        <div className="w-8 h-8 bg-banking-600 rounded-lg flex items-center justify-center">
          <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h1 className="text-xl font-bold text-banking-900 tracking-tight">
          FinCore Banking
        </h1>
      </div>

      <div className="flex items-center gap-4">
        <span className="text-sm text-slate-500">Demo Tiempo Real</span>
        <div className="w-2 h-2 bg-emerald-400 rounded-full animate-pulse" />
        <button className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-banking-700 hover:bg-banking-50 rounded-lg transition-colors">
          Cerrar Sesión
        </button>
      </div>
    </header>
  )
}

export default Header

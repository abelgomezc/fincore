# FinCore Frontend

Aplicación web para FinCore Banking — © 2026 Abel Gomez

## Stack

- **React 18** + **Vite 5**
- **TypeScript 5** — Tipado fuerte
- **Tailwind CSS 3** — Estilos
- **TanStack Query v5** — Server state
- **Zustand** — Client state
- **Recharts** — Gráficas financieras
- **React Router v6** — Navegación
- **Axios** — HTTP (timeout 30s)
- **Framer Motion** — Animaciones
- **WebSocket** — Demo transferencia en tiempo real
- **react-hot-toast** — Notificaciones toast
- **@tabler/icons-react** — Iconografía
- **@tremor/react** — Componentes de dashboard

## Características

- ✅ **Diseño bancario profesional** con Tailwind CSS
- ✅ **Animaciones fluidas** con Framer Motion
- ✅ **Gráficos interactivos** con Recharts y Tremor
- ✅ **Iconografía profesional** con Tabler Icons
- ✅ **Modo claro/oscuro** con persistencia en `sessionStorage`
- ✅ **Notificaciones toast** con react-hot-toast
- ✅ **Responsive design** para móvil y desktop
- ✅ **Loading skeletons** en todos los estados de carga
- ✅ **Tablas expandibles** con animaciones
- ✅ **Cuenta seleccionada persistente** en `sessionStorage` durante la sesión
- ✅ **Selector de cuenta** en sidebar para cambiar la cuenta activa
- ✅ **Origen de transferencia automático** desde la cuenta seleccionada (solo lectura)
- ✅ **Preview de beneficiario** al escribir número de cuenta destino y presionar `Enter`

## Scripts

```bash
npm install
npm run dev
npm run build
npm run preview
```

## Variables de entorno

```bash
VITE_API_URL=http://localhost:8080
```

## Estructura

```
frontend/
├── src/
│   ├── api/          # Clientes HTTP (accountApi, transferApi, etc.)
│   ├── components/   # Componentes reutilizables
│   ├── pages/        # Páginas/rutas
│   ├── store/        # Estado global (Zustand)
│   ├── hooks/        # Custom hooks
│   ├── types/        # Tipos TypeScript
│   ├── utils/        # Utilidades
│   └── main.tsx      # Entry point
├── public/
└── package.json
```

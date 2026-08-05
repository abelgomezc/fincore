import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#1B4F8A',
          50: '#e6f0fa',
          100: '#cce0f5',
          500: '#1B4F8A',
          600: '#163f70',
          700: '#112f54',
          800: '#0d2338',
        },
        success: {
          DEFAULT: '#2ECC71',
          50: '#e8f8f0',
          100: '#d1f0e0',
          200: '#a3e0c0',
          500: '#2ECC71',
          600: '#25a55b',
          700: '#1d7e44',
        },
        danger: {
          DEFAULT: '#E74C3C',
          50: '#fdecec',
          100: '#fbd9d8',
          200: '#f7b3b2',
          500: '#E74C3C',
          600: '#c9302c',
          700: '#a01e1e',
        },
        warning: {
          DEFAULT: '#F39C12',
          50: '#fef4e5',
          100: '#fde9cc',
          200: '#fcd399',
          500: '#F39C12',
          600: '#cc8400',
          700: '#995f00',
        },
        dark: {
          DEFAULT: '#2C3E50',
          500: '#2C3E50',
          600: '#233443',
          700: '#1a2b37',
        },
        surface: {
          DEFAULT: '#F8F9FA',
          50: '#F8F9FA',
          100: '#f8f9fa',
          200: '#e9ecea',
          300: '#dee2e6',
          400: '#ced4da',
          500: '#6c757d',
          600: '#495057',
        },
        card: {
          DEFAULT: '#FFFFFF',
          50: '#FFFFFF',
          100: '#fdfdfd',
        },
      },
    },
  },
  plugins: [],
} satisfies Config;

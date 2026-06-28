import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const rootDir = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  root: rootDir,
  cacheDir: path.resolve(rootDir, 'node_modules/.vite'),
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    // Evita abrir el navegador en cada arranque (ahorra unos segundos).
    open: false,
    fs: {
      strict: true,
      allow: [rootDir],
    },
    watch: {
      ignored: ['**/.git/**', '**/dist/**'],
    },
    warmup: {
      clientFiles: ['./src/main.tsx', './src/App.tsx'],
    },
  },
  optimizeDeps: {
    holdUntilCrawlEnd: false,
    include: [
      'react',
      'react/jsx-dev-runtime',
      'react-dom',
      'react-dom/client',
      'react-router-dom',
      'firebase/app',
      'firebase/auth',
    ],
  },
});

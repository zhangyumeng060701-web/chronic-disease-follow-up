import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/__tests__/setup.js'],
    clearMocks: true,
    restoreMocks: true,
    pool: 'threads',
    maxWorkers: 1,
    fileParallelism: false
  },
  resolve: {
    alias: { '@': path.resolve(import.meta.dirname, 'src') }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})

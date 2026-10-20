import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    // Same origin for the browser, so the backend needs no CORS configuration.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});

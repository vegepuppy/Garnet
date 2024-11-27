import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,//前端的地址
    proxy: {
      "/api": {
        target: "http://127.0.0.1:3001",//这个是后端的地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, "/api"),
      },
    }
  }
})

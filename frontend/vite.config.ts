// frontend/vite.config.ts
import {defineConfig} from 'vite'

export default defineConfig({
    build: {
        outDir: '../src/main/resources/static',
        emptyOutDir: true,
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8080',
            '/ws': {
                target: 'ws://localhost:8080',
                ws: true,
            }
        },

        // allowedHosts: [],
    }
})
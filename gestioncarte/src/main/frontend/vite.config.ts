/// <reference types="vitest" />
import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import * as path from "path";
import {viteStaticCopy} from 'vite-plugin-static-copy'
import {NodeGlobalsPolyfillPlugin} from '@esbuild-plugins/node-globals-polyfill'

const target = path.resolve(__dirname, '../../../target/');
const outDir = path.resolve(target, 'generated-resources/frontend/static');
const coverageDir = path.resolve(target, 'vitest-coverage');

const getVersion = (mode, env) => {
    if (mode === 'production') {
        return '"' + (env.VITE_APP_VERSION || env.npm_package_version) + '"';
    } else {
        const tzOffset = (new Date()).getTimezoneOffset() * 60000;

        return '"' + new Date(Date.now() - tzOffset).toISOString().replace(/T/, ' ').replace(/\..+/, '') + ' - ' + mode + '"';
    }
};

export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), '');

    return {
        plugins: [
            vue(),
            viteStaticCopy({
                targets: [
                    { src: 'node_modules/ionicons/dist/svg/*.svg', dest: path.resolve(outDir, 'svg/ionicons/') }
                ]
            })
        ],
        resolve: {
            alias: {
                '@components': path.resolve(__dirname, './src/vue/components/'),
                '@tr': path.resolve(__dirname, './tests/resources/'),
                '@tu': path.resolve(__dirname, './tests/unit/'),
                '@pages': path.resolve(__dirname, './src/vue/pages/'),
                'src': path.resolve(__dirname, './src/'),
                '@': path.resolve(__dirname, './src/'),
            },
            extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
        },
        optimizeDeps: {
            esbuildOptions: {
                define: {
                    global: 'globalThis'
                },
                plugins: [
                    NodeGlobalsPolyfillPlugin({
                        buffer: true
                    })
                ]
            }
        },
        define: {
            'process.env.VITE_APP_VERSION': getVersion(mode, env),
        },
        build: {
            manifest: true,
            outDir: outDir,
            sourcemap: true,
            rollupOptions: {
                input: {
                    app: './src/vue/app.ts',
                },
            }
        },
        server: {
            port: 3000,
            strictPort: true,
            watch: {
                usePolling: true
            },
        },
        test: {
            environment: "happy-dom",
            reporters: 'vitest-sonar-reporter',
            outputFile: path.resolve(coverageDir, 'sonar-report.xml'),
            coverage: {
                provider: 'v8',
                reporter: ['lcov'],
                reportsDirectory: coverageDir,
                all: true,
            },
        },
    }
});

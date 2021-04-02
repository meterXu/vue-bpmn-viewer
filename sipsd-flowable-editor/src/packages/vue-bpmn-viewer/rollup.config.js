import pkg from './package.json';
import cjs from 'rollup-plugin-commonjs';
import vue from 'rollup-plugin-vue'
import { terser } from 'rollup-plugin-terser'
import svg from 'rollup-plugin-svg'
function pgl() {
    return [
        cjs(),
        vue({
            target: 'browser',
            css: true,
            compileTemplate: true
        }),
        terser(),
        svg()
    ];
}

export default [
    {
        input: './index.js',
        output: {
            name: 'vue-bpmn-viewer',
            file: `dist/vue-bpmn-viewer.umd.js`,
            format: 'umd'
        },
        plugins: pgl()
    },
    {
        input: './index.js',
        output: [
            { file: pkg.main, format: 'cjs' },
            { file: pkg.module, format: 'es' }
        ],
        plugins: pgl()
    }
];

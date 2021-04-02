import pkg from './package.json';
import cjs from 'rollup-plugin-commonjs';
import vue from 'rollup-plugin-vue'
import { terser } from 'rollup-plugin-terser'
import postcss from 'rollup-plugin-postcss'
import cssnano from 'cssnano'
function pgl() {
    return [
        cjs(),
        vue({
            target: 'browser',
            css: true,
            compileTemplate: true
        }),
        terser(),
        postcss({
            plugins: [
                cssnano(),
            ],
            extensions: [ '.css' ],
        })
    ];
}

export default [
    {
        input: './index.js',
        output: {
            name: 'vue-bpmn-controls',
            file: `dist/vue-bpmn-controls.umd.js`,
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

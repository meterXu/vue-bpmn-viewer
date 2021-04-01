import path from 'path'
import pkg from './package.json';
import cjs from 'rollup-plugin-commonjs';
import vue from 'rollup-plugin-vue'
import { terser } from 'rollup-plugin-terser'
import postcss from 'rollup-plugin-postcss'
import cssnano from 'cssnano'
import copy from 'rollup-plugin-copy';
const url = require('postcss-url');
function pgl() {
    return [
        cjs(),
        vue({
            css: true,
            compileTemplate: true
        }),
        terser(),
        postcss({
            plugins: [
                cssnano(),
                url({
                    url: "copy",
                    basePath: path.resolve('src/assets/font'),
                    assetsPath: 'font',
                    useHash: true
                }),
            ],
            extensions: [ '.css' ],
        }),
        copy({
            targets:[
                {src:'./src/assets/css/font/*',dest:'dist/font'}
            ]
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

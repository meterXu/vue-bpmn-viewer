import pkg from './package.json';
import cjs from 'rollup-plugin-commonjs';
import vue from 'rollup-plugin-vue'
import { terser } from 'rollup-plugin-terser'
function pgl() {
    return [
        cjs(),
        vue({
            css: true,
            // 把组件转换成 render 函数
            compileTemplate: true
        }),
        terser()
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

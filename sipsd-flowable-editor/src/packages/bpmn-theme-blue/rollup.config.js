import pkg from './package.json';
import css from 'rollup-plugin-css-only'
import cjs from 'rollup-plugin-commonjs';

function pgl() {
    return [
        cjs(),
        css({output: 'bundle.css'})
    ];
}

export default [
    {
        input: './index.js',
        output: {
            name: 'bpmn-theme-blue',
            file: `dist/bpmn-theme-blue.umd.js`,
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

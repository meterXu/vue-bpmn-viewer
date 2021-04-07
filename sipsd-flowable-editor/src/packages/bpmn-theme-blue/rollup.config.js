import pkg from './package.json';
import postcss from 'rollup-plugin-postcss'
import cjs from 'rollup-plugin-commonjs';
import cssnano from 'cssnano'

function pgl() {
    return [
        cjs(),
        postcss({
            plugins: [
                cssnano(),
            ],
            extensions: [ '.css' ],
        }),
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

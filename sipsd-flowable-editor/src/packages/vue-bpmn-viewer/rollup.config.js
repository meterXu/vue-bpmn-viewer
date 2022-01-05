import pkg from './package.json';
import cjs from 'rollup-plugin-commonjs';
import url from 'rollup-plugin-url'
import { terser } from 'rollup-plugin-terser'
import vue from "rollup-plugin-vue";
import cssnano from 'cssnano';
import postcss from 'rollup-plugin-postcss'
import postcssUrl from 'postcss-url'
function pgl() {
    return [
        cjs(),
        vue({
            target: 'browser',
            css: true,
            compileTemplate: true
        }),
        url({
            include: ['**/*.svg', '**/*.png', '**/*.jpg', '**/*.gif', '**/*.woff', '**/*.woff2'],
            limit: Infinity,
            publicPath: '/assets',
        }),
        postcss({
            plugins: [
                cssnano(),
                postcssUrl({
                    url: "inline",
                    maxSize: 100,
                    fallback: "copy"
                })
            ],
            extensions: [ '.css' ],
        }),
        terser()
    ];
}

export default [
    {
        input: './index.js',
        output: {
            name: 'VueBpmnViewer',
            file: `dist/vue-bpmn-viewer.umd.js`,
            format: 'umd'
        },
        plugins: pgl()
    },
    {
        input: './index.js',
        output: [
            { file: pkg.main, format: 'cjs' },
            { file: "dist/index.esm.js", format: 'es' }
        ],
        plugins: pgl()
    }
];

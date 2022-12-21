import cjs from 'rollup-plugin-commonjs';
import url from 'rollup-plugin-url'
import { terser } from 'rollup-plugin-terser'
import vue from "rollup-plugin-vue";
import cssnano from 'cssnano';
import postcss from 'rollup-plugin-postcss'
import postcssUrl from 'postcss-url'
export default [
    {
        input: './index.js',
        output: [
            { file: `dist/bpmn-viewer-vue.umd.js`, format: 'umd',name: 'BpmnViewerVue'},
            { file: "dist/index.js", format: 'cjs' },
            { file: "dist/index.esm.js", format: 'es' }
        ],
        plugins: [
            cjs(),
            vue({
                target: 'browser',
                css: true,
                compileTemplate: true
            }),
            url({
                include: ['**/*.svg', '**/*.png', '**/*.jpg', '**/*.gif', '**/*.woff', '**/*.woff2'],
                limit: Infinity,
                publicPath: '/icons',
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
        ]
    }
];

const CopyPlugin  = require('copy-webpack-plugin');
module.exports = {
  publicPath: './',
  lintOnSave: undefined,
  productionSourceMap: false,
  css: {
    extract: false
  },
  configureWebpack:  config => {
    return {
      optimization: {
        runtimeChunk: false, // 依赖处理与bundle合并
        splitChunks: {
          cacheGroups: {
            default: false
          }
        }
      },
      plugins:[
        new CopyPlugin({patterns:
          [{
            from: './src/project.js', to: `/js/project.[contenthash:4].js`,
            transform: (res, p) => {
              res = res.toString().replace(/export default project_.*/, '')
              return res
            }
          }]
        })
      ]
    }
  },
  devServer: {
    port: 8081
  }
}

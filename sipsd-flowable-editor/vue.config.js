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
  chainWebpack: config => {
    const imgRule = config.module.rule('images')
    imgRule.use('url-loader')
        .tap(options => {
          options.name = `/img/[name].[hash:8].[ext]`
          options.fallback = {
            loader: 'file-loader',
            options: {
              name: `/img/[name].[hash:8].[ext]`
            }
          }
          return options
        })
    const svgRule = config.module.rule('svg')
    svgRule
        .use('file-loader')
        .tap(options => {
          options.name = `/img/[name].[hash:8].[ext]`
          options.fallback = {
            loader: 'file-loader',
            options: {
              name: `/img/[name].[hash:8].[ext]`
            }
          }
          return options
        })
    const fontRule = config.module.rule('fonts')
    fontRule
        .use('url-loader')
        .tap(options => {
          options.name = `/fonts/[name].[hash:8].[ext]`
          options.fallback = {
            loader: 'file-loader',
            options: {
              name: `/fonts/[name].[hash:8].[ext]`
            }
          }
          return options
        })
    const mediaRule = config.module.rule('media')
    mediaRule
        .use('url-loader')
        .tap(options => {
          options.name = `/media/[name].[hash:8].[ext]`
          options.fallback = {
            loader: 'file-loader',
            options: {
              name: `/media/[name].[hash:8].[ext]`
            }
          }
          return options
        })
  },
  devServer: {
    port: 8081
  }
}

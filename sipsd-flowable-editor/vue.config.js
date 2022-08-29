const CopyPlugin  = require('copy-webpack-plugin');
module.exports = {
  publicPath: './',
  lintOnSave: undefined,
  productionSourceMap: false,
  css: {
    extract: false
  },
  configureWebpack:  config => {
    config.entry = {
      project:["./src/project.js"],
      main: ["./src/main.js"]
    }
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
            from: './src/project.js', to: `./js/project.[contenthash:4].js`,
            transform:(res,p)=>{
              res = res.toString().replace(`export default project`,'')
              let regex = new RegExp('(?<=process.env\\.)\\w*','gi')
              let ms = res.match(regex)
              ms.forEach(m=>{
                res = res.replaceAll(`process.env.${m}`,`"${process.env[m]}"`)
              })
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

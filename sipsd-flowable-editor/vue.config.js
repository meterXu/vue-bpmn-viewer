const namespace = "bpmn"
const CopyPlugin  = require('copy-webpack-plugin');
module.exports = {
  lintOnSave: undefined,
  productionSourceMap: false,
  css: {
    extract: false
  },
  configureWebpack:  config => {
    config.entry = {
      [namespace]: ["./src/main.js"]
    }
    return {
      output: {
        library: namespace,
        filename: process.env.NODE_ENV === 'production'
          ? `biz/${namespace}/js/[id].[contenthash:4].js`
          : '[id].js' ,
        chunkFilename: `biz/${namespace}/js/${namespace}.vendors.[contenthash:4].js`,
      },
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
            from: './src/project.js', to: `biz/${namespace}/js/project.[contenthash:4].js`,
            transform:(res,p)=>{
              res = res.toString().replace('export default project','')
              const matchs = res.match(/require\(.*\)/g)
              matchs.forEach(c => {
                const fullpath = c.match(/(?<=assets\/).*(?=")/g)[0]
                res = res.replace(c,`"./biz/${namespace}/img/${fullpath}"`)
              });
              return res
            }
          }]
        })
      ],
      module:{
        rules:[
          // {
          //   test: /\.(ttf|otf|eot|woff|woff2)$/,
          //   loader:'url-loader',
          //   options:{
          //     limit: 10000,
          //     name: 'fonts/[name].[contenthash:4].[ext]'
          //   }
          // }
        ]
      }
    }
  },
  chainWebpack:config=>{
    config.module
      .rule("images")
      .use("url-loader")
      .tap(options => {
        options.name = `biz/${namespace}/img/[name].[ext]`;
        options.fallback = {
          loader: "file-loader",
          options: {
            name: `biz/${namespace}/img/[name].[ext]`
          }
        };
        return options;
      })
  },
  devServer: {
    port: 8081
  }
}

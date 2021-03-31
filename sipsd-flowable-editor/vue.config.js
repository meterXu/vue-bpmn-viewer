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
      ]
    }
  },
  chainWebpack:config=>{
    const svgRule = config.module.rule('svg');
    svgRule.uses.clear();
    config.module
      .rule("images")
        .test(/\.(png|jpe?g|gif|webp|svg)(\?.*)?$/)
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

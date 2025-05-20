const { defineConfig } = require('@vue/cli-service');
const webpack = require('webpack');

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: false,
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        __VUE_OPTIONS_API__: JSON.stringify(true),
        __VUE_PROD_DEVTOOLS__: JSON.stringify(false),
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false)
      })
    ]
  },
  devServer: {
    proxy: {
      '/api': {
        target: process.env.VUE_APP_BACKEND_URL || 'http://localhost:8080',  // Your backend server
        changeOrigin: true,               // Needed for virtual hosted sites
        // pathRewrite: {
        //   '^/api': '',                    // Rewrite '/api' to be forwarded to the backend
        // },
      },
    },
  },
});

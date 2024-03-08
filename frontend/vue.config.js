/*
 * Copyright (c) 2023-2024 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const talkforgeaiServerPort = process.env.TALKFORGEAI_SERVER_PORT || 8090;

const {defineConfig} = require('@vue/cli-service');
const {VuetifyPlugin} = require('webpack-plugin-vuetify');
module.exports = defineConfig({
  runtimeCompiler: true,
  transpileDependencies: true,
  configureWebpack: {
    plugins: [
      new VuetifyPlugin({autoImport: true}),
    ],
  },
  chainWebpack: config => {
    config.module.rule('vue').use('vue-loader').tap(options => ({
      ...options,
      compilerOptions: {
        // treat any tag that starts with tf- as custom elements
        isCustomElement: tag => tag.startsWith('tf-'),
      },
    }));
  },
  // proxy all webpack dev-server requests starting with /api
  // to our Spring Boot backend (localhost:8098) using http-proxy-middleware
  // see https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:' + talkforgeaiServerPort, // this configuration needs to correspond to the Spring Boot backends' application.properties server.port
        changeOrigin: true,
        ws: true, // websockets
        compress: false, // deactivates compression, which is enabled by default. Needed for SSE.
      },
    },
  },
  // Change build paths to make them Maven compatible
  // see https://cli.vuejs.org/config/
  outputDir: 'target/dist',
  assetsDir: 'static',
});

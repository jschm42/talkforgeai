// @ts-ignore
import type IForkTsCheckerWebpackPlugin from 'fork-ts-checker-webpack-plugin';
import {VueLoaderPlugin} from 'vue-loader';
import CopyPlugin = require('copy-webpack-plugin');

// eslint-disable-next-line @typescript-eslint/no-var-requires
const ForkTsCheckerWebpackPlugin: typeof IForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');

export const plugins = [
  new ForkTsCheckerWebpackPlugin({
    logger: 'webpack-infrastructure',
  }),
  new VueLoaderPlugin(),
  new CopyPlugin({
    patterns: [
      {from: 'resources/persona', to: '../persona'},
    ],
  }),
];

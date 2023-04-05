import type {Configuration} from 'webpack';

import {rules} from './webpack.rules';
import {plugins} from './webpack.plugins';

rules.push({
  test: /\.vue$/,
  loader: 'vue-loader',
});

rules.push({
  test: /\.css$/,
  use: [
    'vue-style-loader',
    'css-loader',
  ],
});

export const rendererConfig: Configuration = {
  module: {
    rules,
  },
  plugins,
  resolve: {
    extensions: ['.js', '.ts', '.jsx', '.tsx', '.css', '.vue'],
  },
};

/*
 * Copyright (c) 2023 Jean Schmitz.
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

import {defineCustomElement} from 'vue';
import katex from 'katex';

const LaTeXElement = defineCustomElement({
  template: `<span :style="latexStyles" v-html="latexRendered"></span>`,
  data() {
    return {
      latexRendered: '',
      hasError: false,
    };
  },
  computed: {
    latexStyles() {
      return {
        'color': this.hasError ? 'red' : 'white',
      };
    },
  },
  props: {
    latex: String,
  },
  mounted() {
    try {
      const options = {
        displayMode: true,
        //output: 'html',
        output: 'mathml',
      };
      this.latexRendered = katex.renderToString(this.latex, options);
    } catch (error) {
      console.error('error rendering LaTeX', error);
      this.latexRendered = error;
      this.hasError = true;
    }
  },
});

customElements.define('tf-latex', LaTeXElement);

export default LaTeXElement;

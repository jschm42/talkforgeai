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

/**
 * This file will automatically be loaded by webpack and run in the "renderer" context.
 * To learn more about the differences between the "main" and the "renderer" context in
 * Electron, visit:
 *
 * https://electronjs.org/docs/latest/tutorial/process-model
 *
 * By default, Node.js integration in this file is disabled. When enabling Node.js integration
 * in a renderer process, please be aware of potential security implications. You can read
 * more about security risks here:
 *
 * https://electronjs.org/docs/tutorial/security
 *
 * To enable Node.js integration in this file, open up `main.js` and enable the `nodeIntegration`
 * flag:
 *
 * ```
 *  // Create the browser window.
 *  mainWindow = new BrowserWindow({
 *    width: 800,
 *    height: 600,
 *    webPreferences: {
 *      nodeIntegration: true
 *    }
 *  });
 * ```
 */
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import 'bootstrap-icons/font/bootstrap-icons.css';

import '@fortawesome/fontawesome-free/js/brands.js';
import '@fortawesome/fontawesome-free/js/solid.js';
import '@fortawesome/fontawesome-free/js/fontawesome.js';

import 'highlight.js/styles/github.css';
import {createApp} from 'vue';
import App from './component/App.vue';
import {useStore} from './store';

class MyCustomElement extends HTMLElement {

  constructor() {
    super();

    this.title = this.innerHTML;
    // Your initialization code here

    this.innerHTML = `Image Prompt`;

    // Add a CSS class to the custom element
    this.classList.add('image-prompt-element');

  }

  // Define your custom element's behavior here
}

customElements.define('image-prompt', MyCustomElement);

const app = createApp(App);

app.use(useStore);
app.mount('#app');

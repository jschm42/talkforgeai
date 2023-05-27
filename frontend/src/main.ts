import {createApp} from 'vue';
import App from './App.vue';
import router from './router';
import {useStore} from '@/store';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import 'bootstrap';
import '@fortawesome/fontawesome-free/js/brands.js';
import '@fortawesome/fontawesome-free/js/solid.js';
import '@fortawesome/fontawesome-free/js/fontawesome.js';
import 'highlight.js/styles/atom-one-dark.css';

createApp(App).use(router).use(useStore).mount('#app');

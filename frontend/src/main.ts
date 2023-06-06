import {createApp} from 'vue';
import App from './App.vue';
import router from './router';
import {useStore} from '@/store';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.min.css';
import '@fortawesome/fontawesome-free/css/fontawesome.min.css';
import 'bootstrap';
import 'highlight.js/styles/atom-one-dark.css';

createApp(App).use(router).use(useStore).mount('#app');

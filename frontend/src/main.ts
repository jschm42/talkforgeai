import {createApp} from 'vue';
import App from './App.vue';
import router from './router';
import {useStore} from '@/store';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';

createApp(App).use(router).use(useStore).mount('#app');

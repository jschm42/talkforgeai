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

import {createApp} from 'vue';
import router from './router';
import {useStore} from '@/store';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.min.css';
import 'bootstrap';
import 'highlight.js/styles/atom-one-dark.css';

import '@/custom/LaTeXComponent.js';
import App from '@/components/App.vue';
import {createVuetify} from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';

const vuetify = createVuetify({
  theme: {
    defaultTheme: 'dark',
  },
  components,
  directives,
});

const app = createApp(App);

app.use(vuetify);
app.use(router);
app.use(useStore);
app.mount('#app');



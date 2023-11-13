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

import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router';
import PersonaChoiceView from '@/views/PersonaChoiceView.vue';
import ChatView from '@/views/ChatView.vue';
import PersonaEditorView from '@/views/PersonaEditorView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'persona-choice',
    component: PersonaChoiceView,
  },
  {
    path: '/chat/:assistantId',
    name: 'chat',
    component: ChatView,
    props: true,
  },
  {
    path: '/persona/create',
    name: 'persona-create',
    component: PersonaEditorView,
  },

  {
    path: '/persona/edit/:assistantId',
    name: 'persona-edit',
    component: PersonaEditorView,
    props: true,
  },

];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;

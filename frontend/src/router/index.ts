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

import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router';
import PersonaChoiceView from '@/components/choice/ChoiceView.vue';
import ChatView from '@/components/chat/ChatView.vue';
import EditorView from '@/components/editor/EditorView.vue';
import MemoryView from '@/components/memory/MemoryView.vue';

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
    component: EditorView,
  },

  {
    path: '/persona/edit/:assistantId',
    name: 'persona-edit',
    component: EditorView,
    props: true,
  },

  {
    path: '/memory/editor',
    name: 'memory-editor',
    component: MemoryView,
    props: true,
  },

];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;

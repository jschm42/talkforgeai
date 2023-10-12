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
    path: '/chat/:personaId',
    name: 'chat',
    component: ChatView,
    props: true,
  },
  {
    path: '/persona/create',
    name: 'persona-create',
    component: PersonaEditorView,
    props: false,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;

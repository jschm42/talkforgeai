import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router';
import PersonaChoiceView from '@/views/PersonaChoiceView.vue';
import ChatView from '@/views/ChatView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: PersonaChoiceView,
  },
  {
    path: '/chat/:personaId',
    name: 'chat',
    component: ChatView,
    props: true,
  },
  // {
  //   path: '/about',
  //   name: 'about',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  // }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;

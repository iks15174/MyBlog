import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/?',
    component: () => import(/* webpackChunkName: "home" */ '../views/Home.vue'),
    props: true
  }, {
    path: '/posts/:id',
    component: () => import(/* webpackChunkName: "post" */ '../views/Post.vue'),
    props: true
  }, {
    path: '/posts/create',
    component: () => import(/* webpackChunkName: "post" */ '../views/CreatePost.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router

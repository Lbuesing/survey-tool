import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/surveys/:id',
    name: 'SurveyView',
    component: () => import('../views/SurveyView.vue')
  },
  {
    path: '/:pathMatch(.*)*', // Catch-all for undefined routes
    name: 'NotFound',
    component: () => import('../views/NotFound.vue') // You'll need to create this file
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

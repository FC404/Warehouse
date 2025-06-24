import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Dashboard from '../views/Dashboard.vue';
import Orders from '../views/Orders.vue';
import outsourcingDeliveries from '../views/OutsourcingDeliveries.vue';
import MaterialForm from '../views/MaterialForm.vue';
import User from '../views/User.vue';
import Taskbar from '../views/Taskbar.vue';
import Log from '../views/Log.vue';

// 导入404页面组件
import NotFound from '../views/NotFound.vue';
import { validateUser } from '@/api'; // 引入封装的 validateUser 接口

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    {
      path: '/taskbar',
      name: 'taskbar',
      component: Taskbar,
      meta: { requiresAuth: true }
    },
    {
      path: '/user',
      name: 'user',
      component: User,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: Orders,
      meta: { requiresAuth: true }
    },
    {
      path: '/outsourcingDeliveries',
      name: 'outsourcingDeliveries',
      component: outsourcingDeliveries,
      meta: { requiresAuth: true }
    },
    {
      path: '/materialForm',
      name: 'materialForm',
      component: MaterialForm,
      meta: { requiresAuth: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: NotFound
    },
    {
      path: '/log',
      name: 'log',
      component: Log,
      meta: { requiresAuth: true }
    }


  ]
});

// 路由守卫：验证 token
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token'); // 从 localStorage 获取 token

  // 如果目标路由需要认证（meta.requiresAuth），但没有 token 或 token 无效
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      // 如果没有 token，跳转到登录页
      next({ name: 'login' });
    } else {
      try {
        // 调用 validateUser 接口验证 token
        const response = await validateUser(token);
        console.log(response);

        if (response.data.code === 0) {
          // 如果验证成功，继续路由跳转
          next();
        } else {
          // 如果验证失败，跳转到登录页
          next({ name: 'login' });
        }
      } catch (error) {
        console.error('验证 token 失败', error);
        // 验证失败，跳转到登录页
        next({ name: 'login' });
      }
    }
  } else {
    // 不需要认证的路由，直接跳转
    next();
  }
});


export default router;

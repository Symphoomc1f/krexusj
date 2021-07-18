import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'HC物联网',
      component: () => import('@/views/dashboard/index'),
      meta: { title: 'HC小区物联网平台', icon: '' }
    }]
  },

  {
    path: '/accessControl',
    component: Layout,
    redirect: '/accessControl/accessControl',
    name: 'Example',
    meta: { title: '门禁管理', icon: 'example' },
    children: [
      {
        path: 'accessControl',
        name: '门禁',
        component: () => import('@/views/accessControl/accessControl'),
        meta: { title: '门禁设备', icon: 'table' }
      },
      {
        path: 'accessControlProtocol',
        name: '门禁协议',
        component: () => import('@/views/accessControlProtocol/accessControlProtocol'),
        meta: { title: '门禁协议', icon: 'table' }
      }
    ]
  },
  {
    path: '/barrierGate',
    component: Layout,
    redirect: '/barrierGate/barrierGate',
    name: 'Example',
    meta: { title: '道闸管理', icon: 'example' },
    children: [
      {
        path: 'barrierGate',
        name: '道闸',
        component: () => import('@/views/barrierGate/barrierGate'),
        meta: { title: '道闸设备', icon: 'table' }
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/tree/index'),
        meta: { title: 'Tree', icon: 'tree' }
      }
    ]
  },
  {
    path: '/monitor',
    component: Layout,
    redirect: '/monitor/monitor',
    name: 'Example',
    meta: { title: '监控管理', icon: 'example' },
    children: [
      {
        path: 'monitor',
        name: '道闸',
        component: () => import('@/views/monitorVedio/monitorVedio'),
        meta: { title: '监控设备', icon: 'table' }
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/tree/index'),
        meta: { title: 'Tree', icon: 'tree' }
      }
    ]
  },
  {
    path: '/attendance',
    component: Layout,
    redirect: '/attendance/attendance',
    name: 'Example',
    meta: { title: '云考勤管理', icon: 'example' },
    children: [
      {
        path: '/attendance',
        name: '考勤',
        component: () => import('@/views/attendance/attendance'),
        meta: { title: '考勤设备', icon: 'table' }
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/tree/index'),
        meta: { title: 'Tree', icon: 'tree' }
      }
    ]
  },
  {
    path: '/settings',
    component: Layout,
    redirect: '/settings/settings',
    name: 'Example',
    meta: { title: '系统设置', icon: 'example' },
    children: [
      {
        path: '/mapping',
        name: '系统设置',
        component: () => import('@/views/mapping/mapping'),
        meta: { title: '系统配置', icon: 'table' }
      },
      {
        path: '/communitySettings',
        name: '设置小区',
        component: () => import('@/views/communitySettings/communitySettings'),
        meta: { title: '设置小区', icon: 'table' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

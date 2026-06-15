<template>

  <div class="layout">

    <!-- 顶部 -->

    <header class="top-header">

      <div class="left-area">

        <div class="logo">
          WMS
        </div>

      </div>

      <div class="right-area">

        <div class="warehouse-name">
          亿为科技(YW)
        </div>

        <!-- 用户区域 -->

        <div
            class="user-dropdown"
            @mouseenter="showUserMenu = true"
            @mouseleave="showUserMenu = false"
        >



        </div>

        <!-- 时间 -->

        <div class="time">
          {{ currentTime }}
        </div>

        <div class="user-trigger">
            <span class="gear">
              Admin
            </span>
        </div>

        <!-- 下拉菜单 -->

        <div
            v-show="showUserMenu"
            class="dropdown-menu"
        >

          <div
              class="dropdown-item"
              @click="switchUser"
          >
            切换用户
          </div>

          <div
              class="dropdown-item logout"
              @click="logout"
          >
            退出登录
          </div>

        </div>

      </div>

    </header>

    <!-- 主区域 -->

    <div class="main-container">

      <!-- 左侧菜单 -->

      <aside class="sidebar">

        <div class="menu-title">
          功能菜单
        </div>

        <!-- 入库 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/inbound' }"
            @click="go('/inbound')"
        >
          入库页面
        </div>

        <!-- 出库 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/outbound' }"
            @click="go('/outbound')"
        >
          出库页面
        </div>

        <!-- 任务 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/task-query' }"
            @click="go('/task-query')"
        >
          任务管理
        </div>

        <!-- 物料 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/material' }"
            @click="go('/material')"
        >
          物料管理
        </div>

        <!-- 客商 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/customer' }"
            @click="go('/customer')"
        >
          客商管理
        </div>

        <!-- 库存 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/inventory' }"
            @click="go('/inventory')"
        >
          库存管理
        </div>

        <!-- 库位 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/location' }"
            @click="go('/location')"
        >
          库位管理
        </div>

        <!-- 日志管理 -->

        <div class="menu-group">

          <div
              class="menu-item"
              @click="toggleLogMenu"
          >

            <span>
              日志管理
            </span>

            <span class="arrow">
              {{ logMenuOpen ? '▼' : '▶' }}
            </span>

          </div>

          <!-- 子菜单 -->

          <div
              v-show="logMenuOpen"
              class="submenu"
          >

            <div
                class="submenu-item"
                :class="{ active: route.path === '/operation-log' }"
                @click="go('/operation-log')"
                style="font-size: 15px"
            >
              操作日志
            </div>

            <div
                class="submenu-item"
                :class="{ active: route.path === '/api-log' }"
                @click="go('/api-log')"
                style="font-size: 15px"
            >
              接口日志
            </div>

          </div>

        </div>

        <!-- 系统 -->

        <div
            class="menu-item"
            :class="{ active: route.path === '/system' }"
            @click="go('/system')"
        >
          系统管理
        </div>

      </aside>

      <!-- 内容区域 -->

      <main class="content">

        <router-view />

      </main>

    </div>

  </div>

  <GlobalMessage
      ref="globalMessageRef"
  />
</template>

<script setup>

import { useRouter, useRoute } from 'vue-router'
import {
  ref,
  onMounted,
  onUnmounted, provide
} from 'vue'

import GlobalMessage
  from '@/components/GlobalMessage.vue'

const globalMessageRef = ref(null)

const showMessage = (
    text,
    type = 'info'
) => {

  globalMessageRef.value.show(
      text,
      type
  )

}

/* 全局提供 */

provide(
    'showMessage',
    showMessage
)

const router = useRouter()

const route = useRoute()

const currentTime = ref('')

/* 用户菜单 */

const showUserMenu = ref(false)

/* 日志菜单 */

const logMenuOpen = ref(false)

/* 跳转 */

const go = (path) => {

  router.push(path)

}

/* 日志菜单 */

const toggleLogMenu = () => {

  logMenuOpen.value =
      !logMenuOpen.value

}

/* 切换用户 */

const switchUser = () => {

  alert('切换用户')

}

/* 登出 */

const logout = () => {

  alert('退出登录')

}

/* 更新时间 */

const updateTime = () => {

  const now = new Date()

  const year = now.getFullYear()

  const month =
      String(now.getMonth() + 1)
          .padStart(2, '0')

  const day =
      String(now.getDate())
          .padStart(2, '0')

  const hour =
      String(now.getHours())
          .padStart(2, '0')

  const minute =
      String(now.getMinutes())
          .padStart(2, '0')

  const second =
      String(now.getSeconds())
          .padStart(2, '0')

  currentTime.value =
      `${year}-${month}-${day} ${hour}:${minute}:${second}`

}

let timer = null

onMounted(() => {

  updateTime()

  timer = setInterval(() => {

    updateTime()

  }, 1000)

})

onUnmounted(() => {

  clearInterval(timer)

})

</script>

<style scoped>

/* 重置 */

* {

  margin: 0;

  padding: 0;

  box-sizing: border-box;
}

/* 主布局 */

.layout {

  height: 100vh;

  display: flex;

  flex-direction: column;

  overflow: hidden;

  background: #f5f7fb;
}

/* 顶部 */

.top-header {

  height: 64px;

  flex-shrink: 0;

  background:
      linear-gradient(
          90deg,
          #1677ff,
          #409eff
      );

  display: flex;

  align-items: center;

  justify-content: space-between;

  padding: 0 24px;

  color: white;

  box-shadow:
      0 2px 10px rgba(0,0,0,0.1);
}

/* 左侧 */

.left-area {

  display: flex;

  align-items: center;
}

.logo {

  font-size: 24px;

  font-weight: bold;

  letter-spacing: 2px;
}

/* 右侧 */

.right-area {

  display: flex;

  align-items: center;

  gap: 30px;
}

.warehouse-name {

  font-size: 18px;
}

/* 用户菜单 */

.user-dropdown {

  position: relative;
}

.user-trigger {

  display: flex;

  align-items: center;

  gap: 8px;

  cursor: pointer;

  padding: 6px 12px;

  border-radius: 8px;

  transition: 0.2s;
}

.user-trigger:hover {

  background: rgba(255,255,255,0.15);
}

.gear {

  font-size: 18px;
}

.user {

  font-size: 18px;
}

/* 下拉 */

.dropdown-menu {

  position: absolute;

  top: 48px;

  right: 0;

  width: 140px;

  background: white;

  border-radius: 8px;

  overflow: hidden;

  box-shadow:
      0 4px 12px rgba(0,0,0,0.15);

  z-index: 100;
}

.dropdown-item {

  height: 42px;

  display: flex;

  align-items: center;

  padding-left: 16px;

  color: #333;

  cursor: pointer;

  transition: 0.2s;
}

.dropdown-item:hover {

  background: #f5f7fb;
}

.logout {

  color: #f56c6c;
}

/* 时间 */

.time {

  font-size: 15px;

  font-weight: bold;
}

/* 主区域 */

.main-container {

  flex: 1;

  display: flex;

  overflow: hidden;
}

/* 左侧菜单 */

.sidebar {

  width: 220px;

  flex-shrink: 0;

  background: white;

  border-right: 1px solid #ebeef5;

  overflow-y: auto;

  padding-top: 12px;
}

.menu-title {

  height: 40px;

  display: flex;

  align-items: center;

  padding-left: 24px;

  color: #888;

  font-size: 13px;
}

/* 菜单 */

.menu-item {

  height: 52px;

  display: flex;

  align-items: center;

  justify-content: space-between;

  padding: 0 20px 0 28px;

  cursor: pointer;

  transition: all 0.2s;

  color: #444;

  font-size: 15px;
}

.menu-item:hover {

  background: #f2f8ff;
}

.menu-item.active {

  background: #e8f3ff;

  color: #1677ff;

  border-right: 4px solid #1677ff;

  font-weight: bold;
}

/* 子菜单 */

.submenu {

  background: #fafcff;
}

.submenu-item {

  height: 44px;

  display: flex;

  align-items: center;

  padding-left: 52px;

  cursor: pointer;

  color: #666;

  transition: 0.2s;
}

.submenu-item:hover {

  background: #eef5ff;

  color: #1677ff;
}




.submenu-item.active {

  color: #1677ff;

  font-weight: bold;
}

/* 箭头 */

.arrow {

  font-size: 12px;

  color: #999;
}

/* 内容区 */

.content {

  flex: 1;

  overflow-y: auto;

  overflow-x: hidden;

  padding: 20px;

  min-width: 0;

  background: #f5f7fb;
}

/* 滚动条 */

::-webkit-scrollbar {

  width: 8px;

  height: 8px;
}

::-webkit-scrollbar-thumb {

  background:
      rgba(0,0,0,0.15);

  border-radius: 10px;
}

::-webkit-scrollbar-track {

  background: transparent;
}

</style>
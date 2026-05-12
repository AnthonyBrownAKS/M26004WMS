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

        <div class="time-area">

          <div class="user">
            Administrator
          </div>

          <div class="time">
            {{ currentTime }}
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

        <div
            class="menu-item"
            :class="{ active: route.path === '/inbound' }"
            @click="go('/inbound')"
        >
          入库页面
        </div>

        <div
            class="menu-item"
            :class="{ active: route.path === '/outbound' }"
            @click="go('/outbound')"
        >
          出库页面
        </div>

        <div
            class="menu-item"
            :class="{ active: route.path === '/task-query' }"
            @click="go('/task-query')"
        >
          任务查询
        </div>

      </aside>

      <!-- 内容区域 -->

      <main class="content">

        <router-view />

      </main>

    </div>

  </div>

</template>

<script setup>

import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'

const router = useRouter()

const route = useRoute()

const currentTime = ref('')

const go = (path) => {

  router.push(path)

}

/* 更新时间 */

const updateTime = () => {

  const now = new Date()

  const year = now.getFullYear()

  const month = String(now.getMonth() + 1).padStart(2, '0')

  const day = String(now.getDate()).padStart(2, '0')

  const hour = String(now.getHours()).padStart(2, '0')

  const minute = String(now.getMinutes()).padStart(2, '0')

  const second = String(now.getSeconds()).padStart(2, '0')

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

/* 全局重置 */

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 整体布局 */

.layout {

  flex: 1;

  display: flex;

  flex-direction: column;

  overflow: hidden;
}

/* 顶部 */

.top-header {

  height: 64px;

  flex-shrink: 0;

  background: linear-gradient(90deg, #1677ff, #409eff);

  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 0 24px;

  color: white;

  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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
  font-size: 20px;
  margin-right: 30px;
}

.time-area {

  display: flex;

  flex-direction: column;

  align-items: flex-end;

  gap: 4px;
}

.time {

  font-size: 14px;

  font-weight: bold;
}

.user {

  font-size: 20px;

  opacity: 0.9;
}

/* 主体 */

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

.menu-item {

  height: 52px;

  display: flex;

  align-items: center;

  padding-left: 28px;

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

/* 右侧内容 */

.content {

  flex: 1;

  overflow-y: auto;

  padding: 20px;

  min-width: 0;
}

/* 滚动条美化 */

::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.15);
  border-radius: 10px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

</style>
<template>

  <div class="api-log-page">

    <div class="card table-card">

      <!-- 顶部 -->

      <div class="table-header">

        <div class="card-title">
          API日志监控
        </div>

      </div>

      <!-- 查询 -->

      <div class="search-bar">

        <input
            type="datetime-local"
            v-model="startTime"
        >

        <span class="time-separator">
          至
        </span>

        <input
            type="datetime-local"
            v-model="endTime"
        >

        <button @click="loadLogList">
          查询
        </button>

        <button
            class="reset-btn"
            @click="resetSearch"
        >
          重置
        </button>

      </div>

      <!-- 表格 -->

      <div class="table-wrapper">

        <table class="log-table">

          <thead>

          <tr>

            <th>方向</th>

            <th>传入数据</th>

            <th>传出数据</th>

            <th>调用日期</th>

          </tr>

          </thead>

          <tbody>

          <tr
              v-for="item in logList"
              :key="item.id"
          >

            <!-- 类型 -->

            <td>

              <div
                  class="type-tag"
                  :class="{
                    inbound:
                    item.type === 'INBOUND',

                    outbound:
                    item.type === 'OUTBOUND'
                  }"
              >

                {{ item.type }}

              </div>

            </td>

            <!-- 参数 -->

            <td>

              <div class="json-box">

                {{ item.param }}

              </div>

            </td>

            <!-- 返回 -->

            <td>

              <div class="json-box">

                {{ item.result }}

              </div>

            </td>

            <!-- 时间 -->

            <td>

              <div class="time-text">

                {{ formatTime(item.creationTime) }}

              </div>

            </td>

          </tr>

          <!-- 占位 -->

          <tr
              v-for="n in emptyRows"
              :key="'empty-' + n"
          >

            <td colspan="4"></td>

          </tr>

          </tbody>

        </table>

      </div>

      <!-- 分页 -->

      <div class="pagination">

        <button
            @click="prevPage"
            :disabled="current === 1"
        >
          上一页
        </button>

        <span>
          第 {{ current }} / {{ pages }} 页
        </span>

        <button
            @click="nextPage"
            :disabled="current >= pages"
        >
          下一页
        </button>

        <span>
          共 {{ total }} 条
        </span>

      </div>

    </div>

  </div>

</template>

<script setup>

import axios from 'axios'

import {
  computed,
  inject,
  onMounted,
  ref
} from 'vue'

const showMessage =
    inject('showMessage')

const logList = ref([])

const startTime = ref('')

const endTime = ref('')

const current = ref(1)

const size = ref(7)

const pages = ref(1)

const total = ref(0)

const emptyRows = computed(() => {

  return Math.max(
      0,
      size.value - logList.value.length
  )

})

const loadLogList = async () => {

  try {

    const res = await axios.get(

        '/api/log/apiLog',

        {

          params: {

            current: current.value,

            size: size.value,

            startTime: startTime.value,

            endTime: endTime.value

          }

        }

    )

    const data = res.data.data

    logList.value =
        data.records || []

    pages.value =
        data.pages || 1

    total.value =
        data.total || 0

  } catch (e) {

    console.error(e)

    showMessage('日志加载失败', 'error')

  }

}

const prevPage = () => {

  if (current.value > 1) {

    current.value--

    loadLogList()

  }

}

const nextPage = () => {

  if (current.value < pages.value) {

    current.value++

    loadLogList()

  }

}

const resetSearch = () => {

  startTime.value = ''

  endTime.value = ''

  current.value = 1

  loadLogList()

}

const formatTime = (time) => {

  if (!time) {

    return ''

  }

  return time
      .replace('T', ' ')
      .substring(0, 19)

}

onMounted(() => {

  loadLogList()

})

</script>

<style scoped>

.api-log-page {

  padding: 20px;

  background: #eef2f6;

}

/* 卡片 */

.card {

  background: white;

  border-radius: 10px;

  padding: 24px;

  box-shadow:
      0 2px 8px rgba(0,0,0,0.05);
}

/* 顶部 */

.table-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 20px;
}

.card-title {

  font-size: 18px;

  font-weight: bold;

  color: #2b2f36;

  letter-spacing: 1px;
}

/* 查询 */

.search-bar {

  display: flex;

  align-items: center;

  gap: 12px;

  margin-bottom: 18px;
}

.search-bar input {

  height: 40px;

  border: 1px solid #cfd6df;

  border-radius: 6px;

  padding: 0 14px;

  background: #ffffff;

  color: #2f3440;

  outline: none;

  transition: 0.2s;

  font-size: 13px;
}

.search-bar input:hover {

  border-color: #1677ff;
}

.search-bar input:focus {

  border-color: #1677ff;

  box-shadow:
      0 0 0 2px rgba(22,119,255,0.12);
}

.search-bar button {

  min-width: 90px;

  height: 38px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;

  background: #1677ff;

  transition: 0.2s;
}

.search-bar button:hover {

  opacity: 0.9;
}

.reset-btn {

  background: #909399 !important;
}

.time-separator {

  color: #606266;

  font-size: 13px;
}

/* 表格 */

.table-wrapper {

  flex: 1;

  border: 1px solid #d8dee6;

  border-radius: 8px;

  overflow: auto;

  background: #f7f9fc;
}

.log-table {

  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

.log-table thead {

  position: sticky;

  top: 0;

  z-index: 10;

  background: #eef2f6;
}

.log-table th {

  height: 46px;

  font-size: 13px;

  color: #5c6675;

  font-weight: 700;

  border-bottom: 1px solid #dfe5ec;
}

.log-table td {

  padding: 10px;

  border-bottom: 1px solid #edf1f5;

  text-align: center;

  vertical-align: top;
}

/* 类型标签 */

.type-tag {

  display: inline-flex;

  align-items: center;

  justify-content: center;

  min-width: 92px;

  height: 30px;

  border-radius: 4px;

  font-size: 12px;

  font-weight: bold;

  letter-spacing: 1px;
}

.inbound {

  background: rgba(22,119,255,0.12);

  color: #1677ff;

  border: 1px solid rgba(22,119,255,0.25);
}

.outbound {

  background: rgba(255,122,69,0.12);

  color: #ff7a45;

  border: 1px solid rgba(255,122,69,0.25);
}

/* JSON框 */

.json-box {

  max-height: 130px;

  overflow: auto;

  padding: 10px 12px;

  border-radius: 6px;

  background: #f4f7fb;

  border: 1px solid #d8dee8;

  color: #394150;

  text-align: left;

  font-size: 12px;

  line-height: 1.5;

  font-family:
      Consolas,
      Monaco,
      monospace;

  white-space: pre-wrap;

  word-break: break-all;
}

.json-box:hover {

  border-color: #1677ff;

  background: #f8fbff;
}

.log-table tbody tr {

  height: 88px;

  transition: 0.15s;
}

.log-table tbody tr:hover {

  background: #f5f9ff;
}

/* 时间 */

.time-text {

  font-size: 13px;

  color: #606266;

  font-weight: 600;

  white-space: nowrap;
}

/* 分页 */

.pagination {

  margin-top: 18px;

  display: flex;

  justify-content: right;

  align-items: center;

  gap: 14px;
}

.pagination button {

  min-width: 86px;

  height: 36px;

  border: none;

  border-radius: 6px;

  background: #1677ff;

  color: white;

  cursor: pointer;
}

.pagination button:disabled {

  background: #c0c4cc;
}

/* 滚动条 */

.json-box::-webkit-scrollbar {

  width: 6px;

  height: 6px;
}

.json-box::-webkit-scrollbar-thumb {

  background: #4b5563;

  border-radius: 4px;
}

</style>
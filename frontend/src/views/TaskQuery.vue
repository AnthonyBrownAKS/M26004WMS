<template>

  <div class="task-query-page">

    <!-- 顶部 -->

    <div class="card">

      <div class="card-title">
        任务查询
      </div>

      <!-- 查询栏 -->

      <div class="search-bar">

        <input
            v-model="searchContainerId"
            placeholder="请输入容器ID"
            style="color: #2e303a"
        />

        <button @click="loadTaskPage">
          查询
        </button>

      </div>

      <!-- 表格 -->

      <table class="task-table">

        <thead>

        <tr>

          <th>容器ID</th>

          <th>创建时间</th>

          <th>完成时间</th>

          <th>物料ID</th>

          <th>目标库位</th>

          <th>任务状态</th>

          <th>任务类型</th>

        </tr>

        </thead>

        <tbody>

        <!-- 数据 -->

        <tr
            v-for="item in taskList"
            :key="item.id"
        >

          <td>{{ item.containerId }}</td>

          <td>
            {{ formatTime(item.createTime) }}
          </td>

          <td>
            {{ formatTime(item.finishTime) }}
          </td>

          <td>{{ item.materialId }}</td>

          <td>{{ item.targetLocationId }}</td>

          <td>

            <span
                class="status-tag"
                :class="getStatusClass(item.status)"
            >
              {{ item.status }}
            </span>

          </td>

          <td>{{ item.taskType }}</td>

        </tr>

        <!-- 补空行 -->

        <tr
            v-for="n in (10 - taskList.length)"
            :key="'empty-' + n"
        >
          <td>&nbsp;</td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
        </tr>

        </tbody>

      </table>

      <!-- 分页 -->

      <div class="pagination">

        <button
            :disabled="current === 1"
            @click="changePage(current - 1)"
        >
          上一页
        </button>

        <span>
          第 {{ current }} / {{ pages }} 页
        </span>

        <button
            :disabled="current === pages"
            @click="changePage(current + 1)"
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
import { onMounted, ref } from 'vue'

/* 数据 */

const taskList = ref([])

const current = ref(1)

const pages = ref(1)

const total = ref(0)

const size = ref(10)

/* 查询 */

const searchContainerId = ref('')

/* 加载分页 */

const loadTaskPage = async () => {

  try {

    const res = await axios.get(

        '/api/task/page',

        {

          params: {

            current: current.value,

            size: size.value,

            containerId: searchContainerId.value

          }

        }

    )

    console.log(res.data)

    taskList.value = res.data.data.records || []

    current.value = res.data.current || 1

    pages.value = res.data.pages || 1

    total.value = res.data.total || 0

  } catch (e) {

    console.error(e)

    alert('任务数据加载失败')

  }

}

/* 翻页 */

const changePage = (page) => {

  current.value = page

  loadTaskPage()

}

/* 时间格式化 */

const formatTime = (time) => {

  if (!time) {

    return '-'
  }

  return time
      .replace('T', ' ')
      .substring(0, 19)
}

/* 状态颜色 */

const getStatusClass = (status) => {

  if (status === 'CREATED') {

    return 'created'
  }

  if (status === 'FINISHED') {

    return 'finished'
  }

  if (status === 'RUNNING') {

    return 'running'
  }

  if (status === 'FAILED') {

    return 'failed'
  }

  return ''
}

/* 初始化 */

onMounted(() => {

  loadTaskPage()

})

</script>

<style scoped>

.task-query-page {

  display: flex;

  flex-direction: column;
}

/* 卡片 */

.card {

  background: white;

  border-radius: 10px;

  padding: 20px;

  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.card-title {

  font-size: 18px;

  font-weight: bold;

  margin-bottom: 20px;

  color: #333;
}

/* 查询栏 */

.search-bar {

  margin-bottom: 20px;

  display: flex;

  align-items: center;

  gap: 12px;
}

.search-bar input {

  width: 260px;

  height: 40px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  outline: none;

  background: #f5f7fb;

  transition: 0.2s;
}

.search-bar input:focus {

  border-color: #409eff;

  background: white;
}

.search-bar button {

  height: 40px;

  padding: 0 20px;

  border: none;

  border-radius: 6px;

  background: #409eff;

  color: white;

  cursor: pointer;

  transition: 0.2s;
}

.search-bar button:hover {

  background: #2f8df7;
}

/* 表格 */

.task-table {

  width: 100%;

  border-collapse: collapse;
}

.task-table thead {

  background: #f5f7fa;
}

.task-table th {

  height: 48px;

  color: #666;

  font-weight: bold;

  border-bottom: 1px solid #ebeef5;
}

.task-table tbody {

  height: 500px;
}

.task-table td {

  height: 50px;

  text-align: center;

  border-bottom: 1px solid #ebeef5;

  color: #555;
}

/* 状态标签 */

.status-tag {

  display: inline-flex;

  align-items: center;

  justify-content: center;

  min-width: 90px;

  height: 30px;

  border-radius: 15px;

  font-size: 13px;

  font-weight: bold;
}

.created {

  background: #ecf5ff;

  color: #409eff;
}

.running {

  background: #fdf6ec;

  color: #e6a23c;
}

.finished {

  background: #f0f9eb;

  color: #67c23a;
}

.failed {

  background: #fef0f0;

  color: #f56c6c;
}

/* 分页 */

.pagination {

  margin-top: 20px;

  display: flex;

  justify-content: flex-end;

  align-items: center;

  gap: 16px;
}

.pagination button {

  height: 34px;

  padding: 0 16px;

  border: none;

  border-radius: 6px;

  background: #409eff;

  color: white;

  cursor: pointer;

  transition: 0.2s;
}

.pagination button:hover {

  background: #2f8df7;
}

.pagination button:disabled {

  background: #ccc;

  cursor: not-allowed;
}

</style>
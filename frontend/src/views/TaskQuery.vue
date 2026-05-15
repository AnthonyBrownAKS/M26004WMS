<template>

  <div class="task-query-page">

    <div class="card table-card">

      <!-- 顶部 -->

      <div class="table-header">

        <div class="card-title">
          任务管理
        </div>



      </div>

      <!-- 查询 -->

      <div class="search-bar">

        <input
            v-model="searchContainerId"
            placeholder="请输入容器ID"
            style="color: #2e303a"
        >

        <button
            class="search-button"
            @click="loadTaskPage">
          查询
        </button>

        <button
            class="add-btn"
            @click="openAdd"
        >
          新增任务
        </button>

      </div>

      <!-- 表格 -->

      <div class="table-wrapper">

        <table class="task-table">

          <thead>

          <tr>

            <th style="width: 140px">
              容器ID
            </th>

            <th style="width: 180px">
              创建时间
            </th>

            <th style="width: 180px">
              完成时间
            </th>

            <th style="width: 120px">
              物料ID
            </th>

            <th style="width: 140px">
              目标库位
            </th>

            <th style="width: 120px">
              任务状态
            </th>

            <th style="width: 120px">
              任务类型
            </th>

            <th style="width: 140px">
              操作
            </th>

          </tr>

          </thead>

          <tbody>

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

            <td>
              {{ taskTypeMap[item.taskType] }}
            </td>

            <td>

              <div class="action-buttons">

                <button
                    class="table-edit-btn"
                    @click="openEdit(item)"
                >
                  修改
                </button>

                <button
                    class="table-delete-btn"
                    @click="openDelete(item)"
                >
                  删除
                </button>

              </div>

            </td>

          </tr>

          <!-- 占位 -->

          <tr
              v-for="n in emptyRows"
              :key="'empty-' + n"
          >
            <td colspan="8"></td>
          </tr>

          </tbody>

        </table>

      </div>

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
            :disabled="current >= pages"
            @click="changePage(current + 1)"
        >
          下一页
        </button>

        <span>
          共 {{ total }} 条
        </span>

      </div>

    </div>

    <!-- 新增/修改弹窗 -->

    <div
        v-if="editVisible"
        class="dialog-mask"
    >

      <div class="dialog industrial-dialog">

        <div class="dialog-header">

          <div class="dialog-title">
            任务编辑
          </div>

        </div>

        <div class="dialog-form">

          <div class="dialog-item">

            <label>
              容器ID
            </label>

            <input
                v-model="editForm.containerId"
                placeholder="请输入容器ID"
            >

          </div>

          <div class="dialog-item">

            <label>
              物料ID
            </label>

            <input
                v-model="editForm.materialId"
                placeholder="请输入物料ID"
            >

          </div>

          <div class="dialog-item">

            <label>
              目标库位
            </label>

            <input
                v-model="editForm.targetLocationId"
                placeholder="请输入目标库位"
            >

          </div>

          <div class="dialog-item">

            <label>
              任务状态
            </label>

            <select v-model="editForm.status">

              <option value="CREATED">
                CREATED
              </option>

              <option value="RUNNING">
                RUNNING
              </option>

              <option value="FINISHED">
                FINISHED
              </option>

              <option value="FAILED">
                FAILED
              </option>

            </select>

          </div>

          <div class="dialog-item">

            <label>
              任务类型
            </label>

            <select v-model="editForm.taskType">

              <option value="IN">
                入库
              </option>

              <option value="OUT">
                出库
              </option>

            </select>

          </div>

        </div>

        <div class="dialog-buttons">

          <button
              class="dialog-confirm-btn"
              @click="submitEdit"
          >
            提交
          </button>

          <button
              class="dialog-cancel-btn"
              @click="editVisible = false"
          >
            取消
          </button>

        </div>

      </div>

    </div>

    <!-- 删除弹窗 -->

    <div
        v-if="deleteVisible"
        class="dialog-mask"
    >

      <div class="dialog delete-dialog">

        <div class="delete-title">
          确认删除该任务？
        </div>

        <div class="dialog-buttons">

          <button
              class="dialog-confirm-btn"
              @click="confirmDelete"
          >
            确定
          </button>

          <button
              class="dialog-cancel-btn"
              @click="deleteVisible = false"
          >
            取消
          </button>

        </div>

      </div>

    </div>

  </div>

</template>

<script setup>

import axios from 'axios'
import {
  computed,
  onMounted,
  ref,
  inject
} from 'vue'

const showMessage =
    inject('showMessage')

/* 数据 */

const taskList = ref([])

/* 分页 */

const current = ref(1)
const pages = ref(1)
const total = ref(0)
const size = ref(9)

/* 查询 */

const searchContainerId = ref('')

/* 弹窗 */

const editVisible = ref(false)
const deleteVisible = ref(false)
const deleteId = ref(null)

/* 表单 */

const editForm = ref({

  containerId: '',

  materialId: '',

  targetLocationId: '',

  status: 'CREATED',

  taskType: 'IN'

})

const taskTypeMap = {

  IN: '入库',

  OUT: '出库'

}

/* 空行 */

const emptyRows = computed(() => {

  return Math.max(
      0,
      size.value - taskList.value.length
  )

})

/* 加载 */

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

    const data = res.data.data

    taskList.value =
        data.records || []

    pages.value =
        data.pages || 1

    total.value =
        data.total || 0

  } catch (e) {

    console.error(e)

    showMessage('任务数据加载失败','error')

  }

}

/* 翻页 */

const changePage = (page) => {

  current.value = page

  loadTaskPage()

}

/* 重置 */

const resetSearch = () => {

  searchContainerId.value = ''

  current.value = 1

  loadTaskPage()

}

/* 新增 */

const openAdd = () => {

  editForm.value = {

    containerId: '',

    materialId: '',

    targetLocationId: '',

    status: 'CREATED',

    taskType: 'IN'

  }

  editVisible.value = true

}

/* 修改 */

const openEdit = (row) => {

  editForm.value = {

    ...row

  }

  editVisible.value = true

}

/* 提交 */

const submitEdit = async () => {

  if (
      !editForm.value.materialId ||
      !editForm.value.containerId
  ) {
    showMessage('字段不能为空', 'error')
    return
  }

  // 容器ID必须以PT开头
  if (!editForm.value.containerId.startsWith('PT')) {
    showMessage('容器ID必须以PT开头', 'error')
    return
  }
  try {

    const res = await axios.post(

        '/api/task/add',

        editForm.value

    )

    if (res.data.code === 200) {

      showMessage('操作成功','success')

      editVisible.value = false

      loadTaskPage()

    }

  } catch (e) {

    console.error(e)

    showMessage('操作失败','error')

  }

}

/* 删除 */

const openDelete = (row) => {

  deleteId.value = row.taskId

  deleteVisible.value = true

}

const confirmDelete = async () => {

  try {

    const res = await axios.delete(

        `/api/task/${deleteId.value}`

    )

    if (res.data.code === 200) {

      showMessage('删除成功','success')

      deleteVisible.value = false

      loadTaskPage()

    }

  } catch (e) {

    console.error(e)

    showMessage('删除失败','error')

  }

}

/* 时间 */

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

  if (status === 'RUNNING') {

    return 'running'
  }

  if (status === 'FINISHED') {

    return 'finished'
  }

  if (status === 'FAILED') {

    return 'failed'
  }

  return ''

}

onMounted(() => {

  loadTaskPage()

})

</script>

<style scoped>

.task-query-page {

  padding: 20px;
}

.card {

  background: white;

  border-radius: 10px;

  padding: 24px;

  box-shadow:
      0 2px 8px rgba(0,0,0,0.04);
}



.table-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 20px;
}

.card-title {

  font-size: 18px;

  font-weight: bold;

  color: #303133;
}

/* 查询 */

.search-bar {

  display: flex;

  gap: 14px;

  margin-bottom: 20px;
}

.search-bar input {

  width: 260px;

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  background: #f5f7fb;

  outline: none;
}

.search-button,
.add-btn {

  min-width: 100px;

  height: 42px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;
}

.search-button
 {
  background: #1677ff;
}

.add-btn{
  background: #67c23a;
}

/* 表格 */

.table-wrapper {

  overflow-y: auto;

  overflow-x: hidden;

  border: 1px solid #ebeef5;

  border-radius: 8px;
}

.task-table {

  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

.task-table thead {

  background: #f5f7fb;
}

.task-table th,
.task-table td {

  height: 56px;

  border-bottom: 1px solid #ebeef5;

  text-align: center;

  font-size: 14px;

  color: #333;

  white-space: nowrap;

  overflow: hidden;

  text-overflow: ellipsis;
}

/* 状态 */

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

/* 操作 */

.action-buttons {

  display: flex;

  justify-content: center;

  gap: 8px;
}

.table-edit-btn,
.table-delete-btn {

  min-width: 54px;

  height: 30px;

  border: none;

  border-radius: 5px;

  color: white;

  font-size: 13px;

  cursor: pointer;
}

.table-edit-btn {

  background: #409eff;
}

.table-delete-btn {

  background: #f56c6c;
}

/* 分页 */

.pagination {

  margin-top: 20px;

  display: flex;

  justify-content: right;

  align-items: center;

  gap: 16px;
}

.pagination button {

  min-width: 90px;

  height: 38px;

  border: none;

  border-radius: 6px;

  background: #409eff;

  color: white;

  cursor: pointer;
}

.pagination button:disabled {

  background: #c0c4cc;
}

/* 弹窗 */

.dialog-mask {

  position: fixed;

  inset: 0;

  background: rgba(0,0,0,0.35);

  display: flex;

  justify-content: center;

  align-items: center;

  z-index: 999;
}

.industrial-dialog {

  width: 520px;

  background: white;

  border-radius: 10px;

  overflow: hidden;
}

.dialog-header {

  height: 58px;

  display: flex;

  align-items: center;

  padding: 0 24px;

  background: #f7f9fc;

  border-bottom: 1px solid #ebeef5;
}

.dialog-title {

  font-size: 17px;

  font-weight: bold;
}

.dialog-form {

  padding: 24px;

  display: flex;

  flex-direction: column;

  gap: 18px;
}

.dialog-item {

  display: flex;

  align-items: center;
}

.dialog-item label {

  width: 90px;

  font-size: 14px;

  font-weight: 600;

  color: #606266;
}

.dialog-item input,
.dialog-item select {

  flex: 1;

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  background: #f5f7fb;

  color: #303133;

  font-size: 14px;

  outline: none;
}

.dialog-buttons {

  height: 72px;

  display: flex;

  justify-content: center;

  align-items: center;

  gap: 16px;

  border-top: 1px solid #ebeef5;
}

.dialog-confirm-btn,
.dialog-cancel-btn {

  min-width: 110px;

  height: 40px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;
}

.dialog-confirm-btn {

  background: #409eff;
}

.dialog-cancel-btn {

  background: #909399;
}

.delete-dialog {

  width: 360px;

  background: white;

  border-radius: 10px;

  padding: 30px;
}

.delete-title {

  text-align: center;

  font-size: 25px;

  margin-bottom: 24px;
}

</style>
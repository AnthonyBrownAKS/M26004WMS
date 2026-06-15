<template>

  <div class="material-page">

    <div class="card table-card">

      <!-- 顶部 -->

      <div class="table-header">

        <div class="card-title">
          库存管理INVENTORY
        </div>

        <div class="header-actions">

          <button
              class="add-btn"
              @click="openAdd"
          >
            新增库存
          </button>

        </div>

      </div>

      <!-- 查询 -->

      <div class="search-bar">

        <input
            v-model="searchContainerId"
            placeholder="请输入容器ID"
            @keyup.enter="loadInventoryList"
            style="color: #2e303a"
        >

        <input
            v-model="searchLocationAreaId"
            placeholder="请输入库区"
            @keyup.enter="loadInventoryList"
            style="color: #2e303a"
        >

        <button @click="loadInventoryList">
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

        <table class="inventory-table">

          <thead>

          <tr>

            <th class="expand-column"></th>

            <th>容器ID</th>

            <th>库区</th>

            <th>行</th>

            <th>列</th>

            <th>层</th>

            <th>存放物料量</th>

            <th>创建时间</th>

            <th>操作</th>

          </tr>

          </thead>

          <tbody>

          <template
              v-for="item in inventoryList"
              :key="item.id"
          >

            <!-- 主行 -->

            <tr class="main-row">

              <!-- 展开箭头 -->

              <td
                  class="expand-column"
                  @click="toggleRow(item)"
              >

                <span
                    class="expand-icon"
                    :class="{
                      expanded:
                      expandedRows[item.id]
                    }"
                >
                  ▶
                </span>

              </td>

              <td>
                {{ item.containerId }}
              </td>

              <td>
                {{ item.locationAreaId }}
              </td>

              <td>
                {{ item.rowNo }}
              </td>

              <td>
                {{ item.columnNo }}
              </td>

              <td>
                {{ item.layerNo }}
              </td>

              <td class="sum-cell">
                {{ item.sum }}
              </td>

              <td>
                {{ formatTime(item.creationTime) }}
              </td>

              <td>

                <div
                    class="action-buttons"
                    @click.stop
                >

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
                    出库
                  </button>

                </div>

              </td>

            </tr>

            <!-- 展开区域 -->

            <tr
                v-if="expandedRows[item.id]"
                class="expand-row"
            >

              <td colspan="9">

                <div
                    v-if="detailMap[item.id]?.length"
                >

                  <div
                      v-for="(detail,index)
                      in detailMap[item.id]"
                      :key="detail.id"
                      class="expand-item"
                  >

                    <!-- 编号 -->

                    <div class="expand-index">

                      #{{ index + 1 }}

                    </div>

                    <!-- 内容 -->

                    <div class="expand-content">

                      <div class="expand-grid">

                        <div class="grid-item">

                          <span class="grid-label">
                            物料编码
                          </span>

                          <span class="grid-value">
                            {{ detail.materialCode }}
                          </span>

                        </div>

                        <div class="grid-item">

                          <span class="grid-label">
                            物料名称
                          </span>

                          <span class="grid-value">
                            {{ detail.materialName }}
                          </span>

                        </div>

                        <div class="grid-item">

                          <span class="grid-label">
                            规格
                          </span>

                          <span class="grid-value">
                            {{ detail.spec }}
                          </span>

                        </div>

                        <div class="grid-item">

                          <span class="grid-label">
                            客商代码
                          </span>

                          <span class="grid-value">
                            {{ detail.customerCode }}
                          </span>

                        </div>

                        <div class="grid-item">

                          <span class="grid-label">
                            数量
                          </span>

                          <span
                              class="
                              grid-value
                              quantity-value
                            "
                          >
                            {{ detail.quantity }}
                          </span>

                        </div>

                        <div class="grid-item">

                          <span class="grid-label">
                            批次
                          </span>

                          <span class="grid-value">
                            {{ detail.batch }}
                          </span>

                        </div>

                        <div
                            class="
                            grid-item
                            time-item
                          "
                        >

                          <span class="grid-label">
                            入库时间
                          </span>

                          <span class="grid-value">
                            {{
                              formatTime(
                                  detail.creationTime
                              )
                            }}
                          </span>

                        </div>

                      </div>

                    </div>

                  </div>

                </div>

                <div
                    v-else
                    class="empty-detail"
                >
                  暂无物料
                </div>

              </td>

            </tr>

          </template>

          <!-- 空行 -->

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

    <!-- 新增/修改 -->

    <div
        v-if="addVisible"
        class="dialog-mask"
    >

      <div class="dialog industrial-dialog">

        <div class="dialog-header">

          <div class="dialog-title">

            {{ editForm.id ? '修改库存' : '新增库存' }}

          </div>

        </div>

        <div class="dialog-form">

          <div class="dialog-item">

            <label>容器ID</label>

            <input
                v-model="editForm.containerId"
            >

          </div>


          <div class="dialog-item">

            <label>库区ID</label>

            <input
                v-model="editForm.locationAreaId"
            >

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
              @click="addVisible = false"
          >
            取消
          </button>

        </div>

      </div>

    </div>

    <div
        v-if="editVisible"
        class="dialog-mask"
    >

      <div class="dialog industrial-dialog">

        <div class="dialog-header">

          <div class="dialog-title">

            {{'修改库存'}}

          </div>

        </div>

        <div class="dialog-form">


          <div class="dialog-item">

            <label>容器ID</label>

            <input
                v-model="editForm.containerId"
            >

          </div>


          <div class="dialog-item">

            <label>库区</label>

            <input
                v-model="editForm.locationAreaId"
            >

          </div>

          <div class="dialog-item">

            <label>行</label>

            <input
                v-model="editForm.rowNo"
            >

          </div>

          <div class="dialog-item">

            <label>列</label>

            <input
                v-model="editForm.columnNo"
            >

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
              @click="addVisible = false, editVisible = false"
          >
            取消
          </button>

        </div>

      </div>

    </div>

    <!-- 出库 -->

    <div
        v-if="deleteVisible"
        class="dialog-mask"
    >

      <div class="dialog delete-dialog">

        <div class="delete-title">
          确认出库？
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
  inject,
  onMounted,
  ref
} from 'vue'

const showMessage =
    inject('showMessage')

const inventoryList = ref([])

const addVisible = ref(false)

const searchContainerId = ref('')

const searchLocationAreaId = ref('')

const current = ref(1)

const size = ref(8)

const pages = ref(1)

const total = ref(0)

const editVisible = ref(false)

const deleteVisible = ref(false)

const deleteId = ref(null)

const expandedRows = ref({})

const detailMap = ref({})

const editForm = ref({

  id:'',

  materialCode: '',

  customerCode: '',

  containerId: '',

  quantity: 0,

  batch: '',

  locationAreaId: '',

  rowNo: 0,

  columnNo: 0

})

const emptyRows = computed(() => {

  return Math.max(
      0,
      size.value - inventoryList.value.length
  )

})

const loadInventoryList = async () => {

  try {

    const res = await axios.get(

        '/api/inventory/page',

        {

          params: {

            current: current.value,

            size: size.value,

            containerId:
            searchContainerId.value,

            locationAreaId:
            searchLocationAreaId.value

          }

        }

    )

    const data = res.data.data

    inventoryList.value =
        data.records || []

    pages.value =
        data.pages || 1

    total.value =
        data.total || 0

  } catch (e) {

    console.error(e)

    showMessage('库存加载失败', 'error')

  }

}

const toggleRow = async (item) => {

  const rowId = item.id

  expandedRows.value[rowId] =
      !expandedRows.value[rowId]

  if (detailMap.value[rowId]) {

    return

  }

  try {

    const res = await axios.get(
        `/api/inventory/list/${item.containerId}`
    )

    detailMap.value[rowId] =
        res.data.data || []

  } catch (e) {

    console.error(e)

    showMessage('详情加载失败', 'error')

  }

}

const prevPage = () => {

  if (current.value > 1) {

    current.value--

    loadInventoryList()

  }

}

const nextPage = () => {

  if (current.value < pages.value) {

    current.value++

    loadInventoryList()

  }

}

const resetSearch = () => {

  searchContainerId.value = ''

  searchLocationAreaId.value = ''

  current.value = 1

  loadInventoryList()

}

const openAdd = () => {

  editForm.value = {

    id:'',

    containerId: '',

    locationAreaId: '',

  }

  addVisible.value = true

}

const openEdit = (row) => {

  editForm.value = {

    ...row

  }

  editVisible.value = true

}

const submitEdit = async () => {

  // 容器ID必须以PT开头
  if (!editForm.value.containerId.startsWith('PT')) {

    showMessage('容器ID必须以PT开头', 'error')
    return

  }

  try {

    const res = await axios.post(

        '/api/inventory/add',

        editForm.value

    )

    if (res.data.code === 200) {

      showMessage('提交成功', 'success')

      editVisible.value = false
      addVisible.value = false

      loadInventoryList()

    }

  } catch (e) {

    console.error(e)

    showMessage('提交失败', 'error')

  }

}

const openDelete = (row) => {

  deleteId.value = row.containerId

  deleteVisible.value = true

}

const confirmDelete = async () => {

  try {

    const res = await axios.post(
        `/api/task/createOutbound/${deleteId.value}`
    )

    if (res.data.code === 200) {

      showMessage('出库成功', 'success')

      deleteVisible.value = false

      loadInventoryList()

    }

  } catch (e) {

    console.error(e)

    showMessage('出库失败', 'error')

  }

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

  loadInventoryList()

})

</script>

<style scoped>

.material-page {

  padding: 20px;

  background: #eef2f6;

}

.card {

  background: white;

  border-radius: 8px;

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

.add-btn {

  min-width: 110px;

  height: 40px;

  border: none;

  border-radius: 6px;

  background: #1677ff;

  color: white;

  cursor: pointer;
}

.search-bar {

  display: flex;

  gap: 14px;

  margin-bottom: 20px;
}

.search-bar input {

  width: 280px;

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  background: #f5f7fb;

  outline: none;
}

.search-bar button {

  min-width: 100px;

  height: 42px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;
}

.search-bar button:first-of-type {

  background: #1677ff;
}

.reset-btn {

  background: #909399;
}

.table-wrapper {

  border: 1px solid #dfe5ec;

  border-radius: 8px;

  overflow: hidden;
}

.inventory-table {

  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

.inventory-table thead {

  background: #eef2f6;
}

.inventory-table th {

  height: 46px;

  font-size: 13px;

  color: #5c6675;

  font-weight: 700;

  border-bottom: 1px solid #dfe5ec;
}

.inventory-table td {

  height: 44px;

  font-size: 13px;

  color: #2f3542;

  border-bottom: 1px solid #edf1f5;

  text-align: center;
}

.main-row:hover {

  background: #f7fbff;
}

.expand-column {

  width: 48px;
}

.expand-icon {

  display: inline-block;

  transition: 0.2s;

  cursor: pointer;

  color: #606266;
}

.expand-icon.expanded {

  transform: rotate(90deg);

  color: #1677ff;
}

.sum-cell {

  font-weight: bold;

  color: #1677ff;
}

.expand-row td {

  background: #eef2f6;

  padding: 10px 14px;
}

.expand-item {

  display: flex;

  margin-bottom: 8px;

  border: 1px solid #d7dde5;

  border-radius: 6px;

  overflow: hidden;

  background: #f8fafc;
}

.expand-index {

  width: 64px;

  background: #2f3b52;

  color: white;

  display: flex;

  justify-content: center;

  align-items: center;

  font-size: 15px;

  font-weight: bold;
}

.expand-content {

  flex: 1;

  padding: 10px 14px;
}

.expand-grid {

  display: grid;

  grid-template-columns:
      repeat(4, 1fr);

  gap: 8px 18px;
}

.grid-item {

  display: flex;

  align-items: center;

  min-height: 26px;

  font-size: 13px;
}

.grid-label {

  color: #7a8699;

  margin-right: 8px;

  white-space: nowrap;
}

.grid-value {

  color: #1f2937;

  font-weight: 600;
}

.quantity-value {

  color: #1677ff;

  font-size: 15px;
}

.time-item {

  grid-column: span 2;
}

.empty-detail {

  padding: 18px;

  color: #909399;
}

.action-buttons {

  display: flex;

  justify-content: center;

  gap: 8px;
}

.table-edit-btn,
.table-delete-btn {

  min-width: 54px;

  height: 28px;

  border: none;

  border-radius: 4px;

  color: white;

  font-size: 12px;

  cursor: pointer;
}

.table-edit-btn {

  background: #1677ff;
}

.table-delete-btn {

  background: #3cb1a8;
}

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

  border-radius: 8px;

  overflow: hidden;
}

.dialog-header {

  height: 56px;

  display: flex;

  align-items: center;

  padding: 0 22px;

  background: #f5f7fa;

  border-bottom: 1px solid #ebeef5;
}

.dialog-title {

  font-size: 16px;

  font-weight: bold;
}

.dialog-form {

  padding: 22px;

  display: flex;

  flex-direction: column;

  gap: 16px;
}

.dialog-item {

  display: flex;

  align-items: center;
}

.dialog-item label {

  width: 90px;

  font-size: 13px;

  font-weight: 600;

  color: #606266;
}

.dialog-item input {

  flex: 1;

  height: 38px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  background: white;

  color: #303133;

  outline: none;

  transition: 0.2s;
}


.dialog-buttons {

  height: 68px;

  display: flex;

  justify-content: center;

  align-items: center;

  gap: 14px;

  border-top: 1px solid #ebeef5;
}

.dialog-confirm-btn,
.dialog-cancel-btn {

  min-width: 100px;

  height: 38px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;
}

.dialog-confirm-btn {

  background: #1677ff;
}

.dialog-cancel-btn {

  background: #909399;
}

.delete-dialog {

  width: 340px;

  background: white;

  border-radius: 8px;

  padding: 28px;
}

.delete-title {

  text-align: center;

  font-size: 20px;

  margin-bottom: 22px;
}

/* 数字输入框 */

.number-input {

  flex: 1;

  display: flex;

  align-items: center;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  overflow: hidden;

  background: white;
}

/* 输入框 */

.number-input input {

  flex: 1;

  height: 38px;

  border: none;

  text-align: center;

  outline: none;

  background: white;

  color: #303133;
}

/* 去掉原生箭头 */

.number-input input::-webkit-outer-spin-button,
.number-input input::-webkit-inner-spin-button {

  -webkit-appearance: none;

  margin: 0;
}

.number-input input[type="number"] {

  -moz-appearance: textfield;
}

/* 按钮 */

.number-btn {

  width: 38px;

  height: 38px;

  border: none;

  background: #eef2f6;

  color: #606266;

  cursor: pointer;

  font-size: 18px;

  transition: 0.2s;
}

.number-btn:hover {

  background: #dbe4ee;

  color: #1677ff;
}

</style>
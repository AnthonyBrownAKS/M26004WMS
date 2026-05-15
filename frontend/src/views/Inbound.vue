<template>

  <div class="inbound-page">

    <!-- 入库列表 -->

    <div class="card table-card">

      <div class="card-title">
        入库任务列表
      </div>

      <!-- 查询 -->

      <div class="search-bar">

        <input
            v-model="query.materialCode"
            placeholder="物料编码"
        >

        <input
            v-model="query.materialName"
            placeholder="物料名称"
        >

        <input
            v-model="query.containerId"
            placeholder="容器ID"
        >

        <input
            v-model="query.batch"
            placeholder="批次"
        >

        <input
            v-model="query.customerCode"
            placeholder="客商编码"
        >

        <button @click="loadInboundList">
          查询
        </button>

      </div>

      <!-- 表格 -->

      <div class="table-wrapper">

        <table class="inventory-table">

          <thead>

          <tr>

            <th>物料名称</th>

            <th>规格</th>

            <th>容器ID</th>

            <th>批次</th>

            <th>客商</th>

            <th>数量</th>

            <th>入库时间</th>

            <th>操作</th>

          </tr>

          </thead>

          <tbody>

          <tr
              v-for="item in inboundList"
              :key="item.id"
          >

<!--            <td>{{ item.materialCode }}</td>-->

            <td>{{ item.materialName }}</td>

            <td>{{ item.spec }}</td>

            <td>{{ item.containerId }}</td>

            <td>{{ item.batch }}</td>

            <td>{{ item.customerCode }}</td>

            <td>{{ item.quantity }}</td>

            <td>{{ formatTime(item.inboundTime) }}</td>

            <td>

              <div class="action-buttons">

                <button
                    class="table-edit-btn"
                    @click="openEdit(item)"
                    @mousedown.prevent
                >
                  修改
                </button>

                <button
                    class="table-delete-btn"
                    @click="openDelete(item)"
                    @mousedown.prevent
                >
                  删除
                </button>

              </div>

            </td>

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
          第 {{ current }} 页
        </span>

        <button
            @click="nextPage"
            :disabled="current >= pages"
        >
          下一页
        </button>

      </div>

    </div>

    <div style="height: 32px"></div>

    <!-- 入库 -->

    <div class="card">

      <div class="card-title">
        入库管理
      </div>

      <div class="form-grid">

        <!-- 物料 -->

        <div class="form-item">

          <label>
            物料编码：
          </label>

          <input
              id="materialInput"
              v-model="form.materialCode"
              placeholder="扫码或输入物料编码"
              @keyup.enter="focusById('quantityInput')"
          />

        </div>

        <!-- 数量 -->

        <div class="form-item">

          <label>
            数量：
          </label>

          <input
              id="quantityInput"
              type="number"
              v-model="form.quantity"
              placeholder="请输入数量"
              @keyup.enter="focusById('containerInput')"
          />

        </div>

        <!-- 容器 -->

        <div class="form-item">

          <label>
            容器ID：
          </label>

          <input
              id="containerInput"
              v-model="form.containerId"
              placeholder="扫码或输入容器ID"
              @keyup.enter="focusById('customerInput')"
          />

        </div>

        <!-- 客商 -->

        <div class="form-item">

          <label>
            客商编码：
          </label>

          <input
              id="customerInput"
              v-model="form.customerCode"
              placeholder="请输入客商编码"
              @keyup.enter="submitInbound"
          />

        </div>

      </div>

      <!-- 按钮 -->

      <div class="button-group">

        <button
            class="scan-btn"
            @click="focusScan"
        >
          扫一扫
        </button>

        <button
            class="submit-btn"
            @click="submitInbound"
        >
          提交入库
        </button>

        <button
            class="reset-btn"
            @click="resetForm"
        >
          重置
        </button>

      </div>

    </div>

    <!-- 修改 -->

    <div
        v-if="editVisible"
        class="dialog-mask"
    >

      <div class="dialog industrial-dialog">

        <!-- 标题 -->

        <div class="dialog-header">

          <div class="dialog-title">
            修改出库记录
          </div>

        </div>

        <!-- 表单 -->

        <div class="dialog-form">

          <!-- 物料名称 -->

          <div class="dialog-item">

            <label>
              物料名称
            </label>

            <input
                v-model="editForm.materialName"
                placeholder="请输入物料名称"
            >

          </div>

          <!-- 规格 -->

          <div class="dialog-item">

            <label>
              规格型号
            </label>

            <input
                v-model="editForm.spec"
                placeholder="请输入规格"
            >

          </div>

          <!-- 批次 -->

          <div class="dialog-item">

            <label>
              批次号
            </label>

            <input
                v-model="editForm.batch"
                placeholder="请输入批次号"
            >

          </div>

          <!-- 客商 -->

          <div class="dialog-item">

            <label>
              客商编码
            </label>

            <input
                v-model="editForm.customerCode"
                placeholder="请输入客商编码"
            >

          </div>

          <!-- 数量 -->

          <div class="dialog-item">

            <label>
              出库数量
            </label>

            <input
                v-model="editForm.quantity"
                placeholder="请输入数量"
            >

          </div>

        </div>

        <!-- 按钮 -->

        <div class="dialog-buttons">

          <button
              class="dialog-confirm-btn"
              @click="submitEdit"
          >
            提交修改
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

    <!-- 删除 -->

    <div
        v-if="deleteVisible"
        class="dialog-mask"
    >

      <div class="dialog">

        <h3>
          确认删除？
        </h3>

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
  ref,
  onMounted,
  nextTick,
  inject
} from 'vue'

const showMessage =
    inject('showMessage')

const inboundList = ref([])

const current = ref(1)

const pages = ref(1)

const size = ref(5)

const editVisible = ref(false)

const deleteVisible = ref(false)

const deleteId = ref(null)

const editForm = ref({})

const query = ref({

  materialCode: '',

  materialName: '',

  containerId: '',

  batch: '',

  customerCode: ''

})

const form = ref({

  materialCode: '',

  quantity: '',

  containerId: '',

  customerCode: ''

})

const loadInboundList = async () => {

  const res = await axios.get(

      '/api/inbound/page',

      {

        params: {

          current: current.value,

          size: size.value,

          ...query.value

        }

      }

  )

  inboundList.value =
      res.data.data.records

  pages.value =
      res.data.data.pages

}

const prevPage = () => {

  if (current.value > 1) {

    current.value--

    loadInboundList()

  }

}

const nextPage = () => {

  if (current.value < pages.value) {

    current.value++

    loadInboundList()

  }

}

const focusById = async (id) => {

  await nextTick()

  document
      .getElementById(id)
      ?.focus()

}

const focusScan = () => {

  focusById('materialInput')
  showMessage('准备扫码','success')

}

const submitInbound = async () => {

  // 非空判断
  if (
      !form.value.materialCode ||
      !form.value.quantity ||
      !form.value.containerId ||
      !form.value.customerCode
  ) {

    showMessage('所有字段不能为空', 'error')
    return

  }

  // 数量必须是数字
  if (isNaN(form.value.quantity)) {

    showMessage('数量必须为数字', 'error')
    return

  }

  // 数量必须大于0
  if (Number.isNaN(Number(form.value.quantity))) {

    showMessage('数量必须大于0', 'error')
    return

  }

  // 容器ID必须以PT开头
  if (!form.value.containerId.startsWith('PT')) {

    showMessage('容器ID必须以PT开头', 'error')
    return

  }

  try {

    await axios.post(

        '/api/task/inbound',

        form.value

    )

    showMessage('入库成功', 'success')

    resetForm()

    loadInboundList()

  } catch (e) {

    showMessage('入库失败', 'error')

  }

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

const resetForm = () => {

  form.value = {

    materialCode: '',

    quantity: '',

    containerId: '',

    customerCode: ''

  }
  // showMessage('重置成功','success')

}

const openEdit = (row) => {

  editForm.value = {

    ...row

  }

  editVisible.value = true

}

const submitEdit = async () => {

  await axios.put(

      '/api/inbound/add',

      editForm.value

  )

  editVisible.value = false
  showMessage('修改成功','success')

  loadInboundList()

}

const openDelete = (row) => {

  deleteId.value = row.id

  deleteVisible.value = true

}

const confirmDelete = async () => {

  await axios.delete(

      `/api/inbound/delete/${deleteId.value}`

  )

  deleteVisible.value = false

  loadInboundList()

}

onMounted(() => {

  loadInboundList()

})

</script>

<style scoped>

.inbound-page {

  padding: 20px;
}

.card {

  background: white;

  border-radius: 10px;

  padding: 24px;

  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.table-card {

  height: 60vh;

  display: flex;

  flex-direction: column;
}

.inventory-table th {

  color: #666;

  font-weight: 700;

  font-size: 14px;

  background: #f5f7fb;
}

.card-title {

  font-size: 18px;

  margin-bottom: 20px;

  font-weight: bold;
}

.search-bar {

  display: grid;

  grid-template-columns: repeat(5, 1fr) auto;

  gap: 14px;

  margin-bottom: 20px;
}

.search-bar input,
.dialog input {

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  outline: none;

  background: #f5f7fb;

  transition: 0.2s;

  color: #333;

  font-size: 14px;
}

.form-item input{
  flex: 1;

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  outline: none;

  background: #f5f7fb;

  transition: 0.2s;

  color: #333;

  font-size: 14px;
}


.search-bar input:focus,
.form-item input:focus,
.dialog input:focus {

  border-color: #409eff;

  background: white;
}

.search-bar input::placeholder,
.form-item input::placeholder,
.dialog input::placeholder {


  color: #b0b3b8;
}

.search-bar button,
.submit-btn,
.reset-btn,
.scan-btn,
.dialog-confirm-btn,
.dialog-cancel-btn {

  min-width: 100px;

  height: 42px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;

  transition: 0.2s;
}

.search-bar button,
.submit-btn,
.dialog-confirm-btn {

  background: #2f8df7;
}

.search-bar button:hover,
.submit-btn:hover,
.dialog-confirm-btn:hover {

  background: #2f8df7;
}

.scan-btn {

  background: #67c23a;
}

.reset-btn,
.dialog-cancel-btn {

  background: #909399;
}

.table-wrapper {

  flex: 1;

  overflow: auto;

  border: 1px solid #ebeef5;

  border-radius: 8px;
}

.inventory-table {

  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

.inventory-table thead {

  background: #f5f7fb;

  position: sticky;

  top: 0;

  z-index: 10;
}

.inventory-table th,
.inventory-table td {

  border-bottom: 1px solid #ebeef5;

  padding: 14px 12px;

  text-align: center;

  font-size: 14px;

  color: #333;
}

.inventory-table tbody tr:hover {

  background: #f8fbff;
}

.pagination {

  height: 70px;

  display: flex;

  justify-content: right;

  align-items: center;

  gap: 20px;

  border-top: 1px solid #ebeef5;

  margin-top: 12px;
}

.pagination button {

  min-width: 90px;

  height: 38px;

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

  background: #c0c4cc;

  cursor: not-allowed;
}

.form-grid {

  display: grid;

  grid-template-columns: repeat(2,1fr);

  gap: 20px;
}

.form-item {

  display: flex;

  align-items: center;
}

.form-item label {

  width: 100px;
}

.button-group {

  margin-top: 30px;

  display: flex;

  justify-content: center;

  gap: 20px;
}

.dialog-mask {

  position: fixed;

  inset: 0;

  background: rgba(0,0,0,0.35);

  display: flex;

  align-items: center;

  justify-content: center;

  z-index: 999;
}

.dialog {

  width: 420px;

  background: white;

  border-radius: 12px;

  padding: 28px;

  display: flex;

  flex-direction: column;

  gap: 18px;
}

.dialog-buttons {

  display: flex;

  justify-content: center;

  gap: 16px;
}

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

  cursor: pointer;
}

.table-edit-btn {

  background: #409eff;
}

.table-delete-btn {

  background: #f56c6c;
}

/* 工业风弹窗 */

.industrial-dialog {

  width: 520px;

  padding: 0;

  overflow: hidden;

  border-radius: 10px;

  background: #ffffff;

  box-shadow:
      0 10px 30px rgba(0,0,0,0.12);
}

/* 顶部 */

.dialog-header {

  height: 58px;

  display: flex;

  align-items: center;

  padding: 0 24px;

  border-bottom: 1px solid #ebeef5;

  background: #f7f9fc;
}

.dialog-title {

  font-size: 17px;

  font-weight: 700;

  color: #303133;

  letter-spacing: 0.5px;
}

/* 表单区域 */

.dialog-form {

  padding: 26px 24px;

  display: flex;

  flex-direction: column;

  gap: 18px;
}

/* 单项 */

.dialog-item {

  display: flex;

  align-items: center;
}

/* label */

.dialog-item label {

  width: 92px;

  flex-shrink: 0;

  font-size: 14px;

  font-weight: 600;

  color: #606266;
}

/* 输入框 */

.dialog-item input {

  flex: 1;

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 14px;

  background: #f5f7fb;

  color: #303133;

  font-size: 14px;

  transition: all 0.2s;
}

.dialog-item input:focus {

  border-color: #409eff;

  background: white;

  box-shadow:
      0 0 0 2px rgba(64,158,255,0.12);
}

/* placeholder */

.dialog-item input::placeholder {

  color: #b5b7bd;
}

/* 底部按钮 */

.dialog-buttons {

  height: 72px;

  display: flex;

  align-items: center;

  justify-content: center;

  gap: 18px;

  border-top: 1px solid #ebeef5;

  background: #fafbfd;
}

/* 按钮统一 */

.dialog-confirm-btn,
.dialog-cancel-btn {

  min-width: 120px;

  height: 40px;

  border: none;

  border-radius: 6px;

  font-size: 14px;

  font-weight: 600;

  cursor: pointer;

  transition: 0.2s;
}

/* 提交 */

.dialog-confirm-btn {

  background: #409eff;

  color: white;
}

.dialog-confirm-btn:hover {

  background: #2f8df7;
}

/* 取消 */

.dialog-cancel-btn {

  background: #909399;

  color: white;
}

.dialog-cancel-btn:hover {

  background: #7d8187;
}

</style>
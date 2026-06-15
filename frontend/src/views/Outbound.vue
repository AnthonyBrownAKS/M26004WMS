<template>

  <div class="outbound-page">

    <!-- 出库记录 -->

    <div class="card table-card">

      <div class="card-title">
        物料出库记录
      </div>

      <!-- 查询区域 -->

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

        <button @click="loadOutboundList">
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

          <th>出库时间</th>

          <th>操作</th>

        </tr>

        </thead>

        <tbody>

        <tr
            v-for="item in outboundList"
            :key="item.id"
        >

          <td>{{ item.materialName }}</td>

          <td>{{ item.spec }}</td>

          <td>{{ item.containerId }}</td>

          <td>{{ item.batch }}</td>

          <td>{{ item.customerCode }}</td>

          <td>{{ item.quantity }}</td>

          <td>{{ formatTime(item.outTime) }}</td>

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

    <!-- 间隔 -->

    <div style="height: 32px"></div>

    <!-- 出库 -->

    <div class="card">

      <div class="card-title">
        物料请求出库
      </div>

      <!-- 表单 -->

      <div class="form-grid">

        <!-- 发货物料 -->

        <div class="form-item">

          <label>
            发货物料：
          </label>

          <div class="custom-select">

            <input
                ref="materialRef"
                v-model="form.materialCode"
                placeholder="请输入或选择物料"
                @focus="handleFocus"
                @input="handleMaterialInput"
                @keyup.enter="focusBatch"
            />

            <!-- 下拉列表 -->

            <div
                v-if="showDropdown"
                class="dropdown"
            >

              <div
                  v-for="item in filteredMaterials"
                  :key="item.code"
                  class="dropdown-item"
                  @mousedown.prevent="selectMaterial(item)"
              >

                <div class="material-code">
                  {{ item.code }}
                </div>

                <div class="material-info">
                  {{ item.name }} / {{ item.spec }}
                </div>

              </div>

            </div>

          </div>

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
              @keyup.enter="focusById('containerIdInput')"
          />

        </div>


        <!-- 容器 -->

        <div class="form-item">

          <label>
            容器ID：
          </label>

          <input
              id="containerIdInput"
              v-model="form.containerId"
              placeholder="请输入容器ID"
              @keyup.enter="focusById('quantityInput')"
          />

        </div>


        <!-- 数量 -->

        <div class="form-item">

          <label>
            数量：
          </label>

          <div class="number-input">

            <button
                type="button"
                class="number-btn"
                @click="form.quantity--"
            >
              -
            </button>

            <input
                id="quantityInput"
                type="number"
                v-model="form.quantity"
                @keyup.enter="focusById('batchInput')"
            >

            <button
                type="button"
                class="number-btn"
                @click="form.quantity++"
            >
              +
            </button>
          </div>
        </div>


        <!-- 批次 -->

        <div class="form-item">
          <label>
            批次号：
          </label>

          <input
              id="batchInput"
              ref="batchRef"
              v-model="form.batch"
              placeholder="请输入批次号"
              @blur="searchInventory"
              @keyup.enter="focusById('quantityInput')"
          />
        </div>
      </div>

      <!-- 按钮 -->

      <div class="button-group">

        <button
            class="scan-btn"
            @click="openScanDialog"
        >
          扫一扫
        </button>

        <button
            ref="submitRef"
            class="submit-btn"
            @click="submitOutbound"
        >
          提交出库
        </button>

        <button
            class="reset-btn"
            @click="resetForm"
        >
          重置
        </button>

      </div>

    </div>

    <!-- 修改弹窗 -->

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


    <!-- 人工扫码 -->

    <div
        v-if="scanVisible"
        class="dialog-mask"
    >

      <div class="dialog scan-dialog">

        <div class="dialog-header">

          <div class="dialog-title">
            人工扫码录入
          </div>

        </div>

        <div class="dialog-form">

          <!-- 物料码 -->

          <div class="dialog-item">

            <label>
              物料码
            </label>

            <input
                v-model="scanMaterial"
                placeholder="
客商代码,物料代码,数量
"
            >

          </div>

          <!-- 容器 -->

          <div class="dialog-item">

            <label>
              容器码
            </label>

            <input
                v-model="scanContainer"
                placeholder="
容器ID
"
            >

          </div>

        </div>

        <div class="dialog-buttons">

          <button
              class="dialog-confirm-btn"
              @click="confirmScan"
          >
            确定
          </button>

          <button
              class="dialog-cancel-btn"
              @click="scanVisible = false"
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
import { onMounted, onUnmounted, nextTick, ref,
  inject } from 'vue'

const showMessage =
    inject('showMessage')

/* refs */

const materialRef = ref()
const batchRef = ref()
const submitRef = ref()

/* 表格 */
const scanVisible = ref(false)

const scanMaterial = ref('')

const scanContainer = ref('')

const outboundList = ref([])

const current = ref(1)

const size = ref(5)

const pages = ref(1)

const showDropdown = ref(false)

const filteredMaterials = ref([])

/* 查询 */

const query = ref({

  materialCode: '',

  materialName: '',

  containerId: '',

  batch: '',

  customerCode: ''

})

const openScanDialog = () => {

  scanMaterial.value = ''

  scanContainer.value = ''

  scanVisible.value = true

}

const handleMaterialInput = (e) => {

  // 如果是程序选择
  // 直接跳过

  if (selecting.value) {

    return

  }

  const keyword =
      e.target.value.toLowerCase()

  form.value.code =
      e.target.value

  filteredMaterials.value =
      materialList.value.filter(item =>

          item.code
              .toLowerCase()
              .includes(keyword)

          ||

          item.name
              .toLowerCase()
              .includes(keyword)

      )

  showDropdown.value = true

}

onUnmounted(() => {

  document.removeEventListener(
      'click',
      handleClickOutside
  )

})

/* 表单 */

const form = ref({

  materialCode: '',

  customerCode: '',

  batch: '',

  quantity: '',

  containerId: ''

})


const confirmScan = async () => {

  try {

    const arr =
        scanMaterial.value.split(',')

    if (arr.length < 3) {

      showMessage(
          '物料码格式错误',
          'error'
      )

      return

    }

    // 客商代码

    form.value.customerCode =
        arr[0].trim()

    // 物料代码

    form.value.materialCode =
        arr[1].trim()

    // 数量

    form.value.quantity =
        Number(arr[2].trim())

    // 容器ID

    form.value.containerId =
        scanContainer.value.trim()

    // 批次号
    const res = await axios.post(
        '/api/outbound/batch',
        {
            materialCode: form.value.materialCode,
            containerId: form.value.containerId
        }
    )

    form.value.batch = res.data.data.batch

    scanVisible.value = false

    showMessage(
        '扫码填充成功',
        'success'
    )

  } catch (e) {

    console.error(e)

    showMessage(
        '未找到对应物料',
        'error'
    )

  }

}


/* 修改 */

const editVisible = ref(false)

const editForm = ref({})

const selecting = ref(false)

/* 删除 */

const deleteVisible = ref(false)

const deleteId = ref(null)

/* 物料 */

const materialList = ref([])

/* 加载列表 */

const loadOutboundList = async () => {

  const res = await axios.get(

      '/api/outbound/page',

      {

        params: {

          current: current.value,

          size: size.value,

          ...query.value

        }

      }

  )

  outboundList.value =
      res.data.data.records

  pages.value =
      res.data.data.pages

}

/* 上一页 */

const prevPage = () => {

  if (current.value > 1) {

    current.value--

    loadOutboundList()

  }

}

const handleFocus = () => {

  filteredMaterials.value =
      materialList.value

  showDropdown.value = true

}

/* 下一页 */

const nextPage = () => {

  if (current.value < pages.value) {

    current.value++

    loadOutboundList()

  }

}

/* 修改 */

const openEdit = (row) => {

  editForm.value = {

    ...row

  }

  editVisible.value = true

}

const submitEdit = async () => {

  await axios.put(

      '/api/outbound/add',

      editForm.value

  )

  editVisible.value = false
  showMessage('修改成功','success')

  loadOutboundList()

}

/* 删除 */

const openDelete = (row) => {

  deleteId.value = row.id

  deleteVisible.value = true


}

const confirmDelete = async () => {

  await axios.delete(

      `/api/outbound/${deleteId.value}`

  )

  showMessage('删除成功','success')

  deleteVisible.value = false

  loadOutboundList()

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

/* 聚焦 */

const focusMaterial = () => {

  materialRef.value.focus()
  showMessage('准备扫码','success')
}


/* 提交 */

const submitOutbound = async () => {

  if (
      !form.value.materialCode ||
      !form.value.quantity ||
      !form.value.containerId ||
      !form.value.customerCode ||
      !form.value.batch
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
        '/api/task/outbound',

        form.value
    )

    showMessage('出库成功', 'success')

    resetForm()

    loadOutboundList()

  } catch (e) {

    showMessage('入库失败', 'error')

  }
}

const selectMaterial = async (item) => {

  selecting.value = true

  // 填充
  form.value.materialCode = item.code
  form.value.containerId = item.containerId
  form.value.batch = item.batch
  form.value.quantity = item.quantity
  form.value.customerCode = item.customerCode

  // 关闭下拉
  showDropdown.value = false

  await nextTick()

  // 恢复状态锁
  setTimeout(() => {

    selecting.value = false

    focusById("batchInput")

  }, 50)

}

const handleClickOutside = (e) => {

  if (!e.target.closest('.custom-select')) {

    showDropdown.value = false

  }

}

const focusById = async (id) => {

  await nextTick()

  document
      .getElementById(id)
      ?.focus()

}

const loadMaterials = async () =>
{ try
{ const res = await axios.get( '/api/material/listC' )
  console.log(res.data)
  materialList.value = res.data.data || []
  filteredMaterials.value = materialList.value
}
catch (e) { console.error(e)
  showMessage('数据获取失败','error') } }
/* 选择物料 */
const handleMaterialSelect = async () => {

  if (!form.value.materialCode) {

    return

  }

  try {

    const res = await axios.get(

        '/api/material/customer',

        {

          params: {

            materialCode:
            form.value.materialCode

          }

        }

    )

    form.value.customerCode =
        res.data.data || ''

  } catch (e) {

    console.error(e)

  }

  form.value.batch = ''

  form.value.quantity = ''

  form.value.containerId = ''

}

const focusBatch = () => {

  setTimeout(() => {

    batchRef.value.focus()

  }, 50)

}



/* 重置 */

const resetForm = () => {

  form.value = {

    materialCode: '',

    customerCode: '',

    batch: '',

    quantity: '',

    containerId: ''

  }
  showMessage('操作成功','success')

}

onMounted(() => {

  loadMaterials()
  loadOutboundList()

  document.addEventListener(
      'click',
      handleClickOutside
  )

})

</script>

<style scoped>

.outbound-page {

  padding: 20px;

  background: #eef2f6;
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

.card-title {

  font-size: 18px;

  font-weight: bold;

  color: #303133;

  margin-bottom: 20px;
}

/* 搜索 */

.search-bar {

  display: grid;

  grid-template-columns: repeat(5, 1fr) auto;

  gap: 14px;

  margin-bottom: 20px;
}

.search-bar input {

  width: 150px;

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

.search-bar input:focus {

  border-color: #409eff;

  background: white;
}

.search-bar input::placeholder {

  color: #b0b3b8;
}

.search-bar button {

  min-width: 100px;

  height: 42px;

  border: none;

  border-radius: 6px;

  background: #409eff;

  color: white;

  font-size: 14px;

  cursor: pointer;

  transition: 0.2s;
}

.search-bar button:hover {

  background: #2f8df7;
}

/* 表格 */

.inventory-table {

  width: 100%;

  border-collapse: collapse;

  table-layout: fixed;
}

button:focus {

  outline: none;
}

.inventory-table thead {

  background: #f5f7fb;

  position: sticky;

  top: 0;

  z-index: 10;
}

.dialog input {

  width: 100%;

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

.dialog input:focus {

  border-color: #409eff;

  background: white;
}

.dialog input::placeholder {

  color: #b0b3b8;
}

.inventory-table th,
.inventory-table td {

  border-bottom: 1px solid #ebeef5;

  padding: 14px 12px;

  text-align: center;

  font-size: 14px;

  color: #333;
}

.inventory-table tbody tr {

  height: 58px;
}

.inventory-table tbody tr:hover {

  background: #f8fbff;
}

/* 分页 */

.pagination {

  height: 70px;

  flex-shrink: 0;

  display: flex;

  align-items: center;

  justify-content: right;

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

/* 表单 */

.form-grid {

  display: grid;

  grid-template-columns: repeat(2, 1fr);

  gap: 20px;
}

.form-item {

  display: flex;

  align-items: center;
}

.form-item label {

  width: 100px;
}

.form-item input,
.form-item select {

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

.form-item input:focus,
.form-item select:focus {

  border-color: #409eff;

  background: white;
}

.form-item input::placeholder {

  color: #b0b3b8;
}

/* 按钮 */

.button-group {

  margin-top: 30px;

  display: flex;

  justify-content: center;

  gap: 20px;
}

.submit-btn,
.reset-btn,
.edit-btn,
.delete-btn,
.scan-btn {

  min-width: 100px;

  height: 42px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;

  transition: 0.2s;
}

.submit-btn {
  background: #409eff;
}

.scan-btn{
  background: #67c23a;
}

.scan-btn:hover{
  background: #67c23a;
}

.submit-btn:hover{
  background: #2f8df7;
}

.table-wrapper {

  flex: 1;

  overflow: auto;

  border: 1px solid #ebeef5;

  border-radius: 8px;
}

.reset-btn {

  background: #909399;
}

.reset-btn:hover {

  background: #7d8187;
}

.delete-btn {

  background: #f56c6c;
}

.delete-btn:hover {

  background: #e45656;
}

.submit-btn,
.edit-btn{

  background: #409eff;
}

@keyframes dialogShow {

  from {

    opacity: 0;

    transform:
        translateY(-8px)
        scale(0.98);
  }

  to {

    opacity: 1;

    transform:
        translateY(0)
        scale(1);
  }
}

.delete-btn,
.reset-btn {

  background: #909399;
}

/* 弹窗 */

.dialog-mask {

  position: fixed;

  inset: 0;

  background: rgba(0,0,0,0.35);

  backdrop-filter: blur(2px);

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

  box-shadow:
      0 8px 24px rgba(0,0,0,0.12);

  display: flex;

  flex-direction: column;

  gap: 18px;

  animation: dialogShow 0.2s ease;
}

.dialog-buttons {

  display: flex;

  justify-content: center;

  gap: 16px;

  margin-top: 8px;
}

.dialog-confirm-btn,
.dialog-cancel-btn {

  min-width: 110px;

  height: 40px;

  border: none;

  border-radius: 6px;

  color: white;

  font-size: 14px;

  cursor: pointer;

  transition: 0.2s;
}

/* 提交 */

.dialog-confirm-btn {

  background: #409eff;
}

.dialog-confirm-btn:hover {

  background: #2f8df7;
}

/* 取消 */

.dialog-cancel-btn {

  background: #909399;
}

.dialog-cancel-btn:hover {

  background: #7d8187;
}

.custom-select {

  position: relative;

  flex: 1;

  height: 42px;
}

.custom-select input {

  width: 100%;

  height: 42px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  outline: none;

  background: #f5f7fb;

  transition: 0.2s;

  color: #333;

  font-size: 14px;

  box-sizing: border-box;
}

.custom-select input:focus {

  border-color: #409eff;

  background: white;
}

/* 下拉 */

.dropdown {

  position: absolute;

  top: calc(100% + 4px);

  left: 0;

  width: 100%;

  background: white;

  border: 1px solid #dcdfe6;

  border-radius: 8px;

  box-shadow: 0 4px 12px rgba(0,0,0,0.08);

  z-index: 999;

  max-height: 260px;

  overflow-y: auto;

  box-sizing: border-box;
}

/* 选项 */

.dropdown-item {

  padding: 10px 12px;

  cursor: pointer;

  transition: 0.2s;

  border-bottom: 1px solid #f2f2f2;
}

.dropdown-item:hover {

  background: #f5f7fb;
}

.material-code {

  font-size: 14px;

  color: #333;

  font-weight: 600;
}

.material-info {

  font-size: 12px;

  color: #999;

  margin-top: 4px;
}
.action-buttons {

  display: flex;

  justify-content: center;

  align-items: center;

  gap: 8px;
}

/* 修改 */

.table-edit-btn {

  min-width: 54px;

  height: 30px;

  border: none;

  border-radius: 5px;

  background: #409eff;

  color: white;

  font-size: 13px;

  cursor: pointer;

  transition: 0.2s;
}

.table-edit-btn:hover {

  background: #2f8df7;
}

/* 删除 */

.table-delete-btn {

  min-width: 54px;

  height: 30px;

  border: none;

  border-radius: 5px;

  background: #f56c6c;

  color: white;

  font-size: 13px;

  cursor: pointer;

  transition: 0.2s;
}

.table-delete-btn:hover {

  background: #e45656;
}

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


.scan-btn:hover {

  opacity: 0.9;
}

.scan-dialog {

  width: 520px;

  height: 280px;

  background: white;

  border-radius: 10px;

  overflow: hidden;
}

.scan-dialog button{

  margin-top: 15px;

}

</style>
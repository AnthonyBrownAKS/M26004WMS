<template>

  <div class="material-page">

    <!-- 表格 -->

    <div class="card table-card">

      <!-- 顶部 -->

      <div class="table-header">

        <div class="card-title">
          客商管理CUSTOMER
        </div>

        <div class="header-actions">

          <button
              class="import-btn"
              @click="triggerImport"
          >
            导入Excel
          </button>

          <button
              class="export-btn"
              @click="exportExcel"
          >
            导出Excel
          </button>

          <button
              class="add-btn"
              @click="openAdd"
          >
            新增客商
          </button>

          <input
              ref="fileInput"
              type="file"
              accept=".xlsx,.xls"
              style="display:none"
              @change="handleImport"
          />

        </div>

      </div>

      <!-- 查询 -->

      <div class="search-bar">

        <input
            v-model="searchCode"
            placeholder="请输入客商编码"
            @keyup.enter="loadList"
            style="color: #2e303a"
        >

        <button @click="loadList">
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

            <th>客商名称</th>

            <th>客商编码</th>

            <th>邮箱</th>

            <th>手机号</th>

            <th>客户</th>

            <th>供应商</th>

            <th>创建时间</th>

            <th>操作</th>

          </tr>

          </thead>

          <tbody>

          <tr
              v-for="item in customerSupplierList"
              :key="item.id"
          >

            <td>{{ item.name }}</td>

            <td>{{ item.code }}</td>

            <td>{{ item.email }}</td>

            <td>{{ item.phone }}</td>

            <td>
              {{ item.isCustomer ? '是' : '否' }}
            </td>

            <td>
              {{ item.isSupplier ? '是' : '否' }}
            </td>

            <td>
              {{ formatTime(item.creationTime) }}
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

    <!-- 新增修改 -->

    <div
        v-if="editVisible"
        class="dialog-mask"
    >

      <div class="dialog industrial-dialog">

        <div class="dialog-header">

          <div class="dialog-title">

            {{ editForm.id ? '修改客商' : '新增客商' }}

          </div>

        </div>

        <div class="dialog-form">

          <div class="dialog-item">

            <label>客商名称</label>

            <input
                v-model="editForm.name"
                placeholder="请输入客商名称"
            >

          </div>

          <div class="dialog-item">

            <label>客商编码</label>

            <input
                v-model="editForm.code"
                placeholder="请输入客商编码"
            >

          </div>

          <div class="dialog-item">

            <label>邮箱</label>

            <input
                v-model="editForm.email"
                placeholder="请输入邮箱"
            >

          </div>

          <div class="dialog-item">

            <label>手机号</label>

            <input
                v-model="editForm.phone"
                placeholder="请输入手机号"
            >

          </div>

          <div class="dialog-item">

            <label>客户</label>

            <select v-model="editForm.isCustomer">

              <option :value="true">
                是
              </option>

              <option :value="false">
                否
              </option>

            </select>

          </div>

          <div class="dialog-item">

            <label>供应商</label>

            <select v-model="editForm.isSupplier">

              <option :value="true">
                是
              </option>

              <option :value="false">
                否
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

    <!-- 删除 -->

    <div
        v-if="deleteVisible"
        class="dialog-mask"
    >

      <div class="dialog delete-dialog">

        <div class="delete-title">
          确认删除该客商？
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

const customerSupplierList = ref([])

const searchCode = ref('')

const current = ref(1)

const size = ref(8)

const pages = ref(1)

const total = ref(0)

const editVisible = ref(false)

const deleteVisible = ref(false)

const deleteId = ref(null)

const fileInput = ref()

const editForm = ref({

  name: '',

  code: '',

  email: '',

  phone: '',

  isCustomer: false,

  isSupplier: false

})

const emptyRows = computed(() => {

  return Math.max(
      0,
      size.value - customerSupplierList.value.length
  )

})

const loadList = async () => {

  try {

    const res = await axios.get(

        '/api/customerSupplier/page',

        {

          params: {

            current: current.value,

            size: size.value,

            code: searchCode.value

          }

        }

    )

    const data = res.data.data

    customerSupplierList.value =
        data.records || []

    pages.value =
        data.pages || 1

    total.value =
        data.total || 0

  } catch (e) {

    console.error(e)

    showMessage('加载失败', 'error')

  }

}

const prevPage = () => {

  if (current.value > 1) {

    current.value--

    loadList()

  }

}

const nextPage = () => {

  if (current.value < pages.value) {

    current.value++

    loadList()

  }

}

const resetSearch = () => {

  searchCode.value = ''

  current.value = 1

  loadList()

}

const openAdd = () => {

  editForm.value = {

    name: '',

    code: '',

    email: '',

    phone: '',

    isCustomer: false,

    isSupplier: false

  }

  editVisible.value = true

}

const openEdit = (row) => {

  editForm.value = {

    ...row

  }

  editVisible.value = true

}

const submitEdit = async () => {

  try {

    const res = await axios.post(

        '/api/customerSupplier/add',

        editForm.value

    )

    if (res.data.code === 200) {

      showMessage('提交成功', 'success')

      editVisible.value = false

      loadList()

    }

  } catch (e) {

    console.error(e)

    showMessage('提交失败', 'error')

  }

}

const openDelete = (row) => {

  deleteId.value = row.id

  deleteVisible.value = true

}

const confirmDelete = async () => {

  try {

    const res = await axios.delete(

        `/api/customerSupplier/${deleteId.value}`

    )

    if (res.data.code === 200) {

      showMessage('删除成功', 'success')

      deleteVisible.value = false

      loadList()

    }

  } catch (e) {

    console.error(e)

    showMessage('删除失败', 'error')

  }

}

const triggerImport = () => {

  fileInput.value.click()

}

const handleImport = async (e) => {

  const file = e.target.files[0]

  if (!file) {

    return

  }

  const formData = new FormData()

  formData.append(
      'file',
      file
  )

  try {

    const res = await axios.post(

        '/api/customerSupplier/import',
        formData
    )

    if (res.data.code === 200) {

      showMessage('导入成功', 'success')

      loadList()

    }

  } catch (e) {

    console.error(e)

    showMessage('导入失败', 'error')

  }

}

const exportExcel = async () => {

  try {

    const res = await axios.get(

        '/api/customerSupplier/exportAll',

        {

          responseType: 'blob'

        }

    )

    const blob = new Blob(

        [res.data],

        {

          type:
              'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'

        }

    )

    const url =
        window.URL.createObjectURL(blob)

    const link =
        document.createElement('a')

    link.href = url

    link.download = '客商数据.xlsx'

    document.body.appendChild(link)

    link.click()

    document.body.removeChild(link)

    window.URL.revokeObjectURL(url)

  } catch (e) {

    console.error(e)

    showMessage('导出失败', 'error')

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

  loadList()

})

</script>

<style scoped>

.material-page {

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

  color: #303133;
}

.header-actions {

  display: flex;

  gap: 12px;
}

/* 按钮 */

.import-btn,
.export-btn,
.add-btn {

  min-width: 110px;

  height: 40px;

  border: none;

  border-radius: 6px;

  color: white;

  cursor: pointer;

  font-size: 14px;

  transition: 0.2s;
}

.import-btn {

  background: #67c23a;
}

.export-btn {

  background: #e6a23c;
}

.add-btn {

  background: #1677ff;
}

/* 查询 */

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

/* 表格 */

.table-wrapper {

  overflow: auto;

  border: 1px solid #ebeef5;

  border-radius: 8px;
}

.inventory-table {

  width: 100%;

  table-layout: fixed;

  border-collapse: collapse;
}

.inventory-table thead {

  background: #f5f7fb;
}

.inventory-table th,
.inventory-table td {

  height: 56px;

  border-bottom: 1px solid #ebeef5;

  text-align: center;

  font-size: 14px;

  color: #333;

  white-space: nowrap;

  overflow: hidden;

  text-overflow: ellipsis;
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

  background: #1677ff;
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

  background: #1677ff;

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

  background: #1677ff;
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

  font-size: 16px;

  margin-bottom: 24px;
}

</style>
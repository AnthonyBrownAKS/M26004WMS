<template>

  <div class="material-page">

    <!-- 表格 -->

    <div class="card table-card">

      <!-- 顶部 -->

      <div class="table-header">

        <div class="card-title">
          物料管理
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
            新增物料
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
            placeholder="请输入物料编码"
            @keyup.enter="loadMaterialList"
            style="color: #2e303a"
        >

        <button @click="loadMaterialList">
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

            <th>物料编码</th>

            <th>物料名称</th>

            <th>规格</th>

            <th>唯一物料</th>

            <th>有效期</th>

            <th>ABC分类</th>

            <th>安全库存</th>

            <th>创建时间</th>

            <th>操作</th>

          </tr>

          </thead>

          <tbody>

          <tr
              v-for="item in materialList"
              :key="item.id"
          >

            <td>{{ item.code }}</td>

            <td>{{ item.name }}</td>

            <td>{{ item.spec }}</td>

            <td>
              {{ item.isUnique ? '是' : '否' }}
            </td>

            <td>
              {{ item.validityDate }}
            </td>

            <td>
              {{ item.abcType }}
            </td>

            <td>
              {{ item.safetyStock }}
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

          <!-- 占位行 -->

          <tr
              v-for="n in emptyRows"
              :key="'empty-' + n"
          >

            <td colspan="9"></td>

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

    <!-- 新增/修改弹窗 -->

    <div
        v-if="editVisible"
        class="dialog-mask"
    >

      <div class="dialog industrial-dialog">

        <!-- 标题 -->

        <div class="dialog-header">

          <div class="dialog-title">

            {{ editForm.id ? '修改物料' : '新增物料' }}

          </div>

        </div>

        <!-- 表单 -->

        <div class="dialog-form">

          <div class="dialog-item">

            <label>
              物料编码
            </label>

            <input
                v-model="editForm.code"
                placeholder="请输入物料编码"
            >

          </div>

          <div class="dialog-item">

            <label>
              物料名称
            </label>

            <input
                v-model="editForm.name"
                placeholder="请输入物料名称"
            >

          </div>

          <div class="dialog-item">

            <label>
              规格型号
            </label>

            <input
                v-model="editForm.spec"
                placeholder="请输入规格型号"
            >

          </div>

          <div class="dialog-item">

            <label>
              唯一物料
            </label>

            <select v-model="editForm.isUnique">

              <option :value="true">
                是
              </option>

              <option :value="false">
                否
              </option>

            </select>

          </div>

          <div class="dialog-item">

            <label>
              有效期
            </label>

            <input
                type="number"
                v-model="editForm.validityDate"
                placeholder="请输入有效期"
            >

          </div>

          <div class="dialog-item">

            <label>
              ABC分类
            </label>

            <input
                v-model="editForm.abcType"
                placeholder="请输入ABC分类"
            >

          </div>

          <div class="dialog-item">

            <label>
              安全库存
            </label>

            <input
                type="number"
                v-model="editForm.safetyStock"
                placeholder="请输入安全库存"
            >

          </div>

        </div>

        <!-- 按钮 -->

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
          确认删除该物料？
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

const materialList = ref([])

const searchCode = ref('')

/* 分页 */

const current = ref(1)

const size = ref(8)

const pages = ref(1)

const total = ref(0)

/* 弹窗 */

const editVisible = ref(false)

const deleteVisible = ref(false)

const deleteId = ref(null)

const fileInput = ref()

/* 表单 */

const editForm = ref({

  code: '',

  name: '',

  spec: '',

  isUnique: false,

  validityDate: null,

  abcType: '',

  safetyStock: null

})

/* 空行 */

const emptyRows = computed(() => {

  return Math.max(
      0,
      size.value - materialList.value.length
  )

})

/* 加载 */

const loadMaterialList = async () => {

  try {

    const res = await axios.get(

        '/api/material/page',

        {

          params: {

            current: current.value,

            size: size.value,

            code: searchCode.value

          }

        }

    )

    const data = res.data.data

    materialList.value =
        data.records || []

    pages.value =
        data.pages || 1

    total.value =
        data.total || 0

  } catch (e) {

    console.error(e)

    showMessage('物料加载失败','error')

  }

}

/* 翻页 */

const prevPage = () => {

  if (current.value > 1) {

    current.value--

    loadMaterialList()

  }

}

const nextPage = () => {

  if (current.value < pages.value) {

    current.value++

    loadMaterialList()

  }

}

/* 重置搜索 */

const resetSearch = () => {

  searchCode.value = ''

  current.value = 1

  loadMaterialList()

}

/* 新增 */

const openAdd = () => {

  editForm.value = {

    code: '',

    name: '',

    spec: '',

    isUnique: false,

    validityDate: null,

    abcType: '',

    safetyStock: null

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

  try {

    editForm.value.validityDate =
        Number(editForm.value.validityDate)

    editForm.value.safetyStock =
        Number(editForm.value.safetyStock)

    const res = await axios.post(

        '/api/material/add',

        editForm.value

    )

    if (res.data.code === 200) {

      showMessage('修改成功','success')

      editVisible.value = false

      loadMaterialList()

    } else {

      showMessage('修改失败','error')

    }

  } catch (e) {

    console.error(e)

    showMessage('服务器异常','error')

  }

}

/* 删除 */

const openDelete = (row) => {

  deleteId.value = row.id

  deleteVisible.value = true

}

const confirmDelete = async () => {

  try {

    const res = await axios.delete(

        `/api/material/${deleteId.value}`

    )

    if (res.data.code === 200) {

      showMessage('删除成功','success')

      deleteVisible.value = false

      loadMaterialList()

    }

  } catch (e) {

    console.error(e)

    showMessage('删除失败','error')

  }

}

/* 导入 */

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

        '/api/material/import',

        formData

    )

    if (res.data.code === 200) {

      showMessage('导入成功','success')

      loadMaterialList()

    }

  } catch (e) {

    console.error(e)

    showMessage('导入失败','error')

  }

}

/* 导出 */

const exportExcel = async () => {

  try {

    const res = await axios.get(

        '/api/material/exportAll',

        {

          responseType: 'blob'

        }

    )

    showMessage('正在导出数据...','success')

    // 创建 blob
    const blob = new Blob(

        [res.data],

        {

          type:
              'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'

        }

    )

    // 创建下载链接
    const url =
        window.URL.createObjectURL(blob)

    // 创建a标签
    const link =
        document.createElement('a')

    link.href = url

    link.download = '物料数据.xlsx'

    document.body.appendChild(link)

    link.click()

    document.body.removeChild(link)

    // 释放
    window.URL.revokeObjectURL(url)

  } catch (e) {

    console.error(e)

    showMessage('导出失败','error')

  }

}

/* 时间 */

const formatTime = (time) => {

  if (!time) {

    return ''
  }

  return time
      .replace('T', ' ')
      .substring(0, 19)

}

onMounted(() => {

  loadMaterialList()

})

</script>

<style scoped>

.material-page {

  padding: 20px;
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
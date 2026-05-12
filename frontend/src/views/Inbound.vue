<template>

  <div class="inbound-page">

    <!-- 入库记录 -->

    <div class="card">

      <div class="card-title">
        入库记录
      </div>

      <table class="inventory-table">

        <thead>

        <tr>

          <th>物料编码</th>

          <th>容器ID</th>

          <th>批次号</th>

          <th>供应商</th>

          <th>数量</th>

          <th>任务创建时间</th>

        </tr>

        </thead>

        <tbody>

        <tr
            v-for="item in inboundList"
            :key="item.id"
        >

          <td>{{ item.materialCode }}</td>

          <td>{{ item.containerId }}</td>

          <td>{{ item.batch }}</td>

          <td>{{ item.customerCode }}</td>

          <td>{{ item.quantity }}</td>

          <td>{{ formatTime(item.creationTime) }}</td>

        </tr>

        <!-- 补空行 -->

        <tr
            v-for="n in (5 - inboundList.length)"
            :key="'empty-' + n"
        >

          <td>&nbsp;</td>
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

    <!-- 入库表单 -->

    <div class="card form-card">

      <div class="card-title">
        入库信息
      </div>

      <div class="form-grid">

        <!-- 物料编码 -->

        <div class="form-item">

          <label>
            物料编码：
          </label>

          <input
              id="materialInput"
              v-model="form.materialCode"
              placeholder="扫码或输入物料编码"
              @keydown.enter.prevent="
                focusById('quantityInput')
              "
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
              @keydown.enter.prevent="
                focusById('containerInput')
              "
          />

        </div>

        <!-- 容器 -->

        <div class="form-item">

          <label>
            容器ID：
          </label>

          <input
              id="containerInput"
              :value="form.containerId"
              @input="form.containerId = $event.target.value.toUpperCase()"
              placeholder="扫码或输入容器ID"
              @keydown.enter.prevent="
                focusById('customerInput')
              "
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
              @keydown.enter.prevent="
                submitInbound
              "
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
            class="reset-btn"
            @click="resetForm"
        >
          重置
        </button>

        <button
            class="submit-btn"
            @click="submitInbound"
        >
          提交入库
        </button>

      </div>

    </div>

  </div>

</template>

<script setup>

import axios from 'axios'

import {
  onMounted,
  ref,
  nextTick,
  inject
} from 'vue'

/* 入库记录 */

const inboundList = ref([])

const current = ref(1)

const pages = ref(1)

const total = ref(0)

const size = ref(5)

// 提示弹窗
const showMessage =
    inject('showMessage')

/* 表单 */

const form = ref({

  materialCode: '',

  quantity: '',

  containerId: '',

  customerCode: ''

})

/* 聚焦指定ID */

const focusById = async (id) => {

  await nextTick()

  const el =
      document.getElementById(id)

  if (el) {

    el.focus()

    el.select?.()

  }

}

/* 扫一扫 */

const focusScan = () => {

  focusById('materialInput')

}

/* 加载记录 */

const loadInboundList = async () => {

  try {

    const res = await axios.get(

        '/api/material/inbound',

        {

          params: {

            current: current.value,

            size: size.value

          }

        }

    )

    const data = res.data.data

    inboundList.value =
        data || []

    current.value =
        data.current || 1

    pages.value =
        data.pages || 1

    total.value =
        data.total || 0

  } catch (e) {

    console.error(e)

    showMessage(
        '入库记录加载失败',
        'error'
    )

  }

}

/* 翻页 */

const changePage = (page) => {

  current.value = page

  loadInboundList()

}

/* 时间格式 */

const formatTime = (time) => {

  if (!time) {

    return ''
  }

  return time
      .replace('T', ' ')
      .substring(0, 19)

}

/* 提交 */

const submitInbound = async () => {

  /* 去除空格 */

  const materialCode =
      form.value.materialCode.trim()

  const quantity =
      String(form.value.quantity).trim()

  const containerId =
      form.value.containerId.trim()

  const customerCode =
      form.value.customerCode.trim()

  /* 非空判断 */

  if (
      !materialCode ||
      !quantity ||
      !containerId ||
      !customerCode
  ) {

    showMessage(
        '所有字段不能为空',
        'error'
    )

    return

  }

  /* 容器ID判断 */

  if (!containerId.startsWith('PT')) {

    showMessage(
        '容器ID必须以PT开头',
        'error'
    )

    focusById('containerInput')

    return

  }

  /* 数量判断 */

  const num = Number(quantity)

  if (
      isNaN(num) ||
      num <= 0
  ) {

    showMessage(
        '数量必须为正数字',
        'error'
    )

    focusById('quantityInput')

    return

  }

  try {

    const res = await axios.post(

        '/api/task/inbound',

        {

          materialCode,

          quantity: num,

          containerId,

          customerCode

        }

    )

    if (res.data.code === 200) {

      showMessage(
          '入库成功',
          'success'
      )

      resetForm()

      loadInboundList()

      focusScan()

    } else {

      showMessage(
          '提交失败',
          'error'
      )

    }

  } catch (e) {

    console.error(e)

    showMessage(
        '服务器异常',
        'error'
    )

  }

}

/* 重置 */

const resetForm = () => {

  form.value = {

    materialCode: '',

    quantity: '',

    containerId: '',

    customerCode: ''

  }

}

/* 初始化 */

onMounted(() => {

  loadInboundList()

  focusScan()

})

</script>

<style scoped>

.inbound-page {

  display: flex;

  flex-direction: column;

  gap: 20px;
}

/* 卡片 */

.card {

  background: white;

  border-radius: 10px;

  padding: 20px;

  box-shadow:
      0 2px 8px rgba(0,0,0,0.05);
}

.card-title {

  font-size: 18px;

  font-weight: bold;

  margin-bottom: 20px;

  color: #333;
}

/* 表格 */

.inventory-table {

  width: 100%;

  border-collapse: collapse;
}

.inventory-table thead {

  background: #f5f7fa;
}

.inventory-table th {

  height: 48px;

  color: #666;

  font-weight: bold;

  border-bottom:
      1px solid #ebeef5;
}

.inventory-table tbody {

  height: 320px;
}

.inventory-table td {

  height: 52px;

  text-align: center;

  border-bottom:
      1px solid #ebeef5;

  color: #555;
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
}

.pagination button:disabled {

  background: #ccc;

  cursor: not-allowed;
}

/* 表单 */

.form-card {

  padding-bottom: 30px;
}

.form-grid {

  display: grid;

  grid-template-columns:
      repeat(2, 1fr);

  gap: 20px 40px;
}

.form-item {

  display: flex;

  align-items: center;
}

.form-item label {

  width: 100px;

  color: #666;

  font-size: 14px;
}

.form-item input {

  flex: 1;

  height: 42px;

  border:
      1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  outline: none;

  background: #f5f7fb;

  color: #2e303a;

  transition: 0.2s;
}

.form-item input:focus {

  border-color: #409eff;

  background: white;
}

/* 按钮 */

.button-group {

  margin-top: 30px;

  display: flex;

  justify-content: center;

  gap: 20px;
}

.scan-btn,
.submit-btn,
.reset-btn {

  min-width: 120px;

  height: 42px;

  border: none;

  border-radius: 6px;

  cursor: pointer;

  color: white;

  font-size: 15px;

  transition: 0.2s;
}

.scan-btn {

  background: #67c23a;
}

.scan-btn:hover {

  background: #5daf34;
}

.submit-btn {

  background: #409eff;
}

.submit-btn:hover {

  background: #2f8df7;
}

.reset-btn {

  background: #909399;
}

.reset-btn:hover {

  background: #7d8187;
}

</style>
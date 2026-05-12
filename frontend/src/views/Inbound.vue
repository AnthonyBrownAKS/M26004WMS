<template>

  <div class="inbound-page">

    <!-- 库存列表 -->

    <div class="card">

      <div class="card-title">
        库存列表
      </div>

      <table class="inventory-table">

        <thead>

        <tr>
          <th>库区</th>
          <th>排</th>
          <th>列</th>
          <th>容器ID</th>
          <th>创建时间</th>
        </tr>

        </thead>

        <tbody>

        <!-- 数据行 -->

        <tr
            v-for="item in inventoryList"
            :key="item.containerId"
        >
          <td>{{ item.locationAreaId }}</td>

          <td>{{ item.rowNo }}</td>

          <td>{{ item.columnNo }}</td>

          <td>{{ item.containerId }}</td>

          <td>{{ formatTime(item.creationTime) }}</td>
        </tr>

        <!-- 补空行 -->

        <tr
            v-for="n in (5 - inventoryList.length)"
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

        <div class="form-item">

          <label>
            物料编码：
          </label>

          <input
              v-model="form.materialCode"
              placeholder="请输入物料编码"
              style="color: #2e303a"
          />

        </div>

        <div class="form-item">

          <label>
            数量：
          </label>

          <input
              type="number"
              v-model="form.quantity"
              placeholder="请输入数量"
              style="color: #2e303a"
          />

        </div>

        <div class="form-item">

          <label>
            容器ID：
          </label>

          <input
              v-model="form.containerId"
              placeholder="请输入容器ID"
              style="color: #2e303a"
          />

        </div>

        <div class="form-item">

          <label>
            客商编码：
          </label>

          <input
              v-model="form.customerCode"
              placeholder="请输入客商编码"
              style="color: #2e303a"
          />

        </div>

      </div>

      <!-- 按钮 -->

      <div class="button-group">

        <button
            class="scan-btn"
            @click="goScanPage"
        >
          扫一扫
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

/* 库存数据 */

const inventoryList = ref([])

const current = ref(1)

const pages = ref(1)

const total = ref(0)

const size = ref(5)

/* 表单 */

const form = ref({

  materialCode: '',

  quantity: 0,

  containerId: '',

  customerCode: ''

})

/* 加载库存分页 */

const loadInventory = async () => {

  try {

    const res = await axios.get(

        '/api/inventory/pages',

        {

          params: {

            current: current.value,

            size: size.value

          }

        }

    )

    console.log(res.data)

    inventoryList.value = res.data.records || []

    current.value = res.data.current || 1

    pages.value = res.data.pages || 1

    total.value = res.data.total || 0

  } catch (e) {

    console.error(e)

    alert('库存数据加载失败')

  }

}

/* 翻页 */

const changePage = (page) => {

  current.value = page

  loadInventory()

}

/* 去扫一扫页面 */

const goScanPage = () => {

  router.push('/scan')

}

/* 规范时间格式 */
const formatTime = (time) => {

  if (!time) {

    return ''
  }

  return time
      .replace('T', ' ')
      .substring(0, 19)
}

/* 提交入库 */

const submitInbound = async () => {

  try {

    const res = await axios.post(

        '/api/task/inbound',

        {

          materialCode: form.value.materialCode,

          quantity: form.value.quantity,

          containerId: form.value.containerId,

          customerCode: form.value.customerCode

        }

    )

    console.log(res.data)

    if (res.data.code === 200) {

      alert('入库任务创建成功')

      loadInventory()

    } else {

      alert(res.data.message || '入库失败')

    }

  } catch (e) {

    console.error(e)

    alert('服务器异常')

  }

}

/* 初始化 */

onMounted(() => {

  loadInventory()

  /* 接收扫一扫页面返回数据 */

  const scanData = sessionStorage.getItem('scanData')

  if (scanData) {

    form.value = JSON.parse(scanData)

    sessionStorage.removeItem('scanData')

  }

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

  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
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

  border-bottom: 1px solid #ebeef5;
}

.inventory-table tbody {

  height: 350px;
}

.inventory-table td {

  height: 50px;

  text-align: center;

  border-bottom: 1px solid #ebeef5;

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

  transition: 0.2s;
}

.pagination button:hover {

  background: #2f8df7;
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

  grid-template-columns: repeat(2, 1fr);

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

  height: 40px;

  border: 1px solid #dcdfe6;

  border-radius: 6px;

  padding: 0 12px;

  outline: none;

  background: #f5f7fb;

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
.submit-btn {

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

</style>
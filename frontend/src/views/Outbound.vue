<template>

  <div class="outbound-page">

    <div class="card">

      <!-- 标题 -->

      <div class="card-title">
        出库管理
      </div>

      <!-- 表单 -->

      <div class="form-grid">

        <!-- 物料 -->

        <div class="form-item">

          <label>
            发货物料：
          </label>

          <select
              v-model="form.materialCode"
              @change="handleMaterialChange"
          >

            <option value="">
              请选择物料
            </option>

            <option
                v-for="item in materialList"
                :key="item.materialCode"
                :value="item.materialCode"
            >
              {{ item.materialCode }}
              ({{ item.name }})
              / {{ item.spec }}
            </option>

          </select>

        </div>

        <!-- 客商 -->

        <div class="form-item">

          <label>
            客商编码：
          </label>

          <input
              v-model="form.customerCode"
              readonly
              placeholder="自动填充"
          />

        </div>

        <!-- 批次 -->

        <div class="form-item">

          <label>
            批次号：
          </label>

          <input
              v-model="form.batch"
              placeholder="请输入批次号"
              @blur="searchInventory"
          />

        </div>

        <!-- 数量 -->

        <div class="form-item">

          <label>
            数量：
          </label>

          <input
              v-model="form.quantity"
              readonly
              placeholder="自动填充"
          />

        </div>

        <!-- 容器 -->

        <div class="form-item">

          <label>
            容器ID：
          </label>

          <input
              v-model="form.containerId"
              readonly
              placeholder="自动填充"
          />

        </div>

      </div>

      <!-- 按钮 -->

      <div class="button-group">

        <button
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

  </div>

</template>

<script setup>

import axios from 'axios'
import { onMounted, ref } from 'vue'

/* 物料列表 */

const materialList = ref([])

/* 表单 */

const form = ref({

  materialCode: '',

  customerCode: '',

  batch: '',

  quantity: '',

  containerId: ''

})

/* 加载物料 */

const loadMaterials = async () => {

  try {

    const res = await axios.get(
        '/api/material/list'
    )

    console.log(res.data)

    materialList.value =
        res.data.data || []

  } catch (e) {

    console.error(e)

    alert('物料数据加载失败')

  }

}

/* 选择物料 */

const handleMaterialChange = async () => {

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

    alert('客商查询失败')

  }

  /* 清空库存信息 */

  form.value.batch = ''

  form.value.quantity = ''

  form.value.containerId = ''

}

/* 搜索库存 */

const searchInventory = async () => {

  if (!form.value.materialCode
      || !form.value.batch) {

    return
  }

  try {

    const res = await axios.get(

        '/api/inventory/search',

        {

          params: {

            materialCode:
            form.value.materialCode,

            batch:
            form.value.batch

          }

        }

    )

    console.log(res.data)

    const inventory =
        res.data.data

    if (!inventory) {

      alert('未找到库存')

      return
    }

    form.value.quantity =
        inventory.quantity

    form.value.containerId =
        inventory.containerId

  } catch (e) {

    console.error(e)

    alert('库存查询失败')

  }

}

/* 提交出库 */

const submitOutbound = async () => {

  if (!form.value.materialCode
      || !form.value.batch
      || !form.value.containerId) {

    alert('请完善出库信息')

    return
  }

  try {

    const res = await axios.post(

        '/api/task/outbound',

        {

          materialCode:
          form.value.materialCode,

          batch:
          form.value.batch,

          containerId:
          form.value.containerId

        }

    )

    console.log(res.data)

    if (res.data.code === 200) {

      alert('出库任务创建成功')

      resetForm()

    } else {

      alert(
          res.data.message || '出库失败'
      )

    }

  } catch (e) {

    console.error(e)

    alert('服务器异常')

  }

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

}

/* 初始化 */

onMounted(() => {

  loadMaterials()

})

</script>

<style scoped>

.outbound-page {

  display: flex;

  flex-direction: column;
}

/* 卡片 */

.card {

  background: white;

  border-radius: 10px;

  padding: 24px;

  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.card-title {

  font-size: 18px;

  font-weight: bold;

  margin-bottom: 28px;

  color: #333;
}

/* 表单 */

.form-grid {

  display: grid;

  grid-template-columns: repeat(2, 1fr);

  gap: 24px 40px;
}

.form-item {

  display: flex;

  align-items: center;
}

.form-item label {

  width: 110px;

  color: #666;

  font-size: 14px;
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
}

.form-item input:focus,
.form-item select:focus {

  border-color: #409eff;

  background: white;
}

/* 按钮 */

.button-group {

  margin-top: 40px;

  display: flex;

  justify-content: center;

  gap: 20px;
}

.submit-btn,
.reset-btn {

  min-width: 120px;

  height: 42px;

  border: none;

  border-radius: 6px;

  color: white;

  font-size: 15px;

  cursor: pointer;

  transition: 0.2s;
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
<template>
  <div class="page">

    <div class="card">

      <!-- 顶部 -->
      <div class="top-bar">

        <div class="title">库位平面图</div>

        <!-- 图例 -->
        <div class="legend">

          <div class="item">
            <span class="dot green"></span> 空库位
          </div>

          <div class="item">
            <span class="dot blue"></span> 占用
          </div>

          <div class="item">
            <span class="dot red"></span> 锁定
          </div>

        </div>

      </div>

      <!-- 列头（关键：grid统一） -->
      <div class="col-header">

        <div class="row-label"></div>

        <div
            v-for="c in maxCol"
            :key="c"
            class="col"
        >
          C{{ c }}
        </div>

      </div>

      <!-- 仓库主体 -->
      <div class="warehouse">

        <div
            v-for="row in [1,2]"
            :key="row"
            class="row"
        >

          <!-- 行标 -->
          <div class="row-label">
            R{{ row }}
          </div>

          <!-- 列 -->
          <div
              v-for="col in getMaxCol(row)"
              :key="col"
              class="col-box"
          >

            <!-- ⭐层：从下往上 -->
            <div class="layers">

              <div
                  v-for="item in getCells(row, col)"
                  :key="item.id"
                  class="cell"
                  :class="getColor(item)"
                  @click="handleClick(item)"
              >
                <span v-if="getFixedLabel(item.row, item.column, item.layer)">
  {{
                    getFixedLabel(item.row, item.column, item.layer) === 'OUTBOUND'
                        ? '入库'
                        : '出库'
                  }}
</span>

                <span v-else>
  L{{ item.layer }}
</span>
              </div>

            </div>

          </div>

        </div>

      </div>

    </div>

    <!-- 弹窗 -->
    <div v-if="showModal" class="mask" @click="showModal=false">

      <div class="modal" @click.stop>

        <!-- 标题 -->
        <div class="m-header">
          <div class="m-title">
            物料信息
          </div>

          <div class="m-sub">
            容器码：{{ barcode }}
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="materials.length === 0" class="empty">

          <div class="empty-icon">📦</div>

          <div class="empty-text">
            暂无物料
          </div>

        </div>

        <!-- 物料列表 -->
        <div v-else class="list">

          <div
              v-for="(m,i) in materials"
              :key="i"
              class="item-card"
          >

            <div class="item-top">
              <span class="index">#{{ i+1 }}</span>
              <span class="code">{{ m.materialCode }}</span>
            </div>

            <div class="item-body">
              <div>名称：{{ m.materialName }}</div>
              <div>规格：{{ m.spec }}</div>
              <div>客户：{{ m.customerCode }}</div>
            </div>

            <div class="item-bottom">
              <span>数量：{{ m.quantity }}</span>
              <span>批次：{{ m.batch }}</span>
            </div>

          </div>

        </div>

      </div>

    </div>

    <!-- 确认弹窗 -->
    <div v-if="confirmVisible" class="mask">

      <div class="confirm-box">

        <div class="confirm-title">
          ⚠️ 确认锁定库位？
        </div>

        <div class="confirm-text">
          该操作将把库位设置为 "禁用状态"
        </div>

        <div class="confirm-btns">

          <button class="ok" @click="confirmLock">
            确认
          </button>

          <button class="cancel" @click="cancelLock">
            取消
          </button>

        </div>

      </div>

    </div>

  </div>
</template>

<script setup>
import {onMounted, ref, inject} from "vue";
import axios from "axios";

const showMessage =
    inject('showMessage')

/* ================= 数据 ================= */
const locations = ref([])

const showModal = ref(false)
const materials = ref([])
const barcode = ref("")

const maxCol = 18

/* ================= 加载 ================= */
const load = async () => {
  const res = await axios.get("/api/location/list")
  locations.value = res.data.data
}

/* ================= 工具：获取某格 ================= */
const getCells = (row, col) => {

  return locations.value
      .filter(i =>
          i.row === row &&
          i.column === col
      )
      .sort((a,b) => a.layer - b.layer) // ⭐从下往上
}

/* ================= 列数 ================= */
const getMaxCol = (row) => row === 1 ? 18 : 18

const confirmVisible = ref(false)
const targetItem = ref(null)

const fixedLabels = {
  "1-3-1": "OUTBOUND", // R1 C3 L1 出库
  "1-4-1": "INBOUND"   // R1 C4 L1 入库
}

const getFixedLabel = (row, col, layer) => {
  const key = `${row}-${col}-${layer}`
  return fixedLabels[key]
}



/* ================= 颜色 ================= */
const getColor = (item) => {

  const label = getFixedLabel(item.row, item.column, item.layer)

  if (label === "OUTBOUND") return "orange"
  if (label === "INBOUND") return "orange"

  if (item.lockState === "INBOUND_LOCK") {
    return "red"
  }

  if (item.cargoStatus === "FULL") {
    return "blue"
  }

  return "green"
}

/* ================= 点击 ================= */
const handleClick = async (item) => {

  const type = getColor(item)

  if (type === "orange"){
    showMessage('库位不允许操作', 'error')
  }

  // 🟩 空
  if (type === "green") {

    targetItem.value = item
    confirmVisible.value = true


    return
  }

  // 🟥 解锁
  if (type === "red") {
    await axios.post(`/api/location/${item.id}`, {
      lockState: null
    })
    item.lockState = null
    showMessage('库位已解锁', 'success')
  }

  // 🟦 查看物料
  if (type === "blue") {

    barcode.value = item.lockContainerBarcode

    const res = await axios.get(
        `/api/inventory/list/${item.lockContainerBarcode}`
    )

    document.activeElement?.blur()

    materials.value = res.data.data || []
    showModal.value = true

  }
}

const confirmLock = async () => {

  const item = targetItem.value

  if (!item) return

  await axios.post(`/api/location/${item.id}`, {
    lockState: "INBOUND_LOCK"
  })

  item.lockState = "INBOUND_LOCK"

  showMessage('库位已锁定', 'success')
  confirmVisible.value = false
  targetItem.value = null
}

const cancelLock = () => {
  confirmVisible.value = false
  targetItem.value = null
}

onMounted(load)
</script>

<style scoped>

/* ================= 页面 ================= */
.page {
  padding: 20px;
  background: #eef2f6;
}

/* ================= 卡片 ================= */
.card {
  background: white;
  padding: 20px;
  border-radius: 12px;
}

/* ================= 顶部 ================= */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

/* ================= 图例 ================= */
.legend {
  display: flex;
  gap: 16px;
  font-size: 13px;
}

.item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.green { background: #4caf50; }
.blue  { background: #2196f3; }
.red   { background: #f44336; }

/* ================= 列头（关键修复对齐） ================= */
.col-header {
  display: grid;
  grid-template-columns: 70px repeat(18, 55px);
  margin-bottom: 10px;
  margin-top: 30px;
}

.col {
  text-align: center;
  font-size: 12px;
  color: #666;
}

/* ================= 仓库主体 ================= */
.warehouse {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* 行（关键：grid统一） */
.row {
  display: grid;
  grid-template-columns: 70px repeat(18, 55px);
  align-items: start;
}

/* 行标 */
.row-label {
  font-weight: bold;
  color: #333;
}

/* ================= 每列 ================= */
.col-box {
  width: 50px;
}

/* ================= 层（关键优化） ================= */
.layers {
  display: flex;
  flex-direction: column-reverse; /* 从下往上 */
  gap: 5px;

}

/* ================= 库位 ================= */
.cell {
  user-select: none;
  -webkit-user-drag: none;
  outline: none;
  pointer-events: auto;
  height: 24px;
  border-radius: 5px;
  font-size: 10px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: 0.15s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
}

/* hover优化 */
/*
.cell:hover {
  transform: scale(1.08);
  z-index: 2;
}
*/

/* 颜色 */
.green { background: #4caf50; }
.blue  { background: #2196f3; }
.red   { background: #f44336; }
.purple {background: #9c27b0; }
.orange {background: #ff9800; }

/* ================= 弹窗 ================= */
.mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  width: 600px;
  background: white;
  border-radius: 10px;
  padding: 20px;
}

.m-title {
  font-weight: bold;
  margin-bottom: 10px;
}

.list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.item-card {
  border: 1px solid #ddd;
  padding: 10px;
  font-size: 12px;
  border-radius: 6px;
}

/* 遮罩 */
.mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

/* 弹窗主体 */
.modal {
  width: 620px;
  max-height: 70vh;
  overflow: auto;
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

/* 标题区 */
.m-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 12px;
}

.m-title {
  font-size: 16px;
  font-weight: bold;
}

.m-sub {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}

/* ================= 空状态 ================= */
.empty {
  padding: 40px 0;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.empty-text {
  font-size: 14px;
}

/* ================= 物料卡片 ================= */
.list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.item-card {
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 10px;
  background: #fafafa;
  transition: 0.2s;
}

.item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
}

/* 顶部 */
.item-top {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  margin-bottom: 6px;
}

.index {
  color: #999;
}

.code {
  color: #1976d2;
}

/* 内容 */
.item-body {
  font-size: 12px;
  color: #555;
  line-height: 1.6;
}

/* 底部 */
.item-bottom {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #333;
  font-weight: 500;
}

.confirm-box {
  width: 360px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.confirm-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.confirm-text {
  font-size: 13px;
  color: #666;
  margin-bottom: 18px;
}

.confirm-btns {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.cancel {
  padding: 6px 16px;
  border: none;
  background: #ccc;
  border-radius: 6px;
  cursor: pointer;
}

.ok {
  padding: 6px 16px;
  border: none;
  background: #f44336;
  color: white;
  border-radius: 6px;
  cursor: pointer;
}

</style>
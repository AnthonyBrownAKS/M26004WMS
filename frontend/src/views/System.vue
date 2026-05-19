<template>

  <div class="page">

    <div class="card">

      <!-- 标题 -->

      <div class="top-bar">

        <div class="title">
          系统管理
        </div>


        <div class="log-tools">

          <div>
            日志占用空间：{{ logSize }}
          </div>

        </div>


        <div class="button-group">

          <button
              class="danger-btn"
              @click="clearLogs"
          >
            清空日志
          </button>

        </div>

      </div>

      <!-- 图表 -->

      <div
          ref="chartRef"
          class="chart"
      ></div>

    </div>

  </div>

</template>

<script setup>

import {onMounted, ref, inject} from "vue";

const showMessage = inject('showMessage')

import * as echarts from "echarts";

import axios from "axios";

const chartRef = ref()

const logSize = ref()

let chart = null

// 初始化图表

const initChart = (xData, inboundData, outboundData) => {

  if (!chart) {

    chart = echarts.init(chartRef.value)
  }

  const option = {

    tooltip: {
      trigger: 'axis'
    },

    legend: {
      data: ['INBOUND', 'OUTBOUND'],
      top: 10
    },

    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },

    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData
    },

    yAxis: {
      type: 'value'
    },

    series: [

      {
        name: 'INBOUND',
        type: 'line',
        smooth: true,
        data: inboundData
      },

      {
        name: 'OUTBOUND',
        type: 'line',
        smooth: true,
        data: outboundData
      }

    ]
  }

  chart.setOption(option)
}

// 获取图表数据

const loadChartData = async () => {

  const res = await axios.get('/api/log/chart')

  const data = res.data.data

  initChart(
      data.dates,
      data.inbound,
      data.outbound
  )
}

// 清空日志

const clearLogs = async () => {

  const confirmClear = confirm("确认清空日志吗？")

  if (!confirmClear) return

  await axios.delete('/api/log/clear')

  showMessage('日志已清空', 'success')

  await loadChartData()
}

const loadSize = async ()=>{

  const res = await axios.get("/api/log/size")

  logSize.value = res.data.data
}

onMounted(() => {

  loadSize()

  loadChartData()

  window.addEventListener("resize", () => {

    chart?.resize()
  })
})

</script>

<style scoped>

.page {

  padding: 20px;

  background: #eef2f6;
}

.card {

  background: white;

  border-radius: 10px;

  padding: 24px;

  min-height: 700px;

  box-shadow:
      0 2px 8px rgba(0,0,0,0.05);
}

/* 顶部 */

.top-bar {

  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 25px;
}

.title {

  font-size: 18px;

  font-weight: bold;

  color: #2b2f36;

  letter-spacing: 1px;
}

.button-group {

  display: flex;

  gap: 12px;
}

/* 按钮 */

.danger-btn {

  padding: 10px 18px;

  border: none;

  border-radius: 6px;

  background: #e53935;

  color: white;

  cursor: pointer;

  transition: 0.2s;
}

.danger-btn:hover {

  background: #c62828;
}

/* 图表 */

.chart {

  width: 100%;

  height: 550px;

  margin-top: 10px;
}

.log-tools{
  display:flex;
  gap:12px;
  align-items:center;
}

</style>
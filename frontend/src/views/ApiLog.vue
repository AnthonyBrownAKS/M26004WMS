<template>

  <div class="page">

    <div class="card">

      <div class="title">
        接口管理
      </div>

      <div class="placeholder">

        暂无数据

      </div>

    </div>

  </div>

</template>

<script setup>

</script>

<style scoped>

.page {

  display: flex;

  flex-direction: column;
}

.card {

  background: white;

  border-radius: 10px;

  padding: 24px;

  min-height: 700px;

  box-shadow:
      0 2px 8px rgba(0,0,0,0.05);
}

.title {

  font-size: 20px;

  font-weight: bold;

  color: #333;

  margin-bottom: 24px;
}

.placeholder {

  height: 500px;

  display: flex;

  align-items: center;

  justify-content: center;

  color: #999;

  font-size: 18px;
}

</style>
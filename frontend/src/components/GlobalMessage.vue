<template>

  <transition name="message-fade">

    <div
        v-if="visible"
        class="message-box"
        :class="type"
    >

      <div class="message-icon">

        <span v-if="type === 'success'">
          ✔
        </span>

        <span v-else-if="type === 'error'">
          ✖
        </span>

        <span v-else-if="type === 'warning'">
          !
        </span>

        <span v-else>
          i
        </span>

      </div>

      <div class="message-content">
        {{ message }}
      </div>

    </div>

  </transition>

</template>

<script setup>

import {
  ref
} from 'vue'

const visible = ref(false)

const message = ref('')

const type = ref('info')

let timer = null

/* 暴露方法 */

const show = (
    text,
    msgType = 'info',
    duration = 2500
) => {

  message.value = text

  type.value = msgType

  visible.value = true

  clearTimeout(timer)

  timer = setTimeout(() => {

    visible.value = false

  }, duration)

}

/* 对外暴露 */

defineExpose({

  show

})

</script>

<style scoped>

.message-box {

  position: fixed;

  top: 30px;

  left: 50%;

  transform: translateX(-50%);

  min-width: 260px;

  max-width: 420px;

  min-height: 52px;

  padding: 14px 22px;

  border-radius: 10px;

  display: flex;

  align-items: center;

  gap: 12px;

  color: white;

  font-size: 15px;

  z-index: 99999;

  box-shadow:
      0 6px 20px rgba(0,0,0,0.18);
}

.message-icon {

  font-size: 18px;

  font-weight: bold;
}

.message-content {

  flex: 1;

  word-break: break-all;
}

/* 类型颜色 */

.success {

  background: #67c23a;
}

.error {

  background: #f56c6c;
}

.warning {

  background: #e6a23c;
}

.info {

  background: #409eff;
}

/* 动画 */

.message-fade-enter-active,
.message-fade-leave-active {

  transition: all 0.25s;
}

.message-fade-enter-from,
.message-fade-leave-to {

  opacity: 0;

  transform:
      translateX(-50%)
      translateY(-20px);
}

</style>
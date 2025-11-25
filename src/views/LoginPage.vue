<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="title">欢迎登录</h2>

      <div class="form-group">
        <label>用户名</label>
        <input
            v-model="username"
            type="text"
            placeholder="请输入用户名"
        />
      </div>

      <div class="form-group">
        <label>密码</label>
        <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
        />
      </div>

      <button class="login-btn" @click="login">登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const router = useRouter()

function login() {
  if (username.value === 'admin' && password.value === '123') {
    router.push('/home')
  } else {
    alert('账号或密码错误')
  }
}
</script>

<style scoped>
/* 整体容器：居中对齐 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #4b6cb7, #182848);
}

/* 登录卡片 */
.login-card {
  width: 360px;
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

/* 标题 */
.title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 24px;
  color: #333;
}

/* 表单组 */
.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.form-group label {
  margin-bottom: 6px;
  font-size: 14px;
  color: #555;
}

/* 输入框美化 */
input {
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  outline: none;
  transition: 0.2s;
}

input:focus {
  border-color: #4b6cb7;
  box-shadow: 0 0 4px rgba(75, 108, 183, 0.4);
}

/* 登录按钮 */
.login-btn {
  margin-top: 10px;
  width: 100%;
  padding: 10px;
  font-size: 15px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  color: white;
  background: linear-gradient(135deg, #4b6cb7, #182848);
  transition: 0.25s;
}

.login-btn:hover {
  opacity: 0.88;
  transform: translateY(-2px);
}
</style>

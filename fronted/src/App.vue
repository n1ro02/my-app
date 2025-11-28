<template>
  <div class="layout">
    <header class="header">
      <h2>我的小项目</h2>
    </header>

    <main class="content">
      <!-- 由路由决定显示哪个页面 -->
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>

<style>
.layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.header {
  background: #19aad4;
  color: white;
  padding: 2px 2px;
}

.content {
  flex: 1;
  padding: 2px;
}
</style>

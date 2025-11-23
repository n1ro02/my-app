<template>
  <div class="page">
    <header class="hero">
      <div>
        <p class="eyebrow">Vue + Spring Boot</p>
        <h1>短链接服务</h1>
        <p class="lede">
          输入长链接，生成一个便于分享的短链接。后台使用 Spring Boot 提供接口，默认地址为
          <code>http://localhost:8080</code>。
        </p>
      </div>
      <div class="pill">轻量 Demo</div>
    </header>

    <section class="card">
      <h2>创建短链接</h2>
      <form class="form" @submit.prevent="createLink">
        <label>
          长链接
          <input v-model="form.targetUrl" required type="url" placeholder="https://example.com/articles/123" />
        </label>
        <label>
          自定义短码（可选）
          <input v-model="form.customCode" type="text" maxlength="32" placeholder="例如: promo-2025" />
        </label>
        <div class="actions">
          <button class="primary" type="submit" :disabled="creating">
            {{ creating ? '生成中…' : '生成短链接' }}
          </button>
          <span class="note">短码留空时会自动生成。</span>
        </div>
      </form>
      <p v-if="message" class="toast">{{ message }}</p>
    </section>

    <section class="card">
      <div class="section-title">
        <h2>历史记录</h2>
        <button class="ghost" type="button" :disabled="loading" @click="loadLinks">
          {{ loading ? '刷新中…' : '刷新' }}
        </button>
      </div>
      <div v-if="links.length === 0" class="empty">暂无数据，先创建一个短链接吧。</div>
      <table v-else class="links">
        <thead>
          <tr>
            <th>短码</th>
            <th>短链接</th>
            <th>目标地址</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="link in links" :key="link.id">
            <td class="mono">{{ link.code }}</td>
            <td>
              <div class="short-url">
                <a :href="link.shortUrl" target="_blank" rel="noreferrer">{{ link.shortUrl }}</a>
                <button class="ghost" type="button" @click="copyLink(link.shortUrl)">复制</button>
              </div>
            </td>
            <td class="target">{{ link.targetUrl }}</td>
            <td class="mono">{{ formatDate(link.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'

const apiBase = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'
const links = ref([])
const loading = ref(false)
const creating = ref(false)
const message = ref('')

const form = reactive({
  targetUrl: '',
  customCode: '',
})

const handleError = async (response) => {
  let errorText = '请求失败，请稍后重试'
  try {
    const body = await response.json()
    errorText = body.message ?? errorText
  } catch (error) {
    console.error('无法读取错误信息', error)
  }
  throw new Error(errorText)
}

const loadLinks = async () => {
  loading.value = true
  try {
    const response = await fetch(`${apiBase}/api/links`)
    if (!response.ok) {
      await handleError(response)
    }
    links.value = await response.json()
  } catch (error) {
    message.value = error.message
  } finally {
    loading.value = false
  }
}

const createLink = async () => {
  if (!form.targetUrl) return
  creating.value = true
  message.value = ''
  try {
    const response = await fetch(`${apiBase}/api/links`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    })
    if (!response.ok) {
      await handleError(response)
    }
    const created = await response.json()
    links.value = [created, ...links.value.filter((item) => item.code !== created.code)]
    form.targetUrl = ''
    form.customCode = ''
    message.value = '短链接已生成！'
  } catch (error) {
    message.value = error.message
  } finally {
    creating.value = false
  }
}

const copyLink = async (shortUrl) => {
  try {
    await navigator.clipboard.writeText(shortUrl)
    message.value = '已复制到剪贴板'
  } catch (error) {
    console.error('复制失败', error)
    message.value = '复制失败，请手动复制'
  }
}

const formatDate = (value) => {
  if (!value) return ''
  const date = typeof value === 'string' ? new Date(value) : value
  return date.toLocaleString()
}

onMounted(loadLinks)
</script>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 32px 16px 64px;
  color: #0f172a;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.hero h1 {
  font-size: 32px;
  margin: 4px 0 8px;
}

.eyebrow {
  color: #64748b;
  font-weight: 600;
  letter-spacing: 0.04em;
  margin: 0;
}

.lede {
  max-width: 640px;
  line-height: 1.6;
}

.pill {
  background: #10b98122;
  color: #047857;
  border: 1px solid #10b98144;
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 600;
}

.card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 6px 24px rgba(15, 23, 42, 0.04);
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 12px;
}

.form {
  display: grid;
  gap: 12px;
}

label {
  display: grid;
  gap: 6px;
  font-weight: 600;
  color: #0f172a;
}

input {
  padding: 12px;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  font-size: 15px;
}

input:focus {
  outline: 2px solid #22c55e55;
  border-color: #22c55e;
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

button {
  cursor: pointer;
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  font-weight: 600;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

button.primary {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
}

button.ghost {
  background: transparent;
  border: 1px solid #cbd5e1;
  color: #0f172a;
}

.note {
  color: #64748b;
  font-size: 14px;
}

.toast {
  margin: 8px 0 0;
  color: #0f766e;
  background: #99f6e424;
  border: 1px solid #5eead4;
  padding: 10px 12px;
  border-radius: 10px;
}

.links {
  width: 100%;
  border-collapse: collapse;
}

.links th,
.links td {
  padding: 10px 8px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.links th {
  color: #475569;
  font-size: 14px;
}

.short-url {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.target {
  max-width: 320px;
  word-break: break-all;
}

.mono {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  color: #1e293b;
}

.empty {
  padding: 12px;
  color: #475569;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 10px;
}

@media (max-width: 720px) {
  .hero {
    flex-direction: column;
  }

  .links th:nth-child(4),
  .links td:nth-child(4) {
    display: none;
  }
}
</style>

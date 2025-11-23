# 项目文档：短链接服务（Vue + Spring Boot）

本指南旨在帮助你快速了解并运行本仓库的短链接服务，包括技术栈、架构、运行方式、API 说明以及常见开发场景。所有示例命令默认在仓库根目录执行。

## 1. 技术栈概览
- **前端**：Vite + Vue 3（组合式 API，原生 fetch 调用后端）。
- **后端**：Spring Boot 3，使用 Spring Web + Spring Data JPA。
- **数据库**：内存 H2 数据库（默认），可替换为 MySQL/PostgreSQL 等。
- **构建与包管理**：npm（前端）、Maven（后端）。

## 2. 功能摘要
- 输入长链接生成短码，可自定义短码，自动校验冲突。
- 列出已有短链接，支持复制生成的完整短链接。
- 访问 `/r/{code}` 自动 302 重定向到原始地址。

## 3. 目录结构速览
```
├── src/                  # 前端 Vue 代码
│   ├── App.vue           # 页面与交互逻辑
│   ├── index.css         # 基础样式
│   └── main.js           # 应用入口
├── backend/              # Spring Boot 后端
│   ├── pom.xml           # Maven 依赖与打包配置
│   └── src/main/java/com/example/shortlink
│       ├── ShortLinkApplication.java   # 启动类
│       ├── model/ShortLink.java        # JPA 实体
│       ├── repository/ShortLinkRepository.java
│       ├── service/ShortLinkService.java        # 业务逻辑与短码生成
│       └── web/                                
│           ├── ShortLinkController.java # REST 接口（创建/查询）
│           └── RedirectController.java  # /r/{code} 重定向
└── docs/PROJECT_GUIDE.md   # 本文档
```

## 4. 快速开始
### 前端（Vite + Vue）
```bash
npm install
npm run dev  # 默认 http://localhost:5173
```
- 如后端地址不同，可在运行前设置 `VITE_API_BASE` 环境变量：
  ```bash
  VITE_API_BASE="http://localhost:8080" npm run dev
  ```

### 后端（Spring Boot）
```bash
cd backend
mvn spring-boot:run  # 默认端口 8080
```
- 默认 H2 内存库，控制台位于 `http://localhost:8080/h2-console`（JDBC URL: `jdbc:h2:mem:shortlinks`）。
- 如需自定义短链基址，在 `application.properties` 覆盖 `shortener.base-url`（用于拼接返回的短链接）。

## 5. 前端页面要点（src/App.vue）
- 表单字段：`targetUrl`（必填，长链接）与 `customCode`（可选自定义短码）。
- 交互逻辑：
  - `createLink()` 调用 `POST /api/links` 生成短链，成功后刷新列表并清空表单。
  - `loadLinks()` 调用 `GET /api/links` 拉取历史记录，页面加载时自动执行。
  - `copyLink()` 使用 `navigator.clipboard` 复制生成的短链接。
- UI 结构：顶部介绍区 + “创建短链接”卡片 + “历史记录”表格（含复制按钮）。

## 6. 后端设计
### 核心组件
- **实体**：`ShortLink`（字段：`id`、`code` 唯一、`targetUrl`、`createdAt`）。
- **仓库**：`ShortLinkRepository` 基于 Spring Data JPA，提供按短码查询与存在性校验。
- **服务**：`ShortLinkService`
  - `create(targetUrl, customCode)`：生成/校验短码并保存，支持自定义短码冲突校验。
  - `listAll()`：按创建时间倒序返回全部短链。
  - `resolve(code)`：查找短码，不存在则返回 404。
  - `buildShortUrl(code)`：基于配置 `shortener.base-url` 拼接完整短链（默认 `http://localhost:8080/r/{code}`）。
- **控制器**：
  - `ShortLinkController`（`/api/links`）：`POST` 创建短链，`GET` 列出短链，使用 CORS 允许本地前端访问。
  - `RedirectController`（`/r/{code}`）：302 重定向到原始地址。

### 短码生成策略
- 自定义短码：直接使用用户输入的 `customCode`（去除首尾空格），若已存在则返回 400。
- 自动生成：从大小写字母 + 数字字符集随机生成 8 位短码，若碰撞则重试直到唯一。

## 7. REST API 速查
- `POST /api/links`
  - 请求体：`{ "targetUrl": "https://example.com", "customCode": "promo-2025" }`（`customCode` 可为空）。
  - 成功响应：`{ id, code, targetUrl, createdAt, shortUrl }`。
  - 失败情况：400（缺少目标地址或短码冲突）。
- `GET /api/links`
  - 响应：短链数组，含 `shortUrl` 便于直接展示。
- `GET /r/{code}`
  - 行为：302 重定向到存储的 `targetUrl`，不存在返回 404。

## 8. 配置与环境变量
- `shortener.base-url`：后端用于拼接短链接的基址（默认为 `http://localhost:8080`）。
- 数据库相关：`spring.datasource.*`、`spring.jpa.*` 可按需调整以接入持久化数据库。
- 前端：`VITE_API_BASE` 控制 API 根路径（开发默认 `http://localhost:8080`）。

## 9. 开发与调试提示
- **CORS**：后端允许来自 `http://localhost:5173` 的跨域请求；若前端端口变更，请在 `ShortLinkController` 的 `@CrossOrigin` 调整。
- **日志与 SQL**：`spring.jpa.show-sql=true` 便于调试，可在生产关闭。
- **H2 控制台**：在浏览器访问 `/h2-console`，使用用户名 `sa`、空密码连接内存库。

## 10. 质量与测试建议
- 前端可添加 Vite/ Vitest 或 Cypress 进行单元/端到端测试；当前项目未附带测试脚本。
- 后端可使用 Spring Boot Test / MockMvc 对 `ShortLinkService` 与控制器编写测试用例。
- 提交前建议运行：
  ```bash
  npm run build        # 校验前端能正常编译
  mvn -f backend/pom.xml test  # 执行后端测试
  ```

## 11. 常见问题（FAQ）
- **短码冲突/重复**：如果自定义短码已存在，后端会返回 400；前端会提示“短码已存在，请更换其他短码”。
- **复制失败**：部分浏览器的非安全上下文可能禁用剪贴板写入，需在 HTTPS 或 localhost 访问。
- **跨域报错**：确保前端访问地址与后端 `@CrossOrigin` 一致，或在控制器上添加所需来源。

## 12. 下一步可以尝试的扩展
- 将数据库切换到 MySQL/PostgreSQL，添加 Flyway 管理 schema 变更。
- 增加访问统计（点击次数、最近访问时间）。
- 为短码设置过期时间或访问密码。
- 使用 Redis 缓存短码解析结果以减轻数据库压力。

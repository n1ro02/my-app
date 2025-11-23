# 短链接服务（Vue + Spring Boot）

本项目提供一个简单的短链接服务：前端使用 Vite + Vue 3 编写，后端使用 Spring Boot 提供短码生成与跳转能力，存储默认使用内存 H2 数据库。想快速了解架构、运行方式、API 设计，可阅读详细文档：[docs/PROJECT_GUIDE.md](docs/PROJECT_GUIDE.md)。

## 运行前端

```bash
npm install
npm run dev
```

可以通过环境变量 `VITE_API_BASE` 指定后端地址（默认 `http://localhost:8080`）。

## 运行后端

```bash
cd backend
mvn spring-boot:run
```

- REST 接口：`/api/links`（创建、查询短链接）
- 访问短链接：`/r/{code}` 会 302 跳转至目标地址。
- 默认使用内存 H2 数据库，并开启了 H2 控制台（`/h2-console`）。

## 目录结构

- `src/`：Vue 前端源码
- `backend/`：Spring Boot 后端源码（Maven 项目）

## 如何合并当前改动（PR 合并指引）

以下流程适用于将当前分支（例如 `work`）的改动合并到主分支（例如 `main`），并推送到远端仓库：

1. 拉取最新的主分支代码：
   ```bash
   git checkout main
   git pull origin main
   ```
2. 回到包含改动的分支并与主分支同步（推荐 rebase 保持提交整洁）：
   ```bash
   git checkout work
   git rebase main
   ```
   如果 rebase 过程中出现冲突，按提示解决冲突后继续：
   ```bash
   git status  # 查看冲突文件
   # 解决冲突后
   git add <file>
   git rebase --continue
   ```
3. 本地验证：
   ```bash
   npm test      # 如配置了前端测试
   mvn test -f backend/pom.xml
   ```
4. 将更新后的分支推送到远端（如果经过 rebase，需要强推）：
   ```bash
   git push origin work --force-with-lease
   ```
5. 在代码托管平台（如 GitHub/GitLab）打开 Pull Request，将 `work` 合并到 `main`，通过代码审查与 CI 后选择「Merge」。

> 如果偏好直接在本地合并而非在线 PR，可以在完成步骤 1–3 后执行 `git checkout main && git merge work`，再推送 `main` 到远端。

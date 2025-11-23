# 短链接服务（Vue + Spring Boot）

本项目提供一个简单的短链接服务：前端使用 Vite + Vue 3 编写，后端使用 Spring Boot 提供短码生成与跳转能力，存储默认使用内存 H2 数据库。

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

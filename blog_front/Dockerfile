# 用于调试的完整 Dockerfile
FROM node:18

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json（或 pnpm-lock.yaml）
# 复制应用源代码
COPY . .

ENV TZ=Asia/Shanghai

# 更新 npm，并安装 pnpm
RUN npm install -g pnpm@latest

# 安装项目依赖
RUN pnpm install --force

# 构建 Nuxt.js 应用
RUN pnpm build

# 启动应用
CMD ["pnpm", "start"]

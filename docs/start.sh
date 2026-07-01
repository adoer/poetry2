#!/bin/bash

echo "=== 新闻网站启动脚本 ==="
echo ""

# 1. 启动后端
echo "[1/2] 启动后端..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

# 等待后端启动
sleep 10

# 2. 构建并启动前端
echo "[2/2] 构建前端..."
cd frontend
npm run build

echo ""
echo "=== 启动完成 ==="
echo "前端: http://localhost (通过 nginx)"
echo "后端: http://localhost:8080"
echo ""
echo "停止: kill $BACKEND_PID"

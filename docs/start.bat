@echo off
echo === 新闻网站启动脚本 ===
echo.

echo [1/3] 构建前端...
cd frontend
call npm run build
cd ..

echo [2/3] 启动后端...
cd backend
start "news-backend" mvn spring-boot:run
cd ..

echo [3/3] 启动前端...
echo 前端静态文件已生成到 frontend/dist/
echo 请配置 nginx 将 root 指向 frontend/dist

echo.
echo === 启动完成 ===
echo 后端: http://localhost:8080
echo 前端: 通过 nginx 访问
pause

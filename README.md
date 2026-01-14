## Redis Configuration (Upstash)

Dự án sử dụng Redis Cloud (Upstash).  
Mỗi người cần **tạo Redis riêng** và cấu hình biến môi trường.

### 1. Tạo Redis trên Upstash
- Truy cập https://upstash.com
- Tạo Redis database
- Lấy 3 thông tin:
  - Endpoint → `REDIS_HOST`
  - Port → `REDIS_PORT`
  - Password → `REDIS_PASSWORD`

---

### 2. Thiết lập biến môi trường (Windows)

#### CMD
```cmd
set REDIS_HOST=open-leopard-10637.upstash.io
set REDIS_PORT=6379
set REDIS_PASSWORD=<YOUR_UPSTASH_REDIS_PASSWORD>
#### PowerShell
```powerShell
$env:REDIS_HOST="open-leopard-10637.upstash.io"
$env:REDIS_PORT="6379"
$env:REDIS_PASSWORD="<YOUR_UPSTASH_REDIS_PASSWORD>"

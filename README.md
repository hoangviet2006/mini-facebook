# \## Redis Configuration (Upstash)

# 

# Dự án sử dụng Redis Cloud (Upstash).  

# Mỗi người cần \*\*tạo Redis riêng\*\* và cấu hình biến môi trường.

# 

# \### 1. Tạo Redis trên Upstash

# \- Truy cập https://upstash.com

# \- Tạo Redis database

# \- Lấy 3 thông tin:

# &nbsp; - Endpoint → `REDIS\_HOST`

# &nbsp; - Port → `REDIS\_PORT`

# &nbsp; - Password → `REDIS\_PASSWORD`

# 

# ---

# 

# \### 2. Thiết lập biến môi trường (Windows)

# 

# \#### CMD


# set REDIS\_HOST=open-leopard-10637.upstash.io
# set REDIS\_PORT=6379
# set REDIS\_PASSWORD=<YOUR\_UPSTASH\_REDIS\_PASSWORD>

# \#### PowerShell

$env:REDIS_HOST="open-leopard-10637.upstash.io"
$env:REDIS_PORT="6379"
$env:REDIS_PASSWORD="<YOUR_UPSTASH_REDIS_PASSWORD>"


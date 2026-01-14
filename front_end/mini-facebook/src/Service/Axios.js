import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL;
const axiosInstance = axios.create({
    baseURL: `${API_URL}`,
});

axiosInstance.interceptors.request.use(config => {  // trước khi gửi request
    const token = localStorage.getItem("accessToken");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

axiosInstance.interceptors.response.use(
    res => res,
    async error => {

        if (error.response?.status === 401 || error.response?.status === 403) {

            const refreshToken = localStorage.getItem("refreshToken");
            if (!refreshToken) {
                localStorage.clear();
                window.location.href = "/login";
                return Promise.reject(error);
            }

            try {
                // Gọi refresh token, không gửi access token cũ
                const res = await axios.post(`${API_URL}/auth/refresh-token`, {
                    refreshToken: refreshToken
                });
                console.log("chạy refresh")
                // Lưu token mới
                localStorage.setItem("accessToken", res.data.accessToken);

                // Gắn vào request cũ và retry
                error.config.headers.Authorization = `Bearer ${res.data.accessToken}`;
                // window.location.reload();
                return axiosInstance(error.config);
            } catch (e) {
                localStorage.clear();
                window.location.href = "/login";
                return Promise.reject(e);
            }
        }

        return Promise.reject(error);
    }
);
export default axiosInstance
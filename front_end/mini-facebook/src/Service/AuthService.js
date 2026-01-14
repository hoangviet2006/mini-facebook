
import axiosInstance  from "./Axios";
const API_URL = process.env.REACT_APP_API_URL;


export async function register(user) {
    const response = await
        axiosInstance.post(`${API_URL}/auth/register`, user);
    return response.data;
}
export async function getOtp(email) {

    const response = await
        axiosInstance.post(`${API_URL}/auth/get/otp`, email);
    console.log("Chay phương thưcs get otp")
    return response.data;
}
export async function login(user) {
    const response = await
        axiosInstance.post(`${API_URL}/auth/login`, user);
    localStorage.setItem("accessToken",response.data.accessToken);
    localStorage.setItem("refreshToken",response.data.refreshToken);
    return response.data;
}

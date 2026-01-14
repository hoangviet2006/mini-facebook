
import axiosInstance from "./Axios";

const API_URL = process.env.REACT_APP_API_URL
export async function getPost() {
    const response =
        await axiosInstance.get(`${API_URL}/post`)
    return response.data;
}
export async function findPostByUser(){
    const response =
        await axiosInstance.get(`${API_URL}/post/find/post/by-user`)
    return response.data;
}
export async function createPost(post){
    const response = await axiosInstance.post(`${API_URL}/post/create/post`,post)
    return response.data;
}
export async function deletePost(id){
    const response = await axiosInstance.delete(`${API_URL}/post/${id}`)
    return response.data;
}
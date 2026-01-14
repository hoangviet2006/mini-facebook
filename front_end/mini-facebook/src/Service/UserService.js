
import axiosInstance from "./Axios";


const API_URL = process.env.REACT_APP_API_URL
export async function getUserByToken(){
    const response =
        await axiosInstance.get(`${API_URL}/user/get/user-by-token`)
    return response.data;
}
export async function profile(){
    const response =
        await axiosInstance.get(`${API_URL}/user/profile`);
    console.log(response?.data)
    return response.data;
}
export  async function updateProfile(user){
    const response =
        await axiosInstance.post(`${API_URL}/user/update/profile`,user)
    return response.data;
}
export async function suggestFriends(){
    const response =
        await axiosInstance.get(`${API_URL}/user/suggest/friends`)
    return response.data;
}
export async function addFriends(receiverId){
    const response =
        await axiosInstance.post(`${API_URL}/user/add/friend`,receiverId)
    return response.data;
}export async function acceptAddFriend(senderId){
    const response =
        await axiosInstance.post(`${API_URL}/user/accept/add/friend`,senderId)
    return response.data;
}
export async function cancelAddFriends(senderId){
    const response =
        await axiosInstance.post(`${API_URL}/user/cancel/add/friend`,senderId)
    return response.data;
}
export async function refuseAddFriend(receiverId){
    const response =
        await axiosInstance.post(`${API_URL}/user/refuse/add/friend`,receiverId)
    return response.data;
}
export async function friendInvitation(){
    const response =
        await axiosInstance.get(`${API_URL}/user/friend/invitation`)
    return response.data;
}
export async function forgotPassWord(email){
    const response =
        await axiosInstance.post(`${API_URL}/auth/forgot/password`,email)
    return response.data;
}
export async function forgotPassWordConfirm(ForgotPasswordDTO){
    const response =
        await axiosInstance.post(`${API_URL}/auth/forgot/password/confirm`,ForgotPasswordDTO)
    return response.data;
}
export async function updatePassword(UpdatePasswordDTO){
    const response =
        await axiosInstance.post(`${API_URL}/user/update/password`,UpdatePasswordDTO)
    return response.data;
}
export async function profileUser(id){
    const response =
        await axiosInstance.get(`${API_URL}/user/profile/${id}`)
    return response.data;
}
export async function getIdCurrentUser(){
    const response =
        await axiosInstance.get(`${API_URL}/user/getId`)
    return response.data;
}

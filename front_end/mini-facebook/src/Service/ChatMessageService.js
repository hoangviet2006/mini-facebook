
import axiosInstance from "./Axios";
const API_URL = process.env.REACT_APP_API_URL


export async function createConversation(createConversationRequest) {
    const response =
        await axiosInstance.post(`${API_URL}/chat-message/create/conversation`,createConversationRequest)
    return response.data;
}
export async function loadListInbox() {
    const response =
        await axiosInstance.get(`${API_URL}/chat-message/conversations/inbox`)
    return response.data;
}
export async function getMessage(id) {
    const response =
        await axiosInstance.get(`${API_URL}/chat-message/conversations/${id}/messages`)
    return response.data;
}
export async function seenMessage(seenMessageRequest) {
    const response =
        await axiosInstance.post(`${API_URL}/chat-message/seen`,seenMessageRequest)
    return response.data;
}
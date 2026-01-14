
        import {Link, useParams} from "react-router-dom";
        import React, {useRef , useEffect, useState } from "react";
        import {createConversation, getMessage, loadListInbox, seenMessage} from "../Service/ChatMessageService";
        import "../css/chatPage.css"
        import {toast} from "sonner";
        import Header from "./Header";
        import { Client } from "@stomp/stompjs";
        import SockJS from "sockjs-client";
        const PageChatComponent = ()=>{
            const { id } = useParams();
            const [conversation, setConversation] = useState(null);
            const [message, setMessage] = useState([]);
            const [inboxList, setInboxList] = useState([])
            const currentUserId = Number(localStorage.getItem("idCurrentUser"));
            const [stompClient, setStompClient] = useState(null);
            const [inputMessage, setInputMessage] = useState("");
            const API_WS =process.env.REACT_APP_SOCKET_URL;
            const messageEndRef = useRef(null);
            useEffect(() => {
                const createConversationRequest ={
                    userId:id
                }
                const initConversation = async ()=>{
                    try {
                        const response = await loadListInbox();
                        setInboxList(response)
                        const newConversation = await createConversation(createConversationRequest);
                        setConversation(newConversation)
                        const getMess = await getMessage(newConversation.idConversation)
                        setMessage(getMess);
                    }catch (e) {
                        toast.warning(e?.response?.data);
                    }
                }
                initConversation();
            }, [id,currentUserId]);
            useEffect(() => {
                messageEndRef.current?.scrollIntoView({ behavior: "smooth" }); // khi co tin nhan cuon sao cho thay messageEndRef
            }, [message]);

            // ws
            useEffect(() => {
                if (!conversation) return;

                const client = new Client({
                    webSocketFactory: () => new SockJS(`${API_WS}`),
                    connectHeaders: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    },
                    debug: (str) => console.log(str),
                    reconnectDelay: 5000
                });

                client.onConnect = () => {
                    console.log("Connected to WebSocket");
                    client.subscribe(`/topic/conversation/${conversation.idConversation}`, (msg) => {
                        const newMsg = JSON.parse(msg.body);
                        setMessage((prev) => [...prev, newMsg]);
                    });
                };

                client.activate();
                setStompClient(client);

                return () => {
                    client.deactivate(); // disconnect khi unmount
                };
            }, [conversation]);



            const handleSendMessage = () => {
                if (stompClient && inputMessage.trim() !== "" && conversation) {
                    stompClient.publish({
                        destination: "/app/chat.send",
                        body: JSON.stringify({
                            content: inputMessage,
                            conversationId: conversation.idConversation,
                            idSender:currentUserId
                        })
                    });
                    setInputMessage("");
                }
            };
            // ws
            const handleSeen = async (idConversation)=>{
                try {
                    const seenMessageRequest = {
                        idConversation:idConversation,
                    }
                    const rs = await seenMessage(seenMessageRequest)
                    console.log(rs)
                }catch (e) {

                }
            }

            if (!conversation) return <div>Đang tải đoạn chat...</div>;
            return (
                <div className="chat-layout">
                    <Header>    </Header>
                    <div className="chat-inbox">
                        {inboxList.length === 0 ? (
                            <p className="no-inbox">Chưa có cuộc trò chuyện</p>
                        ) : (
                            inboxList.map((ib) => (
                                <div
                                    key={ib.idConversation}
                                    className="item-inbox"
                                >
                                    <img
                                        src={ib.avatar || "/default-avatar.png"}
                                        alt="avatar"
                                        className="avatar-inbox"
                                    />
                                    <Link to={`/chat/${ib.idUser}`}  className="inbox-link">
                                        <div className="infor-inbox" onClick={()=>handleSeen(ib.idConversation)}>
                                            <p className="name-inbox">{ib.fullName}</p>
                                            <p className="last-inbox">Nhấn để nhắn tin</p>
                                        </div>
                                    </Link>
                                </div>
                            ))
                        )}
                    </div>
                    <div className="chat-main">
                        <div className="chat-header">
                            <img
                                src={conversation.avatar || "https://via.placeholder.com/40"}
                                alt={conversation.fullName}
                                className="chat-avatar"
                            />
                            <span className="chat-fullname">{conversation.fullName}</span>
                        </div>
                        <div className="message">
                            {message.length === 0 && (
                                <div className="no-message">Các bạn đã có thể trò chuyện với nhau</div>

                            )}

                            {message.map((msg) => {
                                const isMe = msg.senderId === currentUserId;
                                console.log(msg.senderId)
                                return (
                                    <div
                                        key={msg.id}
                                        className={`message-item ${isMe ? "message-sent" : "message-received"}`}
                                    >
                                        <div className="message-row-inner">
                                            <img src={msg.avatarUrl} className="avatar-mini"/>
                                            <div className="message-bubble">
                                                {msg.content}
                                            </div>
                                        </div>

                                        <div className="message-time">
                                            {new Date(msg.createdAt).toLocaleTimeString()}
                                        </div>
                                    </div>
                                );
                            })}
                            <div ref={messageEndRef}></div>
                        </div>
                        <div className="chat-input">
                            <input
                                type="text"
                                placeholder="Nhập tin nhắn..."
                                value={inputMessage}
                                onChange={(e) => setInputMessage(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") handleSendMessage();
                                }}
                            />
                            <button onClick={handleSendMessage}>Gửi</button>
                        </div>
                    </div>
                </div>
            );
        }
        export default PageChatComponent;
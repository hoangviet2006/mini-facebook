import Header from "./Header";
import "../css/home.css"
import {useEffect, useState} from "react";
import {getPost} from "../Service/PostService";
import {addFriends, cancelAddFriends, suggestFriends} from "../Service/UserService";
import {toast} from "sonner";
import {
    Search,
    Bell,
    MessageCircle,
    Menu,
    User,
    Settings,
    LogOut,
    LogIn,
    Share,
    Share2,
    Share2Icon
} from 'lucide-react';
import {FcLike} from "react-icons/fc";
import {BiLike} from "react-icons/bi";
import ClickSpark from "./ClickSpark";
import {Link} from "react-router-dom";

const HomePageComponent = () => {
    const [posts, setPost] = useState([]);
    const [token, setToken] = useState(localStorage.getItem("accessToken") || null);
    const [suggestFriend, setSuggestFriend] = useState([])
    useEffect(() => {
        if (token) {
            const fetchData = async () => {
                try {
                    const response = await getPost();
                    const re = await suggestFriends();
                    setPost(Array.isArray(response) ? response : []);
                    setSuggestFriend( Array.isArray(re)? re: []);
                } catch (e) {
                    setPost([]);
                    setSuggestFriend([[]])
                    console.error("Lỗi fetch posts:", e.response?.status);
                }
            };
            fetchData();
        } else {
            setPost([]);
        }
    }, [token]);
    const handleAddFriend = async (receiverId) => {
        try {
            const receiverDto = {receiverId};
            const response = await addFriends(receiverDto);
            toast.success(response || "Thành công");
            setSuggestFriend(prev =>
                prev.map(u =>
                    u.id === receiverId ? {...u, action: "cancel"} : u
                )
            )
        } catch (error) {
            if (!error) {
                toast.error("Không thể kết nối với server")
            }
            toast.warning(error.response?.data || "Lỗi khi gửi lời mời");
        }
    }
    const handleCancelFriendRequest = async (receiverId) => {
        try {
            const receiverDto = {receiverId};
            const response = await cancelAddFriends(receiverDto);
            toast.success(response || "Thành công");
            setSuggestFriend(prev =>
                prev.map(u =>
                    u.id === receiverId ? {...u, action: "add"} : u
                )
            )
        } catch (e) {
            toast.error(e.response?.data || "Lỗi khi hủy gửi lời mời");
        }

    }
    return (
        <ClickSpark
            sparkColor='#0c0c0c'
            sparkSize={10}
            sparkRadius={15}
            sparkCount={8}
            duration={400}
        >
            <Header/>
            <div className="home-layout">
                <div className="home-left">
                    {token && <h3 className="home-suggest-title">Gợi ý kết bạn</h3> }
                    {suggestFriend.length === 0 ? (
                        <p className="home-suggest-empty">Không có gợi ý nào</p>
                    ) : (
                        <div className="home-suggest-list">
                            {suggestFriend.map(user => (
                                <div key={user.id} className="home-suggest-card">
                                    <div className="home-suggest-header">
                                        <Link to={`/profile/${user.id}`}>
                                            <img
                                                src={user.avatarUrl}
                                                className="home-suggest-avatar"
                                            />
                                        </Link>
                                        <span className="home-suggest-name">{user.fullName}</span>
                                    </div>

                                    {user.action === "add" && (
                                        <div className="home-suggest-actions">
                                            <button
                                                className="home-suggest-btn"
                                                onClick={() => handleAddFriend(user.id)}
                                            >
                                                Thêm bạn bè
                                            </button>
                                            <button className="home-suggest-btn-remove">Gỡ</button>
                                        </div>
                                    )}

                                    {user.action === "cancel" && (
                                        <div className="home-suggest-actions">
                                            <button
                                                className="home-suggest-btn-cancel"
                                                onClick={() => handleCancelFriendRequest(user.id)}
                                            >
                                                Hủy yêu cầu
                                            </button>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                <div className="home-main">
                    {posts.length === 0 ? (
                        <p className={"no-post"}>Chưa có bài đăng nào</p>
                    ) : (
                        posts.map((post) => (
                            <div key={post.id} className="home-post">
                                <div className="home-post-header">
                                    <img
                                        src={post.user.avatarUrl}
                                        className="home-post-avatar"
                                    />
                                    <div className="home-post-user">
                                        <h4>{post.user.fullName}</h4>
                                        <span className="home-post-time">
                                          {new Date(post.createdAt).toLocaleString()}
                                        </span>
                                    </div>
                                </div>

                                <div className="home-post-content">
                                    <p>{post.content}</p>

                                    {post.url?.match(/\.(jpg|png|jpeg|gif)$/i) && (
                                        <img src={post.url} className="home-post-image"/>
                                    )}

                                    {post.url?.match(/\.(mp4|webm|ogg)$/i) && (
                                        <video src={post.url} controls className="home-post-video"/>
                                    )}
                                </div>

                                <div className="home-post-actions">
                                    <button className="profile-user-action-btn">
                                        <span>Like</span>
                                    </button>
                                    <button className="profile-user-action-btn">
                                        <span>Comment</span>
                                    </button>
                                    <button className="profile-user-action-btn">
                                        <span>Share</span>
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
                <div className="home-right"/>
            </div>
        </ClickSpark>
    )
}
export default HomePageComponent;

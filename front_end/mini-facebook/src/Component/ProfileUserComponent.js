import {useEffect, useState} from "react";
import {profileUser} from "../Service/UserService";
import {useSearchParams, useParams, Link} from "react-router-dom";
import "../css/profile-user.css";
import Header from "./Header";

const ProfileUserComponent = () => {
    const [user, setUser] = useState(null);
    const {id} = useParams();
    useEffect(() => {
        const data = async () => {
            const response = await profileUser(id);
            setUser(response);
        };
        data();
    }, [id]);

    if (!user) {
        return <div className="profile-user-loading">Đang tải...</div>;
    }

    return (
        <div className="profile-user-container">
            <Header></Header>

            <div className="profile-user-header">
                <div className="profile-user-cover-photo">
                    <img src={user.background} alt="Cover"/>
                </div>

                <div className="profile-user-info-section">
                    <div className="profile-user-info-wrapper">
                        <div className="profile-user-avatar-container">
                            <img src={user.avatar} alt={user.fullName} className="profile-user-avatar"/>
                        </div>

                        <div className="profile-user-details">
                            <h1 className="profile-user-name">{user.fullName}</h1>
                            <p className="profile-user-bio">{user.bio}</p>
                        </div>
                        <Link to={`/chat/${id}`} className={"profile-user-link"}>

                            <div className="profile-user-actions">

                                <button className="profile-user-message-btn">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                                        <path
                                            d="M12 2C6.48 2 2 6.48 2 12c0 1.54.36 3 .97 4.29L2 22l5.71-.97C9 21.64 10.46 22 12 22c5.52 0 10-4.48 10-10S17.52 2 12 2zm0 18c-1.38 0-2.67-.35-3.83-.96l-.27-.16-2.84.48.48-2.84-.16-.27C4.35 14.67 4 13.38 4 12c0-4.41 3.59-8 8-8s8 3.59 8 8-3.59 8-8 8z"/>
                                        <path
                                            d="M17.25 14.75c-.25-.12-1.47-.72-1.69-.81-.23-.08-.39-.12-.56.12-.17.25-.64.81-.78.97-.14.17-.29.19-.54.06-.25-.12-1.05-.39-2-1.23-.74-.66-1.23-1.47-1.38-1.72-.14-.25-.02-.38.11-.51.11-.11.25-.29.37-.43s.17-.25.25-.41c.08-.17.04-.31-.02-.43s-.56-1.34-.76-1.84c-.2-.48-.41-.42-.56-.43h-.48c-.17 0-.43.06-.66.31-.23.25-.87.85-.87 2.07s.89 2.4 1.01 2.56c.12.17 1.75 2.67 4.23 3.74.59.26 1.05.41 1.41.52.59.19 1.13.16 1.56.1.48-.07 1.47-.6 1.67-1.18.21-.58.21-1.07.14-1.18s-.22-.16-.47-.28z"/>
                                    </svg>
                                    Nhắn tin
                                </button>

                            </div>
                        </Link>
                    </div>
                </div>
            </div>

            <div className="profile-user-content-area">
                <div className="profile-user-posts-container">
                    {  Array.isArray( user.posts)&&user.posts.length===0&& (<p>Chưa có bài đăng nào</p>)}
                    {user.posts && user.posts.map((post, index) => (
                        <div key={index} className="profile-user-post-card">
                            <div className="profile-user-post-header">
                                <img src={user.avatar} alt={user.fullName} className="profile-user-post-avatar"/>
                                <div className="profile-user-post-user-info">
                                    <h3 className="profile-user-post-user-name">{user.fullName}</h3>
                                    <p className="profile-user-post-time">
                                        {new Date(post.createAt).toLocaleDateString("vi-VN")}
                                    </p>
                                </div>

                            </div>

                            <div className="profile-user-post-content">
                                <p>{post.content}</p>
                            </div>

                            {post.url && (
                                <div className="profile-user-post-image">
                                    <img src={post.url} alt="Post content"/>
                                </div>
                            )}

                            <div className="profile-user-post-actions">
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
                    ))}
                </div>
            </div>
        </div>
    );
};

export default ProfileUserComponent;
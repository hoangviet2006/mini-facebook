import React, {useEffect, useState} from 'react';
import {Search, Bell, MessageCircle, Menu, User, Settings, LogOut, LogIn} from 'lucide-react';
import "../css/header.css"
import {
    acceptAddFriend,
    friendInvitation,
    getIdCurrentUser,
    getUserByToken,
    refuseAddFriend
} from "../Service/UserService";
import {Link, useNavigate} from "react-router-dom";
import {toast} from "sonner";
import {FaUserFriends} from "react-icons/fa";
import ClickSpark from "./ClickSpark";
import {loadListInbox} from "../Service/ChatMessageService";

const Header = () => {
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem("accessToken") || "");
    const [friendRequestList, setFriendRequestList] = useState([]);
    const [openMenu, setOpenMenu] = useState(false);
    const [openModal, setOpenModal] = useState(false);
    const [stateInbox, setStateInbox] = useState(false);
    const [inboxList, setInboxList] = useState([])
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            setUser(null);
            console.log("Không lấy được thong tin user")
            return;
        }
        const fetchData = async () => {
            try {
                const response = await getUserByToken();
                const rp = await friendInvitation();
                setFriendRequestList(rp);
                setUser(response)
                const idCurrentUser = await getIdCurrentUser();
                localStorage.setItem("idCurrentUser",idCurrentUser)
            } catch (e) {
                setToken(null)
                setUser(null)
                setFriendRequestList([])
            }
        }
        fetchData();
    }, [token]);
    const navigate = useNavigate();
    const handleLogOut = () => {
        localStorage.clear();
        setToken(null);
        navigate('/login')
    };
    const handleAcceptFriend = async (idFriend) => {
        const senderDto = {
            senderId: idFriend
        }
        try {
            const response = await acceptAddFriend(senderDto);
            toast.success(response);
            setFriendRequestList(
                pre =>
                    pre.map(s => s.sender.id === idFriend ? {...s, status: "APPROVED"} : s
                    ))
        } catch (e) {
            toast.warning(e?.response?.data)
        }
    }
    const handleRefuseFriend = async (idFriend) => {
        const senderDto = {
            senderId: idFriend
        }
        try {
            const response = await refuseAddFriend(senderDto);
            toast.success(response);
            setFriendRequestList(
                pre =>
                    pre.map(s => s.sender.id === idFriend ? {...s, status: ""} : s
                    ))
        } catch (e) {
            toast.warning(e?.response?.data)
        }
    }
    const loadInbox = async () => {
        setStateInbox((pre) => !pre)
        const response = await loadListInbox();
        setInboxList(response)
    }

    return (
        <ClickSpark
            sparkColor='#0c0c0c'
            sparkSize={10}
            sparkRadius={15}
            sparkCount={8}
            duration={400}
        >
            <>
                <header className="fb-header">
                    {/* Logo */}
                    <div className="fb-header-logo-section">
                        {token && (
                            <Link to={"/"}>
                                <div className="fb-header-logo-icon">
                                    <svg viewBox="0 0 24 24" fill="none">
                                        <circle cx="12" cy="12" r="10" fill="#4F63D2"/>
                                        <path
                                            d="M7 9 L12 16 L17 9"
                                            stroke="white"
                                            strokeWidth="2.5"
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                        />
                                    </svg>
                                </div>
                            </Link>
                        )}
                        <h1 className="fb-header-logo-text">FakeBook</h1>
                    </div>

                    {/* Search */}
                    <div className="fb-header-search-section">
                        <div className="fb-header-search-wrapper">
                            <Search className="fb-header-search-icon" size={18}/>
                            <input
                                type="text"
                                className="fb-header-search-input"
                                placeholder="Tìm kiếm bạn bè, bài viết..."
                            />
                        </div>
                    </div>

                    {/* Actions */}
                    <div className="fb-header-actions">
                        <button
                            className="fb-header-icon-btn"
                            onClick={()=>loadInbox()}
                        >
                            <MessageCircle size={20}/>
                            <span className="fb-header-notification-badge"></span>
                        </button>

                        {/* Friend menu */}
                        <div className="fb-header-friend-menu-wrapper">
                            <button
                                className="fb-header-icon-btn"
                                onClick={() => setOpenMenu(!openMenu)}
                            >
                                <FaUserFriends size={20}/>
                            </button>

                            {openMenu && (
                                <div className="fb-header-friend-menu">
                                    <div
                                        className="fb-header-friend-item"
                                        onClick={() => {
                                            setOpenModal(true);
                                            setOpenMenu(false);
                                        }}
                                    >
                                        <FaUserFriends size={18}/>
                                        <span>Lời mời kết bạn</span>
                                    </div>
                                    <div className="fb-header-friend-item">
                                        <FaUserFriends size={18}/>
                                        <span>Bạn bè gợi ý</span>
                                    </div>
                                </div>
                            )}
                        </div>

                        <div className="fb-header-divider"></div>

                        <div className="fb-header-username">
                            <span className="fb-header-at">@</span>
                            <span>{user?.username || "Guest"}</span>
                        </div>

                        <div
                            className="fb-header-profile-btn"
                            onClick={() => setShowDropdown(!showDropdown)}
                        >
                            <div className="fb-header-profile-avatar">
                                <img src={user?.avatarUrl} alt="avatar"/>
                            </div>
                        </div>

                        {/* Dropdown */}
                        {showDropdown && token && user && (
                            <div className="fb-header-dropdown fb-header-dropdown-active">
                                <Link to="/profile" className="fb-header-dropdown-item">
                                    <User size={18}/>
                                    <span>Trang cá nhân</span>
                                </Link>
                                <div className="fb-header-dropdown-item">
                                    <Settings size={18}/>
                                    <span>Cài đặt</span>
                                </div>
                                <div className="fb-header-dropdown-divider"></div>
                                <div
                                    className="fb-header-dropdown-item"
                                    onClick={handleLogOut}
                                >
                                    <LogOut size={18}/>
                                    <span>Đăng xuất</span>
                                </div>
                            </div>
                        )}

                        {/* Friend request modal */}
                        {openModal && (
                            <div className="fb-header-friend-modal">
                                <div className="fb-header-modal-content">
                                    <div className="fb-header-modal-header">
                                        <h2>Lời mời kết bạn</h2>
                                        <button
                                            className="fb-header-modal-close"
                                            onClick={() => setOpenModal(false)}
                                        >
                                            ×
                                        </button>
                                    </div>

                                    <div className="fb-header-modal-body">
                                        {friendRequestList.length === 0 ? (
                                            <p>Không có lời mời nào</p>
                                        ) : (
                                            friendRequestList.map(friend => (
                                                <div key={friend.id} className="fb-header-friend-row">
                                                    <img
                                                        src={friend.sender.avatarUrl}
                                                        className="fb-header-friend-avatar"
                                                        alt=""
                                                    />
                                                    <div className="fb-header-friend-info">
                                                        <p className="fb-header-friend-name">
                                                            {friend.sender.fullName}
                                                        </p>
                                                        <p className="fb-header-friend-date">
                                                            {new Date(friend.createdAt).toLocaleString()}
                                                        </p>
                                                    </div>
                                                    <div className="fb-header-friend-actions">
                                                        {friend.status === "PENDING" && (
                                                            <>
                                                                <button
                                                                    className="fb-header-approve-btn"
                                                                    onClick={() => handleAcceptFriend(friend.sender.id)}
                                                                >
                                                                    Chấp nhận
                                                                </button>
                                                                <button
                                                                    className="fb-header-decline-btn"
                                                                    onClick={() => handleRefuseFriend(friend.sender.id)}
                                                                >
                                                                    Từ chối
                                                                </button>
                                                            </>
                                                        )}
                                                        {friend.status === "APPROVED" && (
                                                            <p className="fb-header-text-approved">
                                                                Các bạn đã là bạn bè
                                                            </p>
                                                        )}
                                                        {friend.status === "" && (
                                                            <p className="fb-header-text-refused">
                                                                Đã từ chối lời mời
                                                            </p>
                                                        )}
                                                    </div>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* Inbox */}
                        {stateInbox && (
                            <div className="fb-header-inbox-modal">
                                {inboxList.length === 0 ? (
                                    <p className="fb-header-inbox-empty">
                                        Chưa có cuộc trò chuyện
                                    </p>
                                ) : (
                                    inboxList.map(ib => (
                                        <div key={ib.idConversation} className="fb-header-inbox-item">
                                            <img
                                                src={ib.avatar || "/default-avatar.png"}
                                                className="fb-header-inbox-avatar"
                                                alt=""
                                            />
                                            <Link
                                                to={`/chat/${ib.idUser}`}
                                                className="fb-header-inbox-link"
                                            >
                                                <div className="fb-header-inbox-info">
                                                    <p className="fb-header-inbox-name">{ib.fullName}</p>
                                                    <p className="fb-header-inbox-last">
                                                        Nhấn để nhắn tin
                                                    </p>
                                                </div>
                                            </Link>
                                        </div>
                                    ))
                                )}
                            </div>
                        )}
                    </div>
                </header>

            </>

        </ClickSpark>
    );
};

export default Header;
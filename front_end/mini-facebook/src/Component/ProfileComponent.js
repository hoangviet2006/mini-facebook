import {useEffect, useState} from "react";
import {profile, updatePassword, updateProfile} from "../Service/UserService";
import {createPost, deletePost, findPostByUser} from "../Service/PostService";
import "../css/profile.css"
import Header from "./Header";
import {toast} from "sonner";
import {useNavigate} from "react-router-dom";
import ClickSpark from "./ClickSpark";
import {Formik, Form, Field, ErrorMessage} from "formik";
import * as Yup from "yup";

const ProfileComponent = () => {
    const [user, setUser] = useState({});
    const [posts, setPosts] = useState([])
    const [token, setToken] = useState(localStorage.getItem("accessToken"));
    const [isShowModal, setIsShowModal] = useState(false);
    const [content, setContent] = useState("");
    const [file, setFile] = useState(null);
    const [fileResponse, setFileResponse] = useState(null);
    const [isUploading, setIsUploading] = useState(false);
    const [openedMenuId, setOpenedMenuId] = useState(null);
    const [isShowModalDelete, setIsShowModalDelete] = useState(false);
    const [idDelete, setIdDelete] = useState('');
    const [openMenu, setOpenMenu] = useState(false);
    const [isUpdateProfile, setIsUpdateProfile] = useState(false);
    const [avatarPreview, setAvatarPreview] = useState(null);
    const [avatar, setAvatar] = useState(null);
    const [backgroundPreview, setBackgroundPreview] = useState(null);
    const [background, setBackground] = useState(null);
    const [spinner, setSpinner] = useState(false);
    const [isUpdatePassword, setIsUpdatePassword] = useState(false);
    const navigate = useNavigate();
    useEffect(() => {
        const handleStorageChange = () => {
            const newToken = localStorage.getItem("accessToken");
            setToken(newToken);
        };
        window.addEventListener("storage", handleStorageChange);
        return () => {
            window.removeEventListener("storage", handleStorageChange);
        };
    }, []);
    useEffect(() => {
        if (!token) {
            setUser({});
            setPosts([]);
            return;
        }
        const fetchData = async () => {
            try {
                const response = await profile();
                const getPost = await findPostByUser();
                setUser(response || {});
                setPosts(getPost || []);
            } catch (e) {
                if (!e.response) {
                    toast.error("Không thể kết nối server!");
                    return;
                }
                if (e.response.status === 401) {
                    // localStorage.removeItem("accessToken");
                    setToken(null);
                    toast.warning(e?.response?.data)
                    navigate("/login");
                }
            }
        }
        fetchData();
    }, [token]);
    const handleHiddenModal = () => {
        setIsShowModal(false);
        setContent("");
        setFile(null);
        setFileResponse(null);
    }
    const handleShowModal = () => {
        setIsShowModal(true);
    }
    const handleSubmit = async () => {
        try {
            setIsUploading(true);
            const formData = new FormData();
            formData.append("content", content);
            if (fileResponse) {
                formData.append("file", fileResponse);
            } else {
                formData.append("file", null);
            }
            const newPost = await createPost(formData);
            setPosts(prev => [newPost,...prev]);
            toast.success("Đăng bài thành công");
            setContent("");
            setFile(null);
            setFileResponse(null);
            handleHiddenModal()
        } catch (err) {
                const message =
                err.response?.data||"File up load quá lớn hoặc không hợp lệ vui lòng thử lại!" ;
            toast.error(message);
        } finally {
            setIsUploading(false);
        }
    };
    const handleShowModalDeletePost = (id) => {
        setIsShowModalDelete(true);
        setIdDelete(id);
    }
    const handleDeletePost = async () => {
        try {
            const response = await deletePost(idDelete);
            setIdDelete(null);
            setIsShowModalDelete(false);
            toast.success(response)
            setTimeout(() => window.location.reload(), 1000);
        } catch (e) {
            console.error("Xoá thất bại:", e.response.data);
        }
    }
    const handleUpdateProfile = (values) => {
        const formData = new FormData();
        formData.append("bio", values?.bio)
        formData.append("fullName", values?.fullName)
        formData.append("avatar", avatar)
        formData.append("background", background);
        const fetchData = async () => {
            try {
                setSpinner(true);
                const res = await updateProfile(formData);
            } catch (e) {
                console.log(e?.response?.data);
                toast.warning(e?.response?.data)
            } finally {
                setSpinner(false);
                window.location.reload();
            }
        }
        fetchData();
        setIsUpdateProfile(false);
        setAvatar(null);
        setBackground(null);
    }
    const handleUpdatePassword = async (values) => {
        try {
            const UpdatePasswordDTO = {
                oldPassword: values.oldPassword,
                newPassword: values.newPassword,
                confirmPassword: values.confirmPassword
            }
            const response = await updatePassword(UpdatePasswordDTO);
            toast.success(response)
            setIsUpdatePassword(false)
        } catch (e) {
            toast.warning(e?.response?.data)
        }
    }
    const passwordSchema = Yup.object().shape({
        newPassword: Yup.string()
            .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/,
                "Mật khẩu phải ít nhất 6 ký tự, gồm 1 chữ hoa, 1 chữ thường và 1 số")
    });
    return (
        <ClickSpark
            sparkColor='#0c0c0c'
            sparkSize={10}
            sparkRadius={15}
            sparkCount={8}
            duration={400}
        >
            <>
                <Header/>
                <div className="profile-container">

                    <div className="cover-photo">
                        <img
                            src={user?.backgroundUrl || "https://picsum.photos/1200/300"}
                            alt="cover"
                        />
                    </div>

                    <div className="profile-header">
                        <div className="avatar">
                            <img
                                src={user?.avatarUrl || "https://picsum.photos/150"}
                                alt="avatar"
                            />
                        </div>

                        <div className="user-info">
                            <h2>{user?.fullName}</h2>
                            <p>@{user?.username}</p>
                            <p>{user?.bio || ""}</p>
                        </div>
                        <div className="edit-profile" onClick={() => setOpenMenu(!openMenu)}>
                            <span>...</span>
                            {openMenu && (
                                <div className="edit-profile-menu ">
                                    <div className="menu-item"
                                         onClick={() => setIsUpdateProfile(!isUpdateProfile)}>Chỉnh
                                        sửa hồ sơ
                                    </div>

                                    <div className="menu-item"
                                         onClick={() => setIsUpdatePassword(!isUpdatePassword)}
                                    >Đổi mật khẩu
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="right-column-only">
                        <div className="create-post-box">
                            <img src={user?.avatarUrl} className="post-avatar" alt=""/>
                            <input type="text" placeholder={`${user?.fullName} ơi,Bạn đang nghỉ gì thế?`}
                                   onClick={() => handleShowModal()}/>
                        </div>

                        {Array.isArray(posts) && posts.length > 0 ? (
                            posts.map((item) => (
                                <div className="post-card-profile" key={item?.id}>
                                    <div className="post-header-profile">
                                        <img
                                            src={item?.user?.avatarUrl || user?.avatarUrl}
                                            alt=""
                                        />
                                        <div>
                                            <h4>{item?.user?.fullName || user?.fullName}</h4>
                                            <span> {new Date(item?.createdAt).toLocaleString()}</span>
                                        </div>
                                        <div
                                            className="edit-post"
                                            onClick={() =>
                                                setOpenedMenuId(openedMenuId === item.id ? null : item.id)
                                            }
                                        >
                                            {openedMenuId === item.id ? (
                                                <div className="post-menu">
                                                    <div className="menu-item">Chỉnh sửa bài viết</div>
                                                    <div className="menu-item delete"
                                                         onClick={() => handleShowModalDeletePost(item.id)}>Xoá bài viết
                                                    </div>
                                                    <div className="menu-item">Chia sẻ bài viết</div>
                                                </div>
                                            ) : (
                                                <span>...</span>
                                            )}
                                        </div>
                                    </div>

                                    <p>{item?.content}</p>

                                    {item?.mediaType === "IMAGE" && item?.url ? (
                                        <img src={item.url} className="post-image" alt=""/>
                                    ) : null}

                                    {item?.mediaType === "VIDEO" && item?.url ? (
                                        <video className="post-image" controls>
                                            <source src={item.url} type="video/mp4"/>
                                        </video>
                                    ) : null}

                                    <div className="post-actions">
                                        <button>Like</button>
                                        <button>Comment</button>
                                        <button>Share</button>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p>Bạn chưa có bài đăng nào...</p>
                        )}

                    </div>
                </div>
                {isShowModal &&
                    <div className="modal-create-post-overlay">
                        <div className="modal-create-post-container">
                            <div className="modal-create-post-header">
                                <h3>Đăng bài</h3>
                                {!isUploading && <button
                                    className="modal-create-post-close"
                                    onClick={handleHiddenModal}
                                >
                                    ×
                                </button>
                                }

                            </div>

                            <div className="modal-create-post-user">
                                <img src={user.avatarUrl}
                                     alt="avatar"
                                     className="modal-create-post-avatar"/>
                                <span>{user.fullName}</span>
                            </div>

                            <textarea
                                className="modal-create-post-textarea"
                                placeholder={`Bạn đang nghĩ gì, ${user.fullName}?`}
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                            />

                            {file && (
                                <div className="modal-create-post-preview">
                                    {file.type.startsWith("image/") ? (
                                        <img
                                            src={URL.createObjectURL(file)}
                                            alt="preview"
                                        />
                                    ) : file.type.startsWith("video/") ? (
                                        <video controls>
                                            <source
                                                src={URL.createObjectURL(file)}
                                                type={file.type}
                                            />
                                        </video>
                                    ) : (
                                        <p>File không hỗ trợ</p>
                                    )}
                                    {!isUploading &&
                                        <button
                                            className="modal-create-post-remove"
                                            onClick={() => setFile(null)}
                                        >
                                            ×
                                        </button>
                                    }

                                </div>
                            )}

                            {!isUploading && (
                                <label className="modal-create-post-upload">
                                    Thêm ảnh / video
                                    <input
                                        type="file"
                                        accept="image/*,video/*"
                                        hidden
                                        onChange={(e) => {
                                            if (e.target.files.length > 0) {
                                                setFile(e.target.files[0]);
                                                setFileResponse(e.target.files[0]);
                                            }
                                        }}
                                    />
                                </label>
                            )}

                            <button
                                className="modal-create-post-submit"
                                onClick={handleSubmit}
                                disabled={isUploading || !content}
                            >
                                {isUploading ? "Đang đăng bài..." : "Đăng bài"}
                            </button>
                        </div>
                    </div>
                }
                {isShowModalDelete && (
                    <div className="modal-overlay-delete">
                        <div className="modal-container-delete">
                            <div className="modal-header-delete">
                            <h3>Xác nhận xoá bài viết</h3>
                                <button
                                    className="close-btn-delete"
                                    onClick={() => setIsShowModalDelete(false)}
                                >
                                    ×
                                </button>
                            </div>
                            <div className="modal-body-delete">
                                <p>Bạn có chắc chắn muốn xoá bài viết này không?</p>
                            </div>
                            <div className="modal-footer-delete">
                                <button
                                    className="cancel-btn-delete"
                                    onClick={() => setIsShowModalDelete(false)}
                                >
                                    Hủy
                                </button>
                                <button
                                    className="delete-btn-delete"
                                    onClick={() => handleDeletePost()}
                                >
                                    Xoá
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                {isUpdateProfile && (
                    <div className="modal-update-profile-overlay">
                        <div className="modal-update-profile-container">
                            <h2>Cập nhật hồ sơ</h2>

                            <Formik
                                initialValues={{
                                    fullName: user?.fullName || "",
                                    bio: user?.bio || "",
                                    avatar: null,
                                    background: null,
                                }}
                                onSubmit={handleUpdateProfile}
                            >
                                <Form>
                                    <div className="modal-update-profile-group">
                                        <label>Họ và tên</label>
                                        <Field name="fullName" type="text"/>
                                    </div>

                                    <div className="modal-update-profile-group">
                                        <label>Bio</label>
                                        <Field name="bio" as="textarea"/>
                                    </div>

                                    <div className="modal-update-profile-group">
                                        <label>Avatar</label>
                                        <input
                                            type="file"
                                            accept="image/*"
                                            onChange={(e) => {
                                                const fileAvatar = e.currentTarget.files[0];
                                                setAvatar(fileAvatar);
                                                if (fileAvatar) {
                                                    setAvatarPreview(URL.createObjectURL(fileAvatar));
                                                }
                                            }
                                            }

                                        />
                                        {avatarPreview && (
                                            <img
                                                src={avatarPreview}
                                                alt="Avatar preview"
                                                className="modal-update-profile-preview avatar"
                                            />
                                        )}
                                    </div>

                                    <div className="modal-update-profile-group">
                                        <label>Background</label>
                                        <input
                                            type="file"
                                            accept="image/*"
                                            onChange={(e) => {
                                                const fileBackground = e.currentTarget.files[0]
                                                setBackground(fileBackground)
                                                if (fileBackground) {
                                                    setBackgroundPreview(URL.createObjectURL(fileBackground));
                                                }
                                            }
                                            }
                                        />
                                        {backgroundPreview && (
                                            <img
                                                src={backgroundPreview}
                                                alt="Background preview"
                                                className="modal-update-profile-preview background"
                                            />
                                        )}
                                    </div>

                                    <div className="modal-update-profile-actions">
                                        <button type="submit" className="modal-update-profile-save">
                                            Lưu
                                        </button>
                                        <button
                                            type="button"
                                            className="modal-update-profile-cancel"
                                            onClick={() => {
                                                setAvatarPreview(null);
                                                setBackgroundPreview(null);
                                                setIsUpdateProfile(false)
                                            }}
                                        >
                                            Hủy
                                        </button>
                                    </div>
                                </Form>
                            </Formik>
                        </div>
                    </div>
                )}
                {spinner && (
                    <div className="loading-overlay">
                        <div className="spinner"></div>
                        <p className={"loading-text"}>Đang upload ảnh...</p>
                    </div>
                )}
                {isUpdatePassword && (
                    <div className={"update-password-modal-overlay"}>
                        <div className={"modal-update-password"}>
                            <h2>Cập nhật mật khẩu</h2>
                            <button
                                className="close-modal-btn"
                                onClick={() => setIsUpdatePassword(false)}
                            >
                                X
                            </button>
                            <Formik initialValues={
                                {
                                    oldPassword: "",
                                    newPassword: "",
                                    confirmPassword: ""
                                }
                            } onSubmit={handleUpdatePassword} validationSchema={passwordSchema}>
                                <Form className="update-password-form">
                                    <Field
                                        type="password"
                                        name="oldPassword"
                                        placeholder="Mật khẩu hiện tại"
                                        className="update-password-input"
                                    />
                                    <ErrorMessage name="oldPassword" component="div" className="error-message"/>
                                    <Field
                                        type="password"
                                        name="newPassword"
                                        placeholder="Mật khẩu mới"
                                        className="update-password-input"
                                    />
                                    <ErrorMessage name="newPassword" component="div" className="error-message"/>
                                    <Field
                                        type="password"
                                        name="confirmPassword"
                                        placeholder="Xác nhận mật khẩu mới"
                                        className="update-password-input"
                                    />
                                    <ErrorMessage name="confirmPassword" component="div" className="error-message"/>
                                    <button type="submit">Cập nhật</button>
                                </Form>
                            </Formik>
                        </div>
                    </div>
                )}
            </>
        </ClickSpark>
    )
}
export default ProfileComponent;
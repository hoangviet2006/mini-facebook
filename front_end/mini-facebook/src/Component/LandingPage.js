import { Link } from "react-router-dom";
import "../css/landingPage.css"
const LandingPage = () => {
    return (
        <div className="landing-wrapper">
            {/* HEADER */}
            <header className="landing-header">
                <div className="logo">FakeBook</div>
                <div className="header-actions">
                    <Link to="/login" className="btn btn-outline">
                        Đăng nhập
                    </Link>
                    <Link to="/register" className="btn btn-primary">
                        Đăng ký
                    </Link>
                </div>
            </header>

            {/* HERO */}
            <section className="landing-hero">
                <div className="hero-content">
                    <h1>Kết nối. Chia sẻ. Ở lại.</h1>
                    <p>
                        Chia sẻ khoảnh khắc, trò chuyện với bạn bè và cập nhật cuộc sống mỗi
                        ngày.
                    </p>
                    <div className="hero-buttons">
                        <Link to="/register" className="btn btn-primary btn-large">
                            Đăng ký ngay
                        </Link>
                        <Link to="/login" className="btn btn-outline btn-large">
                            Đăng nhập
                        </Link>
                    </div>
                </div>
            </section>

            {/* FEATURES */}
            <section className="landing-features">
                <div className="feature">
                    <span className="icon">🔗</span>
                    <h3>Kết nối bạn bè</h3>
                    <p>Dễ dàng tìm và giữ liên lạc với mọi người.</p>
                </div>

                <div className="feature">
                    <span className="icon">💬</span>
                    <h3>Trò chuyện tức thì</h3>
                    <p>Nhắn tin nhanh, realtime, không độ trễ.</p>
                </div>

                <div className="feature">
                    <span className="icon">📸</span>
                    <h3>Chia sẻ khoảnh khắc</h3>
                    <p>Ảnh, video, cảm xúc – tất cả trong một nơi.</p>
                </div>
            </section>

            {/* FOOTER */}
            <footer className="landing-footer">
                © 2026 FakeBook · Điều khoản · Quyền riêng tư
            </footer>
        </div>
    );
};

export default LandingPage;

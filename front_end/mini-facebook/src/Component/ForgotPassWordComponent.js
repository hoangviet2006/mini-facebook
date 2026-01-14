import {Formik, Form, Field, ErrorMessage} from "formik";
import * as Yup from "yup";
import {Link} from "react-router-dom";
import "../css/forgot-password.css";
import Header from "./Header";
import {forgotPassWord} from "../Service/UserService";
import {toast} from "sonner";
import {useState, useEffect} from "react";


const ForgotPassWordComponent = () => {
    const [loading, setLoading] = useState(false)
    const [time, setTime] = useState(0)
    useEffect(() => {
        if (time <= 0) return;

        const timer = setInterval(() => {
            setTime(pre => pre - 1)
        }, 1000)

        return () => clearInterval(timer); // dừng hàm
    }, [time]);

    const validate = Yup.object({
        email: Yup.string()
            .email("Email không hợp lệ")
            .required("Vui lòng nhập email"),
    });

    const handleSubmit = async (values) => {
        const email = {
            email: values.email
        }
        try {
            setLoading(true);
            const response = await forgotPassWord(email);
            toast.success(response);
            setTime(300);
        } catch (e) {
            toast.warning(e?.response?.data)
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="forgot-container">
            {/*<Header/>*/}

            <div className="forgot-card">
                <Formik
                    initialValues={{email: ""}}
                    validationSchema={validate}
                    onSubmit={handleSubmit}
                >
                    <Form>
                        <h3 className="forgot-title">Quên mật khẩu</h3>

                        <p className="forgot-desc">
                            Vui lòng nhập địa chỉ email đã đăng ký.
                            Chúng tôi sẽ gửi cho bạn một liên kết để đặt lại mật khẩu.
                        </p>

                        <div className="form-group">
                            <label htmlFor="email">Email</label>
                            <Field
                                id="email"
                                name="email"
                                type="email"
                                placeholder="example@gmail.com"
                                className="form-input"
                            />
                            <ErrorMessage
                                name="email"
                                component="div"
                                className="error-message"
                            />
                        </div>

                        <div className="form-group mt-3">
                            {loading ? (
                                <div className="spinner-forgot">Đang gửi...</div>
                            ) : time > 0 ? (
                                <div className="time-load">
                                    Gửi lại sau {time}s
                                </div>
                            ) : (
                                <button type="submit" className="forgot-btn">
                                    Gửi liên kết khôi phục
                                </button>
                            )}
                        </div>


                        <div className="back-login mt-3">
                            <Link to="/login">← Quay lại đăng nhập</Link>
                        </div>
                    </Form>
                </Formik>
            </div>
        </div>
    );
};

export default ForgotPassWordComponent;

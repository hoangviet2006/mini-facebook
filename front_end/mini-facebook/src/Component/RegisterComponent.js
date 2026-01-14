import {ErrorMessage, Formik, Form, Field} from "formik";
import {Button} from "react-bootstrap";
import * as Yup from "yup";
import '../css/register.css'
import {useState, useEffect} from "react";
import {getOtp, register} from "../Service/AuthService";
import {toast} from "sonner";
import {Eye, EyeOff} from "lucide-react";
import {Link, useNavigate} from "react-router-dom";
import Header from "./Header";
import ClickSpark from "./ClickSpark";

const RegisterComponent = () => {
    const [otpCountdown, setOtpCountdown] = useState(0);
    const [isLoadingOTP, setIsLoadingOTP] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    useEffect(() => {
        if (otpCountdown > 0) {
            const timer = setTimeout(() => {
                setOtpCountdown(otpCountdown - 1);
            }, 1000);
            return () => clearTimeout(timer);
        }
    }, [otpCountdown]);
    const validationSchema = Yup.object({
        fullName: Yup.string()
            .required('Vui lòng nhập họ và tên')
            .min(10, 'Họ và tên phải có ít nhất 10 ký tự'),
        username: Yup.string()
            .required('Vui lòng nhập tên tài khoản')
            .min(4, 'Tên tài khoản phải có ít nhất 4 ký tự')
            .matches(/^[a-zA-Z0-9_]+$/, 'Tên tài khoản chỉ chứa chữ, số và dấu gạch dưới'),
        password: Yup.string()
            .required('Vui lòng nhập mật khẩu')
            .min(8, 'Mật khẩu phải có ít nhất 8 ký tự')
            .matches(/[a-z]/, 'Mật khẩu phải chứa ít nhất 1 chữ thường')
            .matches(/[A-Z]/, 'Mật khẩu phải chứa ít nhất 1 chữ hoa')
            .matches(/[0-9]/, 'Mật khẩu phải chứa ít nhất 1 số'),
        confirmPassword: Yup.string()
            .required('Vui lòng xác nhận mật khẩu')
            .oneOf([Yup.ref('password'), null], 'Mật khẩu xác nhận không khớp'),
        email: Yup.string()
            .required('Vui lòng nhập địa chỉ email')
            .email('Địa chỉ email không hợp lệ'),
        code: Yup.string()
            .required('Vui lòng nhập mã OTP')
            .length(6, 'Mã OTP phải có 6 ký tự')
    });
    const navigate = useNavigate();
    const handleSubmit = async (values) => {
        const newUser = {
            fullName: values?.fullName,
            username: values?.username,
            password: values?.password,
            email: values?.email,
            code: values?.code
        };
        try {
            const registerMessage = await register(newUser);
            console.log(registerMessage)
            navigate("/login");
            toast.success("Đăng ký thành công!");
        } catch (e) {
            toast.error(e.response?.data);
        }
    };
    const handleGetOtp = async (values) => {
        const email = {
            email: values?.email
        }
        if (!email.email) {
            toast.warning("Vui lòng nhập địa chỉ email trước!")
            return;
        }
        try {
            setIsLoadingOTP(true);
            await getOtp(email);
            setIsLoadingOTP(false);
            setOtpCountdown(60);
            toast.success("OTP code đã được gửi về email của bạn!")
        } catch (e) {
            toast.error(e.response?.data);
            setIsLoadingOTP(false);
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
        <div className="register-container">
            {/*<div className="register-header">*/}
            {/*    <Header/>*/}
            {/*</div>*/}
            <div className="register-card">
                <Formik initialValues={{
                    fullName: '',
                    username: '',
                    password: '',
                    confirmPassword: '',
                    email: '',
                    code: ''
                }} validationSchema={validationSchema} onSubmit={handleSubmit}
                >
                    {({
                          values,
                          errors,
                          touched,
                      }) => (
                        <Form>
                            <h3 className="register-title">Đăng ký tài khoản</h3>
                            <p className="register-subtitle">Tạo tài khoản mới để kết nối với bạn bè</p>
                            <div className="form-group">
                                <label htmlFor="fullName">Họ và tên</label>
                                <Field id="fullName" name="fullName" placeholder="Nhập họ và tên"
                                       className={`form-input ${errors.fullName && touched.fullName ? 'error' : ''}`}
                                />
                                <ErrorMessage name="fullName" component="div" className="error-message"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="username">Tên tài khoản</label>
                                <Field id="username" name="username" placeholder="Nhập tên tài khoản"
                                       className={`form-input ${errors.username && touched.username ? 'error' : ''}`}
                                />
                                <ErrorMessage name="username" component="div" className="error-message"/>
                            </div>
                            <div className="form-group">

                                <label htmlFor="password">Mật khẩu</label>
                                <div className="password-wrapper">
                                    <Field id="password" name="password" placeholder="Nhập mật khẩu"
                                           type={showPassword ? 'text' : 'password'}
                                           className={`form-input ${errors.password && touched.password ? 'error' : ''}`}
                                    />
                                    <button
                                        type="button"
                                        className="password-toggle"
                                        onClick={() => setShowPassword(!showPassword)}
                                    >
                                        {showPassword ? <EyeOff size={20}/> : <Eye size={20}/>}
                                    </button>
                                </div>
                                <ErrorMessage name="password" component="div" className="error-message"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
                                <div className="password-wrapper">
                                    <Field id="confirmPassword" name="confirmPassword" placeholder="Xác nhận mật khẩu"
                                           type={showConfirmPassword ? 'text' : 'password'}
                                           className={`form-input ${errors.confirmPassword && touched.confirmPassword ? 'error' : ''}`}
                                    />
                                    <button
                                        type="button"
                                        className="password-toggle"
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    >
                                        {showConfirmPassword ? <EyeOff size={20}/> : <Eye size={20}/>}
                                    </button>
                                </div>
                                <ErrorMessage name="confirmPassword" component="div" className="error-message"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="email">Địa chỉ email</label>
                                <Field id="email" name="email" placeholder="Nhập địa chỉ email"
                                       className={`form-input ${errors.email && touched.email ? 'error' : ''}`}
                                />

                                <ErrorMessage name="email" component="div" className="error-message"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="code">OTP xác thực</label>
                                <Field id="code" name="code" placeholder="OTP CODE"
                                       className={`form-input ${errors.code && touched.code ? 'error' : ''}`}
                                       maxLength={6}/>
                                <ErrorMessage name="code" component="div" className="error-message"/>
                            </div>
                            <div className="button-group">
                                <button
                                    type="submit"
                                    className="register-btn"
                                >
                                    Đăng ký tài khoản
                                </button>
                                <button
                                    type="button"
                                    className="btn-secondary"
                                    onClick={() => handleGetOtp(values)}
                                    disabled={otpCountdown > 0 || isLoadingOTP}
                                >
                                    {isLoadingOTP ? (
                                        <>
                                            <span className="spinner"></span>
                                            <span>Đang gửi...</span>
                                        </>
                                    ) : otpCountdown > 0 ? (
                                        `Gửi lại sau ${otpCountdown}s`
                                    ) : (
                                        'Nhận mã OTP'
                                    )}
                                </button>
                            </div>
                            <div className="register-footer">
                                Đã có tài khoản?{' '}
                                <Link to="/login" className="register-link">Đăng nhập ngay</Link>
                            </div>
                        </Form>
                    )}
                </Formik>
            </div>
        </div>
        </ClickSpark>
    )
}
export default RegisterComponent;
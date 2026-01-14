import {ErrorMessage, Field, Form, Formik} from "formik";
import {Eye, EyeOff} from "lucide-react";
import {useState} from "react";
import "../css/Login.css"
import * as Yup from "yup";
import {login} from "../Service/AuthService";
import {Link, useNavigate} from "react-router-dom";
import Header from "./Header";
import ClickSpark from "./ClickSpark";
const LoginComponent = ()=>{
    const [showPassword,setShowPassword] = useState(false);
    const validate = Yup.object({
        username: Yup.string()
            .required('Vui lòng nhập tên tài khoản')
            .min(4, 'Tên tài khoản phải có ít nhất 4 ký tự')
            .matches(/^[a-zA-Z0-9_]+$/, 'Tên tài khoản chỉ chứa chữ, số và dấu gạch dưới'),
        password: Yup.string()
            .required('Vui lòng nhập mật khẩu')
            .min(8, 'Mật khẩu phải có ít nhất 8 ký tự')
            .matches(/[a-z]/, 'Mật khẩu phải chứa ít nhất 1 chữ thường')
            .matches(/[A-Z]/, 'Mật khẩu phải chứa ít nhất 1 chữ hoa')
            .matches(/[0-9]/, 'Mật khẩu phải chứa ít nhất 1 số')
    })
    const navigate = useNavigate();
    const [serverError, setServerError] = useState("");
    const handleLogin = async (values)=>{
        const loginRequest = {
            username:values?.username,
            password:values?.password
        }
        try {
            const response = await login(loginRequest);
            navigate("/")
            window.location.reload();
        }catch (e) {
            setServerError(e?.response?.data || "Đăng nhập thất bại");
        }
    }
    return(
        <ClickSpark
            sparkColor='#0c0c0c'
            sparkSize={10}
            sparkRadius={15}
            sparkCount={8}
            duration={400}
        >

        <div className="login-container">
            {/*<Header/>*/}
                <div className="login-header">
                </div>
                <div className="login-card">
                    <Formik initialValues={
                        {
                            username: '',
                            password: '',
                        }
                    } validationSchema={validate} onSubmit={handleLogin}>
                        {({
                              values,
                              errors,
                              touched,}) => (
                            <Form>
                                <h3 className="login-title">Đăng nhập</h3>
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
                                    <div className="forgot-password">
                                        <Link to="/forgot-password">Quên mật khẩu?</Link>
                                    </div>
                                    <ErrorMessage name="password" component="div" className="error-message"/>
                                </div>
                                {serverError && (
                                    <div className="server-error">
                                        {serverError}
                                    </div>
                                )}

                                <button
                                    type="submit"
                                    className="login-btn"
                                >
                                    Đăng nhập
                                </button>

                                <div className="register-footer">
                                    Chưa có tài khoản?{' '}
                                    <Link to="/register" className="register-link">Đăng ký ngay</Link>
                                </div>
                            </Form>
                        )
                        }
                    </Formik>
                </div>
        </div>
        </ClickSpark>
    )
}
export default LoginComponent;
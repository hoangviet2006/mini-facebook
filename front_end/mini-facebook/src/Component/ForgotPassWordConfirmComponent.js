import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";

import { Link, useSearchParams, useNavigate } from "react-router-dom";
import "../css/forgot-password-confirm.css"
import {forgotPassWordConfirm} from "../Service/UserService";
import {toast} from "sonner";
const ForgotPassWordConfirmComponent = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const navigate = useNavigate();
    const handleSubmit = async (values) => {
        const PasswordDTO = {
            password:values.password,
            confirmPassword:values.confirmPassword,
            token: token
        }
        try {
            const response = await forgotPassWordConfirm(PasswordDTO);
            toast.success(response);
            navigate("/login")
        }catch (e) {
            toast.warning(e?.response?.data);
        }
    }
    const validationSchema = Yup.object({
        password: Yup.string()
            .required("Vui lòng nhập mật khẩu mới")
            .min(8, "Mật khẩu phải ít nhất 8 ký tự")
            .matches(/[a-z]/, "Mật khẩu phải có ít nhất 1 chữ thường")
            .matches(/[A-Z]/, "Mật khẩu phải có ít nhất 1 chữ hoa")
            .matches(/[0-9]/, "Mật khẩu phải có ít nhất 1 số"),
        confirmPassword: Yup.string()
            .oneOf([Yup.ref("password")], "Mật khẩu xác nhận không khớp")
            .required("Vui lòng xác nhận mật khẩu")
    });
    return (
        <div className="forgot-confirm-container">
            <div className="forgot-confirm-card">
                <h3 className="forgot-confirm-title">Đặt lại mật khẩu</h3>
                <p className="forgot-confirm-desc">
                    Vui lòng nhập mật khẩu mới cho tài khoản của bạn.
                </p>

                <Formik
                    initialValues={{
                        password: "",
                        confirmPassword: ""
                    }}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                >
                    <Form className="forgot-confirm-form">
                        <div className="form-group">
                            <label>Mật khẩu mới</label>
                            <Field
                                type="password"
                                name="password"
                                className="form-input"
                                placeholder="Nhập mật khẩu mới"
                            />
                            <ErrorMessage name="passWord" component="div" className="error-message" />
                        </div>

                        <div className="form-group">
                            <label>Xác nhận mật khẩu</label>
                            <Field
                                type="password"
                                name="confirmPassword"
                                className="form-input"
                                placeholder="Nhập lại mật khẩu"
                            />
                            <ErrorMessage name="confirmPassWord" component="div" className="error-message" />
                        </div>

                        <button type="submit" className="btn-primary">
                            Cập nhật mật khẩu
                        </button>

                        <div className="forgot-footer">
                            <Link to="/login">← Quay về đăng nhập</Link>
                        </div>
                    </Form>
                </Formik>
            </div>
        </div>
    )
}
export default ForgotPassWordConfirmComponent;
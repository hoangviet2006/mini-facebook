import RegisterComponent from "./Component/RegisterComponent";
import {ToastContainer} from "react-toastify";
import {Toaster} from "sonner";
import {Routes, Route,Navigate} from "react-router-dom";
import LoginComponent from "./Component/LoginComponent";
import HomePageComponent from "./Component/HomePageComponent";
import ProfileComponent from "./Component/ProfileComponent";
import {useState} from "react";
import ForgotPassWordComponent from "./Component/ForgotPassWordComponent";
import ForgotPassWordConfirmComponent from "./Component/ForgotPassWordConfirmComponent";
import ProfileUserComponent from "./Component/ProfileUserComponent";
import PageChatComponent from "./Component/PageChatComponent";
function App() {
    const [token, setToken] = useState(localStorage.getItem("accessToken"));
    return (
        <>
            <ToastContainer/>
            <Toaster position="top-right" richColors/>
            <Routes>
                <Route path={"/"} element={token?<HomePageComponent/>:<Navigate to="/login"/>}></Route>
                <Route path="register" element={<RegisterComponent/>}/>
                <Route path="login" element={<LoginComponent/>}/>
                <Route path="profile" element={<ProfileComponent/>}/>
                <Route path="forgot-password" element={<ForgotPassWordComponent/>}/>
                <Route path="forgot-password-confirm" element={<ForgotPassWordConfirmComponent/>}/>
                <Route path="profile/:id" element={<ProfileUserComponent/>}/>
                <Route path="chat/:id" element={<PageChatComponent/>}/>
            </Routes>
        </>
    );
}

export default App;

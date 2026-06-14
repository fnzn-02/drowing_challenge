import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import MyPage from "../AuthForm/MyPage";
import "./TopBar.css";

const TopBar = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [showMyPage, setShowMyPage] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const isActive = (path: string) => location.pathname === path ? "active" : "";

    useEffect(() => {
        axios.get('http://localhost:8080/mypage', { withCredentials: true })
            .then(() => setIsLoggedIn(true))
            .catch(() => setIsLoggedIn(false))
    }, [location])

    const logout = () => {
        axios.post('http://localhost:8080/auth/logout', {}, { withCredentials: true })
            .then(() => { setIsLoggedIn(false); navigate('/login') })
    }
    return (
        <>
            <header className="top-bar">
                <div className="top-bar-logo" onClick={() => navigate("/")}>
                    🎨 DrawingMarket
                </div>
                <nav className="top-bar-menu">
                    {isLoggedIn ? (
                        <button className="nav-button" onClick={logout}>로그아웃</button>
                    ) : (
                        <button className={`nav-button ${isActive("/login")}`} onClick={() => navigate("/login")}>로그인</button>
                    )}
                    <button className="nav-button" onClick={() => setShowMyPage(true)}>마이페이지</button>
                </nav>
            </header>
            {showMyPage && <MyPage onClose={() => setShowMyPage(false)} />}
        </>
    );
}

export default TopBar;
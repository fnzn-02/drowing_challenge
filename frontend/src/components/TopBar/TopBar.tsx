import { useNavigate, useLocation } from "react-router-dom";
import "./TopBar.css";

const TopBar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const isActive = (path: string) => location.pathname === path ? "active" : "";

    return (
        <header className="top-bar">
            <div className="top-bar-logo" onClick={() => navigate("/mainpage")}>
                🎨 DrawingMarket
            </div>
            <nav className="top-bar-menu">
                <button 
                    className={`nav-button ${isActive("/mainpage")}`} 
                    onClick={() => navigate("/mainpage")}
                >
                    챌린지 목록
                </button>
                <button 
                    className={`nav-button ${isActive("/view")}`} 
                    onClick={() => navigate("/view")}
                >
                    그림 릴스 뷰
                </button>
                <button 
                    className={`nav-button ${isActive("/drawing")}`} 
                    onClick={() => navigate("/drawing")}
                >
                    그림 그리기
                </button>
                <button 
                    className={`nav-button ${isActive("/signup")}`} 
                    onClick={() => navigate("/signup")}
                >
                    회원가입
                </button>
            </nav>
        </header>
    );
}

export default TopBar;
import { useState, useEffect } from "react";
import api from 'axios';
import { useNavigate } from "react-router-dom";
import "./MainPage.css";

interface Challenges {
    id: number;
    title: string;
    description: string;
    startDate: string;
    endDate: string;
    status: string;
    createAt: string;
}

const MainPage = () => {
    const [challenges, setChallenges] = useState<Challenges[]>([]);
    const [search, setSearch] = useState("");
    const navigate = useNavigate();

    const filtered = challenges.filter(c =>
        c.title.toLowerCase().includes(search.toLowerCase()) ||
        c.description?.toLowerCase().includes(search.toLowerCase())
    );

    useEffect(() => {
        const fetchChallenge = async () => {
            try {
                const response = await api.get('http://localhost:8080/challenges');
                setChallenges(response.data);
            } catch (error) {
                console.error("데이터 로딩 실패:", error);
            }
        };
        fetchChallenge();
    }, []);

    return (
        <div className="main-page-container">
            <div className="page-header">
                <h1 className="page-title">🏆 진행 중인 챌린지</h1>
                <input
                    className="challenge-search"
                    type="text"
                    placeholder="챌린지 검색..."
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />
            </div>
            <div className="challenge-list">
                {filtered.length === 0 && <p className="no-result">검색 결과가 없습니다.</p>}
                {filtered.map(challenge => (
                    <div key={challenge.id} className="challenge-bar-card" onClick={() => navigate(`/view/${challenge.id}`)}>
                        <div className="challenge-title-section">
                            <span className="challenge-badge">{challenge.status}</span>
                            <h3 className="challenge-title">{challenge.title}</h3>
                        </div>
                        <div className="challenge-desc-section">
                            <p className="challenge-description">{challenge.description}</p>
                        </div>
                        <div className="challenge-date-section">
                            <span className="date-label">기간:</span>
                            <span className="challenge-date">
                                {challenge.startDate?.replace("T", " ").substring(0, 10)} ~ {challenge.endDate?.replace("T", " ").substring(0, 10)}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default MainPage;
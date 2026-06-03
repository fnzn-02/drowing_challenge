import { useState, useEffect } from "react";
import api from 'axios';
import "./viewDrowing.css";


//DB테이블을 토대로 데이터가 온다고 가정하고 만든거임
interface Drowing {
  id: number;
  comment: string;
  imagePath: string;
  likeCount: number;
  medal: string;
  challengeId: number;
  userId: number;
}


const ViewDrawing = () => {
  const [drawings, setDrawings] = useState<Drowing[]>([]);

  useEffect(() => {
    const fetchDrowing = async () => {
      try {
        const response = await api.get('http://localhost:5173/drowings'); //임시로 public폴더 아래 drowings 목업파일로 연결해둠 추후 백엔드 주소만 넣으면 정상작동
        setDrawings(response.data);
      } catch (error) {
        console.error("데이터 로딩 실패:", error);
      }
    };
    fetchDrowing();
  }, []);

  return (
    <div className="reels-container">
      {drawings.map(drawing => (
        <div key={drawing.id} id={`card-${drawing.id}`} className="reels-card">
          <img src={drawing.imagePath} alt={`drawing-${drawing.id}`} className="reels-image" />
          {drawing.medal && (
            <div className={`medal-badge ${drawing.medal.toLowerCase()}`}>
              {drawing.medal}
            </div>
          )}

          <div className="reels-side-actions">
            <div className="action-button">
              <span className="icon">❤️</span>
              <span className="count">{drawing.likeCount}</span>
            </div>
            <div className="action-button">
              <span className="icon">💬</span>
              <span className="count">댓글</span>
            </div>
          </div>

          {/* 4. 하단 정보 뷰 */}
          <div className="reels-bottom-info">
            <div className="challenge-tag">🏆 {drawing.challengeId}</div>
            <div className="user-nickname">@{drawing.userId}</div>
            <p className="drawing-comment">{drawing.comment}</p>
          </div>

        </div>
      ))}
    </div>
  );
};

export default ViewDrawing;
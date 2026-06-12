import { useState, useEffect } from "react";
import api from 'axios';
import "./viewDrowing.css";
import { useParams, useNavigate } from "react-router-dom";

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
  /* 🚨 어떤 카드의 댓글창이 열려있는지 ID로 관리 (null이면 닫힘 상태) */
  const [activeCommentCardId, setActiveCommentCardId] = useState<number | null>(null);
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDrowing = async () => {
      try {
        const response = await api.get('http://localhost:5173/drowings',{
          params:{challengeId: id}
        });

        if (id) {
          const filteredData = response.data.filter(
            (item: Drowing) => item.challengeId === Number(id)
          );
          setDrawings(filteredData);
        } else {
          setDrawings(response.data);
        }
      } catch (error) {
        console.error("데이터 로딩 실패:", error);
      }
    };
    fetchDrowing();
  }, [id]);

  const toggleComments = (cardId: number) => {
    setActiveCommentCardId(prev => (prev === cardId ? null : cardId));
  };

  return (
    <div className="reels-container">
      <button 
        className="nav-button floating-draw-btn" 
        onClick={() => navigate("/drawing")}
      >
        ✏️ 그림 그리기
      </button>

      {drawings.map(drawing => {
        const isCommentsOpen = activeCommentCardId === drawing.id;

        return (
          <div key={drawing.id} id={`card-${drawing.id}`} className="reels-card">
            {/* 🚨 이미지 영역: 댓글창이 열리면 좌측으로 부드럽게 밀려나도록 클래스 유동적 처리 */}
            <div className={`reels-media-box ${isCommentsOpen ? "shrink" : ""}`}>
              <img src={drawing.imagePath} alt={`drawing-${drawing.id}`} className="reels-image" />
            </div>

            {drawing.medal && (
              <div className={`medal-badge ${drawing.medal.toLowerCase()}`}>
                {drawing.medal}
              </div>
            )}

            {/* 🚨 사이드 액션 버튼 위치 제어 */}
            <div className={`reels-side-actions ${isCommentsOpen ? "shift" : ""}`}>
              <div className="action-button">
                <span className="icon">❤️</span>
                <span className="count">{drawing.likeCount}</span>
              </div>
              <div className="action-button" onClick={() => toggleComments(drawing.id)}>
                <span className="icon">💬</span>
                <span className="count">댓글</span>
              </div>
            </div>

            <div className="reels-bottom-info">
              <div className="challenge-tag">🏆 {drawing.challengeId}</div>
              <div className="user-nickname">ID: {drawing.userId}</div>
              <p className="drawing-comment">{drawing.comment}</p>
            </div>

            {/* 🚨 일관성 있는 디자인의 댓글 창 컴포넌트 분리 삽입 */}
            <div className={`reels-comment-section ${isCommentsOpen ? "open" : ""}`}>
              <div className="comment-header">
                <h3>댓글</h3>
                <button className="comment-close-btn" onClick={() => setActiveCommentCardId(null)}>✕</button>
              </div>
              
              <div className="comment-list">
                {/* 추후 DB 데이터 매핑 공간 (임시 목업 가이드) */}
                <div className="comment-item">
                  <span className="comment-user">@user_sample</span>
                  <span className="comment-text">그림체가 너무 제 취향이에요! 멋집니다 👍</span>
                </div>
                <div className="comment-item">
                  <span className="comment-user">@artist_kim</span>
                  <span className="comment-text">16:9 비율로 보니까 몰입감이 확 사네요.</span>
                </div>
              </div>

              <div className="comment-input-box">
                <input type="text" placeholder="댓글을 입력하세요..." className="comment-input" />
                <button className="comment-submit-btn">게시</button>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default ViewDrawing;
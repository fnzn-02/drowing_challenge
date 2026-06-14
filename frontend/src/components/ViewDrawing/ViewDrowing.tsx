import { useState, useEffect } from "react";
import api from 'axios';
import "./viewDrowing.css";
import { useParams, useNavigate } from "react-router-dom";

interface User {
  id: number;
  nickname: string;
  email: string;
}

interface Challenge {
  id: number;
  title: string;
}

interface Comment {
  id: number;
  content: string;
  user: User;
}

interface Drowing {
  id: number;
  comment: string;
  imagePath: string;
  likeCount: number;
  medal: string | null;
  challenge: Challenge;
  user: User;
  comments?: Comment[];
  isLiked?: boolean;
  commentCount?: number;
}

const ViewDrawing = () => {
  const [drawings, setDrawings] = useState<Drowing[]>([]);
  const [activeCommentCardId, setActiveCommentCardId] = useState<number | null>(null);
  const [commentText, setCommentText] = useState<string>("");
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDrowing = async () => {
      try {
        if (!id) {
          console.error("주소창에 챌린지 ID가 누락되었습니다.");
          return;
        }

        const response = await api.get(`http://localhost:8080/challenges/${id}/drawings`, { withCredentials: true });
        const fetchedDrawings: Drowing[] = response.data;

        const statusPromises = fetchedDrawings.map(async (drawing) => {
          try {
            const statusResponse = await api.get(`http://localhost:8080/drawings/${drawing.id}/likes/status`, { withCredentials: true });
            return { ...drawing, isLiked: statusResponse.data.liked };
          } catch (error) {
            console.error(`좋아요 상태 로딩 실패 (ID: ${drawing.id}):`, error);
            return { ...drawing, isLiked: false };
          }
        });

        const completedDrawings = await Promise.all(statusPromises);
        setDrawings(completedDrawings);

      } catch (error) {
        console.error("그림 데이터 로딩 실패:", error);
      }
    };
    fetchDrowing();
  }, [id]);

  const toggleComments = async (drawingId: number) => {
    if (activeCommentCardId === drawingId) {
      setActiveCommentCardId(null);
      setCommentText("");
      return;
    }

    setActiveCommentCardId(drawingId);
    setCommentText("");

    try {
      const response = await api.get(`http://localhost:8080/drawings/${drawingId}/comments`, { withCredentials: true });
      
      setDrawings(prevDrawings => 
        prevDrawings.map(drawing => 
          drawing.id === drawingId 
            ? { ...drawing, comments: response.data } 
            : drawing
        )
      );
    } catch (error) {
      console.error("실시간 댓글 로딩 실패:", error);
    }
  };

  const toggleLike = async (drawingId: number) => {
    try {
      const response = await api.post(`http://localhost:8080/drawings/${drawingId}/likes`, {}, { withCredentials: true });
      const resultMessage = response.data.message;
      const isCancel = resultMessage.includes("취소");

      setDrawings(prevDrawings =>
        prevDrawings.map(drawing => {
          if (drawing.id === drawingId) {
            return {
              ...drawing,
              likeCount: isCancel ? Math.max(0, drawing.likeCount - 1) : drawing.likeCount + 1,
              isLiked: !isCancel
            };
          }
          return drawing;
        })
      );
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
      } else {
        console.error("좋아요 처리 실패:", error);
      }
    }
  };

  const handleCommentSubmit = async (drawingId: number) => {
    if (!commentText.trim()) return;

    try {
      const response = await api.post(`http://localhost:8080/drawings/${drawingId}/comments`, {
        content: commentText
      }, { withCredentials: true });

      const newComment = response.data;

      setDrawings(prevDrawings =>
        prevDrawings.map(drawing => {
          if (drawing.id === drawingId) {
            return {
              ...drawing,
              comments: [...(drawing.comments || []), newComment]
            };
          }
          return drawing;
        })
      );

      setCommentText("");

      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
      } else {
        console.error("댓글 등록 실패:", error);
      }
    }
  };

  return (
    <div className="reels-container">
      <button 
        className="nav-button floating-draw-btn" 
        onClick={() => navigate(`/drawing/${id}`)}
      > ✏️ 그림 그리기
      </button>

      {drawings.map(drawing => {
        const isCommentsOpen = activeCommentCardId === drawing.id;

        return (
          <div key={drawing.id} id={`card-${drawing.id}`} className="reels-card">
            
            <div className={`reels-media-box ${isCommentsOpen ? "shrink" : ""}`}>
              <img src={`http://localhost:8080${drawing.imagePath}`} alt={`drawing-${drawing.id}`} className="reels-image" />
            </div>

            {drawing.medal && (
              <div className={`medal-badge ${drawing.medal.toLowerCase()}`}>
                {drawing.medal}
              </div>
            )}

            <div className={`reels-side-actions ${isCommentsOpen ? "shift" : ""}`}>
              <div className="action-button">
                <span className="icon" onClick={() => toggleLike(drawing.id)}>
                  {drawing.isLiked ? '❤️' : '🤍'}
                </span>
                <span className="count">{drawing.likeCount}</span>
              </div>
              
              <div className="action-button" onClick={() => toggleComments(drawing.id)}>
                <span className="icon">💬</span>
                <span className="count">{drawing.comments?.length ?? drawing.commentCount ?? 0}</span>
              </div>
            </div>

            <div className="reels-bottom-info">
              <div className="challenge-tag">🏆 {drawing.challenge?.title}</div>
              <div className="user-nickname">작가: {drawing.user?.nickname}</div>
              <p className="drawing-comment">{drawing.comment}</p>
            </div>

            <div className={`reels-comment-section ${isCommentsOpen ? "open" : ""}`}>
              <div className="comment-header">
                <h3>댓글</h3>
                <button className="comment-close-btn" onClick={() => toggleComments(drawing.id)}>✕</button>
              </div>
              
              <div className="comment-list">
                {drawing.comments && drawing.comments.length > 0 ? (
                  drawing.comments.map(comm => (
                    <div key={comm.id} className="comment-item">
                      <span className="comment-user">@{comm.user?.nickname}</span>
                      <span className="comment-text">{comm.content}</span>
                    </div>
                  ))
                ) : (
                  <p className="no-comment-text">가장 먼저 댓글을 남겨보세요! 💬</p>
                )}
              </div>

              <div className="comment-input-box">
                <input 
                  type="text" 
                  placeholder="댓글을 입력하세요..." 
                  className="comment-input" 
                  value={activeCommentCardId === drawing.id ? commentText : ""}
                  onChange={(e) => setCommentText(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") handleCommentSubmit(drawing.id);
                  }}
                />
                <button 
                  className="comment-submit-btn"
                  onClick={() => handleCommentSubmit(drawing.id)}
                >
                  게시
                </button>
              </div>
            </div>

          </div>
        );
      })}
    </div>
  );
};

export default ViewDrawing;
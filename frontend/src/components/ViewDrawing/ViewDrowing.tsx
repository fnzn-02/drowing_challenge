import { useState, useEffect } from "react";
import api from 'axios';
import "./viewDrowing.css";
import { useParams } from "react-router-dom";

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
  const { id } = useParams<{ id: string }>();

  useEffect(() => {
    const fetchDrowing = async () => {
      try {
        const response = await api.get('http://localhost:5173/drowings');
        
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
// src/pages/ViewDrawing.tsx
import React, { useState, useEffect } from "react";
import { mockPageData } from "./mockData.js";
import "./viewDrowing.css";

// 1. 🌟 TypeScript 환경을 위한 인터페이스(타입) 정의
interface UserType {
  nickname: string;
  email: string;
}

interface ChallengeType {
  title: string;
}

interface DrowingType {
  id: number;
  imagePath: string;
  comment: string;
  likeCount: number;
  medal: "GOLD" | "SILVER" | "BRONZE" | null; // 구체적인 리터럴 타입 지정
  createdAt: string;
  user: UserType;
  challenge: ChallengeType;
}

// 목업 데이터 구조의 타입을 위한 인터페이스
interface PageDataType {
  content: DrowingType[];
  isLast: boolean;
}

const ViewDrawing: React.FC = () => {
  // 2. 🌟 가짜 데이터와 상태 배열에 명확한 제네릭 타입 부여
  const [drawings, setDrawings] = useState<DrowingType[]>([]);
  const [page, setPage] = useState<number>(0);
  const [isLast, setIsLast] = useState<boolean>(false);

  // 3. 최초 로드 시 0번 페이지 데이터 주입
  useEffect(() => {
    // mockPageData를 인덱스 시그니처로 안전하게 접근하기 위해 타입 캐스팅
    const typedMockData = mockPageData as Record<number, PageDataType>;
    const firstData = typedMockData[0];
    
    if (firstData) {
      setDrawings(firstData.content);
      setIsLast(firstData.isLast);
    }
  }, []);

  // 4. 프리페칭(미리 가져오기) 함수
  const fetchNextPage = (): void => {
    if (isLast) return;

    const nextPage = page + 1;
    const typedMockData = mockPageData as Record<number, PageDataType>;
    const nextData = typedMockData[nextPage];

    if (nextData) {
      setDrawings((prev) => [...prev, ...nextData.content]);
      setIsLast(nextData.isLast);
      setPage(nextPage);
    }
  };

  // 5. Intersection Observer를 통한 마지막에서 2번째 카드 감시 (프리페칭 트리거)
  useEffect(() => {
    if (isLast || drawings.length === 0) return;

    const observer = new IntersectionObserver(
      (entries: IntersectionObserverEntry[]) => {
        if (entries[0].isIntersecting) {
          fetchNextPage();
        }
      },
      { threshold: 0.1 }
    );

    // 마지막에서 2번째 카드의 ID를 타겟으로 지정
    const targetIndex = drawings.length - 2;
    const targetId = drawings[targetIndex]?.id;
    const targetElement = document.getElementById(`card-${targetId}`);

    if (targetElement) {
      observer.observe(targetElement);
    }

    // 다음 카드가 세팅되면 이전 감시 대상은 연결 해제(Clean-up)
    return () => observer.disconnect();
  }, [drawings, isLast, page]);

  return (
    <div className="reels-container">
      {drawings.map((drawing: DrowingType) => (
        <div key={drawing.id} id={`card-${drawing.id}`} className="reels-card">
          
          {/* 1. 이미지 메인 뷰 */}
          <img src={drawing.imagePath} alt={`drawing-${drawing.id}`} className="reels-image" />

          {/* 2. 메달 (조건부 렌더링) */}
          {drawing.medal && (
            <div className={`medal-badge ${drawing.medal.toLowerCase()}`}>
              {drawing.medal}
            </div>
          )}

          {/* 3. 우측 액션 레이아웃 */}
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
            <div className="challenge-tag">🏆 {drawing.challenge.title}</div>
            <div className="user-nickname">@{drawing.user.nickname}</div>
            <p className="drawing-comment">{drawing.comment}</p>
          </div>

        </div>
      ))}
    </div>
  );
};

export default ViewDrawing;
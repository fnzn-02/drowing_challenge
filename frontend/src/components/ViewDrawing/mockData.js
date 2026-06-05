// src/data/mockData.js

export const mockPageData = {
  0: {
    content: [
      {
        id: 1,
        imagePath: "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=800",
        comment: "첫 챌린지 참여작입니다! 크리스마스의 설렘을 담아봤어요. 🎄",
        likeCount: 152,
        medal: "GOLD",
        createdAt: "2026-05-22T21:00:00",
        user: { nickname: "반호흐_피카소", email: "user1@test.com" },
        challenge: { title: "크리스마스 풍경 그리기" }
      },
      {
        id: 2,
        imagePath: "https://images.unsplash.com/photo-1579783928621-7a13d66a6211?w=800",
        comment: "새벽 감성 가득 담은 수채화 드로잉 🌌",
        likeCount: 84,
        medal: null,
        createdAt: "2026-05-22T21:05:00",
        user: { nickname: "새벽스케치", email: "user2@test.com" },
        challenge: { title: "새벽의 푸른 밤" }
      },
      {
        id: 3,
        imagePath: "https://images.unsplash.com/photo-1580136579312-94651dfd596d?w=800",
        comment: "마감 5분 전!! 심장이 쫄깃했네요 ㅠㅠ 🔥",
        likeCount: 45,
        medal: "BRONZE",
        createdAt: "2026-05-22T21:10:00",
        user: { nickname: "벼락치기장인", email: "user3@test.com" },
        challenge: { title: "크리스마스 풍경 그리기" }
      }
    ],
    isLast: false // 뒤에 1번 페이지가 더 있다는 힌트!
  },
  1: {
    content: [
      {
        id: 4,
        imagePath: "https://images.unsplash.com/photo-1605721911519-3dfeb3be25e7?w=800",
        comment: "사이버펑크 느낌의 네온 도시 네모네모하게 완성!",
        likeCount: 210,
        medal: "SILVER",
        createdAt: "2026-05-22T21:15:00",
        user: { nickname: "네온사인", email: "user4@test.com" },
        challenge: { title: "미래 도시 디스토피아" }
      },
      {
        id: 5,
        imagePath: "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=800",
        comment: "추상화는 언제나 어렵지만 재밌습니다. 🎨",
        likeCount: 19,
        medal: null,
        createdAt: "2026-05-22T21:20:00",
        user: { nickname: "추상메서드", email: "user5@test.com" },
        challenge: { title: "내 마음의 색채" }
      }
    ],
    isLast: true // 이제 진짜 마지막 데이터라는 힌트
  }
};
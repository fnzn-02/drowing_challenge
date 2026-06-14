export const abc = () => {
  const canvas = document.getElementById('timelapse-canvas') as HTMLCanvasElement | null;
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // 로드된 이미지 배열
  const images: HTMLImageElement[] = []; 
  let currentFrame = 0;
  const fps = 30; // 초당 프레임 수

  function drawNextFrame() {
    if (images.length === 0) return;
    if (!canvas || !ctx) return;
    
    // 현재 프레임의 이미지 그리기
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.drawImage(images[currentFrame], 0, 0, canvas.width, canvas.height);
    
    // 다음 프레임 계산
    currentFrame = (currentFrame + 1) % images.length;
    
    // 지정된 FPS에 맞춰 다음 프레임 호출
    setTimeout(() => {
      requestAnimationFrame(drawNextFrame);
    }, 1000 / fps);
  }

  // 애니메이션 시작
  drawNextFrame();
};
export default abc;
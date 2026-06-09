import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import "./Drawing.css";


const Drawing = () => {

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [ctx, setCtx] = useState<CanvasRenderingContext2D | null>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [color, setColor] = useState("#000000");
  const [lineWidth, setLineWidth] = useState(5);
const [title, setTitle] = useState(() => {
  return sessionStorage.getItem("savedTitle") || "";
});
      const SubmitDrawing = async () => {
        try {
            const response = await axios.post('/api/Drawings', {
                title: title,
                json: canvasRef.current?.toDataURL()
                
            });
            alert("등록 성공!");
            sessionStorage.removeItem("savedTitle");
        }catch (error) {
            console.error('등록 중 오류 발생 : ', error);
            alert("등록 실패!");
        }};
        
    useEffect(() => {
        sessionStorage.setItem('savedTitle', title);
    }, [title]);


useEffect(() => {
    const canvas = canvasRef.current;
    if (canvas) {
      canvas.width = 800;
      canvas.height = 600;
      
      const context = canvas.getContext("2d");
      if (context) {
        context.lineCap = "round";
        context.lineJoin = "round";
        context.strokeStyle = color;
        context.lineWidth = lineWidth;
        setCtx(context);

        // [추가] 새로고침 시 세션스토리지에서 기존 그림 데이터 읽어오기
        const savedDrawing = sessionStorage.getItem("savedDrawing");
        if (savedDrawing) {
          const img = new Image();
          img.src = savedDrawing;
          // 이미지가 로드된 후 캔버스에 다시 그려줌
          img.onload = () => {
            context.drawImage(img, 0, 0);
          };
        }
      }
    }
  }, []);

  useEffect(() => {
    if (ctx) {
      ctx.strokeStyle = color;
      ctx.lineWidth = lineWidth;
    }
  }, [color, lineWidth, ctx]);

  const startDrawing = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!ctx) return;
    const { offsetX, offsetY } = e.nativeEvent;
    ctx.beginPath();
    ctx.moveTo(offsetX, offsetY);
    setIsDrawing(true);
  };

  const draw = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!isDrawing || !ctx) return;
    const { offsetX, offsetY } = e.nativeEvent;
    ctx.lineTo(offsetX, offsetY);
    ctx.stroke();
  };
const stopDrawing = () => {
    if (!ctx || !canvasRef.current) return;
    ctx.closePath();
    setIsDrawing(false);

    const currentDrawing = canvasRef.current.toDataURL();
    sessionStorage.setItem("savedDrawing", currentDrawing);
  };

const clearCanvas = () => {
    if (!canvasRef.current || !ctx) return;
    ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
    
    sessionStorage.removeItem("savedDrawing");
  };



  return (
<div className="drawing-container">
    <input 
    className="title-input" 
    onChange={(e) => setTitle(e.target.value)} 
    placeholder="제목을 입력하세요" 
    value={title}/>
        <div className="controls">
        <div className="control-group">
          <label>Color:</label>
          <input
            type="color"
            value={color}
            onChange={(e) => setColor(e.target.value)}
            title="Change Color"
          />
        </div>
         <button className="eraser-btn" onClick={()=>setColor("#ffffff")}>지우개</button>
        <div className="control-group">
          <label>Brush Size: {lineWidth}</label>
          <input
            type="range"
            min="1"
            max="30"
            value={lineWidth}
            onChange={(e) => setLineWidth(Number(e.target.value))}
            title="Brush Size"
          />
        </div>
        <button className="clear-btn" onClick={clearCanvas}>Clear All</button>
      </div>
      <canvas
        ref={canvasRef}
        onMouseDown={startDrawing}
        onMouseMove={draw}
        onMouseUp={stopDrawing}
        onMouseLeave={stopDrawing}
        className="drawing-canvas"
      ></canvas>
      <button className="submit-btn" onClick={SubmitDrawing}>
        제출
      </button>
    </div>
  );
};

export default Drawing;

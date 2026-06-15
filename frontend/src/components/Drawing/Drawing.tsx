import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import "./Drawing.css";

const API = "http://13.125.216.43:8080";

type Tool = 'pen' | 'eraser' | 'fill' | 'picker';

const Drawing = () => {
  const { id: routeId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const challengeId = routeId || searchParams.get("challengeId") || searchParams.get("id") || "1";

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const ctxRef = useRef<CanvasRenderingContext2D | null>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [color, setColor] = useState("#000000");
  const [lineWidth, setLineWidth] = useState(5);
  const [tool, setTool] = useState<Tool>('pen');
  const [historyState, setHistoryState] = useState<{ list: string[]; index: number }>({
    list: [],
    index: -1,
  });

  const [title, setTitle] = useState("");

  const [timeLeft, setTimeLeft] = useState(300);
  const [timeExpired, setTimeExpired] = useState(false);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const formatTime = (s: number) =>
    `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`;

  useEffect(() => {
    timerRef.current = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timerRef.current!);
          setTimeExpired(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => { if (timerRef.current) clearInterval(timerRef.current); };
  }, []);

  const handleRestart = () => {
    if (timerRef.current) clearInterval(timerRef.current);
    clearCanvas();
    setTimeLeft(300);
    setTimeExpired(false);
    timerRef.current = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timerRef.current!);
          setTimeExpired(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const SubmitDrawing = async () => {
    if (!canvasRef.current) return;
    try {
      const dataUrl = canvasRef.current.toDataURL("image/png");
      const blob = await (await fetch(dataUrl)).blob();
      const file = new File([blob], "drawing.png", { type: "image/png" });

      const formData = new FormData();
      formData.append("image", file);
      formData.append("comment", title);

      await axios.post(`${API}/challenges/${challengeId}/draw`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
        withCredentials: true,
      });

      sessionStorage.removeItem("savedTitle");
      sessionStorage.removeItem("savedDrawing");
      navigate(`/view/${challengeId}`);
    } catch (error) {
      console.error("등록 중 오류 발생 : ", error);
      alert("등록 실패!");
    }
  };

  useEffect(() => {
    axios.get(`${API}/challenges/${challengeId}/draw/check`, { withCredentials: true })
      .then(res => { if (res.data === true) navigate(`/view/${challengeId}`); })
      .catch(() => {});
  }, [challengeId, navigate]);

  useEffect(() => {
    sessionStorage.setItem('savedTitle', title);
  }, [title]);

  const saveToHistory = (dataUrl: string) => {
    setHistoryState((prev) => {
      const newList = prev.list.slice(0, prev.index + 1);
      newList.push(dataUrl);
      return {
        list: newList,
        index: newList.length - 1,
      };
    });
  };

  useEffect(() => {
    const canvas = canvasRef.current;
    if (canvas) {
      canvas.width = 800;
      canvas.height = 600;

      const context = canvas.getContext("2d", { willReadFrequently: true });
      if (context) {
        context.lineCap = "round";
        context.lineJoin = "round";
        context.strokeStyle = color;
        context.lineWidth = lineWidth;
        ctxRef.current = context;

        context.fillStyle = "#ffffff";
        context.fillRect(0, 0, canvas.width, canvas.height);
        const initialData = canvas.toDataURL();
        setHistoryState({
          list: [initialData],
          index: 0,
        });
      }
    }
  }, []);

  useEffect(() => {
    const ctx = ctxRef.current;
    if (ctx) {
      ctx.strokeStyle = tool === 'eraser' ? '#ffffff' : color;
      ctx.lineWidth = lineWidth;
    }
  }, [color, lineWidth, tool]);

  const startDrawing = (e: React.MouseEvent<HTMLCanvasElement>) => {
    const ctx = ctxRef.current;
    if (!ctx || !canvasRef.current || timeExpired) return;
    const { offsetX, offsetY } = e.nativeEvent;

    const x = Math.floor(offsetX);
    const y = Math.floor(offsetY);

    if (tool === 'fill') {
      floodFill(x, y);
    } else if (tool === 'picker') {
      pickColor(x, y);
    } else {
      ctx.beginPath();
      ctx.moveTo(offsetX, offsetY);
      setIsDrawing(true);
    }
  };

  const draw = (e: React.MouseEvent<HTMLCanvasElement>) => {
    const ctx = ctxRef.current;
    if (!isDrawing || !ctx) return;
    const { offsetX, offsetY } = e.nativeEvent;
    ctx.lineTo(offsetX, offsetY);
    ctx.stroke();
  };

  const stopDrawing = () => {
    const ctx = ctxRef.current;
    if (!ctx || !canvasRef.current) return;
    if (isDrawing) {
      ctx.closePath();
      setIsDrawing(false);

      const currentDrawing = canvasRef.current.toDataURL();
      sessionStorage.setItem("savedDrawing", currentDrawing);
      saveToHistory(currentDrawing);
    }
  };

  const clearCanvas = () => {
    const ctx = ctxRef.current;
    if (!canvasRef.current || !ctx) return;
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, canvasRef.current.width, canvasRef.current.height);

    const currentDrawing = canvasRef.current.toDataURL();
    sessionStorage.setItem("savedDrawing", currentDrawing);
    saveToHistory(currentDrawing);
  };

  const handleColorChange = (newColor: string) => {
    setColor(newColor);
    if (tool === 'eraser' || tool === 'picker') {
      setTool('pen');
    }
  };

  const handleUndo = () => {
    const ctx = ctxRef.current;
    if (historyState.index > 0 && canvasRef.current && ctx) {
      const prevIndex = historyState.index - 1;
      const prevData = historyState.list[prevIndex];
      const img = new Image();
      img.src = prevData;
      img.onload = () => {
        ctx.clearRect(0, 0, canvasRef.current!.width, canvasRef.current!.height);
        ctx.drawImage(img, 0, 0);
        setHistoryState((prev) => ({ ...prev, index: prevIndex }));
        sessionStorage.setItem("savedDrawing", prevData);
      };
    }
  };

  const hexToRgba = (hex: string): [number, number, number, number] => {
    let c = hex.substring(1);
    if (c.length === 3) {
      c = c[0] + c[0] + c[1] + c[1] + c[2] + c[2];
    }
    const num = parseInt(c, 16);
    return [
      (num >> 16) & 255,
      (num >> 8) & 255,
      num & 255,
      255
    ];
  };

  const floodFill = (startX: number, startY: number) => {
    const ctx = ctxRef.current;
    if (!canvasRef.current || !ctx) return;
    const width = canvasRef.current.width;
    const height = canvasRef.current.height;

    const imageData = ctx.getImageData(0, 0, width, height);
    const data = imageData.data;

    const targetColor = hexToRgba(color);
    const startIdx = (startY * width + startX) * 4;
    const startR = data[startIdx];
    const startG = data[startIdx + 1];
    const startB = data[startIdx + 2];
    const startA = data[startIdx + 3];

    if (
      startR === targetColor[0] &&
      startG === targetColor[1] &&
      startB === targetColor[2] &&
      startA === targetColor[3]
    ) {
      return;
    }

    const stack: [number, number][] = [[startX, startY]];

    data[startIdx] = targetColor[0];
    data[startIdx + 1] = targetColor[1];
    data[startIdx + 2] = targetColor[2];
    data[startIdx + 3] = targetColor[3];

    while (stack.length > 0) {
      const [cx, cy] = stack.pop()!;

      const directions: [number, number][] = [
        [cx - 1, cy],
        [cx + 1, cy],
        [cx, cy - 1],
        [cx, cy + 1]
      ];

      for (const [nx, ny] of directions) {
        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
          const idx = (ny * width + nx) * 4;
          if (
            data[idx] === startR &&
            data[idx + 1] === startG &&
            data[idx + 2] === startB &&
            data[idx + 3] === startA
          ) {
            data[idx] = targetColor[0];
            data[idx + 1] = targetColor[1];
            data[idx + 2] = targetColor[2];
            data[idx + 3] = targetColor[3];
            stack.push([nx, ny]);
          }
        }
      }
    }

    ctx.putImageData(imageData, 0, 0);

    const currentDrawing = canvasRef.current.toDataURL();
    sessionStorage.setItem("savedDrawing", currentDrawing);
    saveToHistory(currentDrawing);
  };

  const pickColor = (x: number, y: number) => {
    const ctx = ctxRef.current;
    if (!ctx || !canvasRef.current) return;
    const imgData = ctx.getImageData(x, y, 1, 1).data;
    const r = imgData[0];
    const g = imgData[1];
    const b = imgData[2];
    const hex = "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
    setColor(hex);
    setTool('pen');
  };

  return (
    <div className="drawing-container">
      <div className="controls">
        <div className="control-group">
          <label>Color:</label>
          <input
            type="color"
            value={color}
            onChange={(e) => handleColorChange(e.target.value)}
            title="Change Color"
          />
        </div>

        <div className="tool-group">
          <button
            className={`tool-btn ${tool === 'pen' ? 'active' : ''}`}
            onClick={() => setTool('pen')}
            title="펜"
          >
            펜
          </button>
          <button
            className={`tool-btn ${tool === 'eraser' ? 'active' : ''}`}
            onClick={() => setTool('eraser')}
            title="지우개"
          >
            지우개
          </button>
          <button
            className={`tool-btn ${tool === 'fill' ? 'active' : ''}`}
            onClick={() => setTool('fill')}
            title="채우기"
          >
            채우기
          </button>
          <button
            className={`tool-btn ${tool === 'picker' ? 'active' : ''}`}
            onClick={() => setTool('picker')}
            title="스포이드"
          >
            스포이드
          </button>
        </div>

        <button
          className="undo-btn"
          onClick={handleUndo}
          disabled={historyState.index <= 0}
          title="뒤로가기"
        >
          뒤로가기
        </button>

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
        <div className={`timer-display ${timeLeft <= 60 ? 'warning' : ''}`}>
          ⏱ {formatTime(timeLeft)}
        </div>
      </div>
      <div style={{ position: 'relative' }}>
        <canvas
          ref={canvasRef}
          onMouseDown={startDrawing}
          onMouseMove={draw}
          onMouseUp={stopDrawing}
          onMouseLeave={stopDrawing}
          className="drawing-canvas"
        ></canvas>
        {timeExpired && (
          <div className="timer-expired-overlay">
            <div className="timer-expired-modal">
              <h3>⏰ 시간 초과!</h3>
              <p>어떻게 하시겠어요?</p>
              <button className="submit-btn" onClick={SubmitDrawing}>제출하기</button>
              <button className="restart-btn" onClick={handleRestart}>다시 그리기</button>
            </div>
          </div>
        )}
      </div>
      <div className="bottom-row">
        <input
          className="title-input"
          onChange={(e) => setTitle(e.target.value)}
          placeholder="한마디 남기기..."
          value={title}
        />
        <button className="submit-btn" onClick={SubmitDrawing}>
          제출
        </button>
      </div>
    </div>
  );
};

export default Drawing;

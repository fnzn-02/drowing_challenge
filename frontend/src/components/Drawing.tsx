import { useEffect, useRef, useState } from "react";

const Drawing = () => {
	const canvasRef = useRef(null);
    const [getCtx, setGetCtx] = useState(null);
    
    useEffect(() => {
    	const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");
        setGetctx(ctx);
    }, [])
    
return(
	<canvas
    	ref={canvasRef}
        onMouseDown={() => setPainting(true)}
        onMouseUp={() => setPainting(false)}
        onMouseMove={e => drawFn(e)}
        onMouseLeave={() => setPainting(false)}
    >
    </canvas>
);
 };export default Drawing;
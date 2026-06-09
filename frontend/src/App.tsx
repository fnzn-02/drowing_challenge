import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ViewDrawing from './components/ViewDrawing/ViewDrowing'
import Drawing from './components/Drawing/Drawing'
import Signup from './components/AuthForm/Signup'
import MainPage from './components/MainPage/MainPage';
import TopBar from './components/TopBar/TopBar';

function App() {
  return (
    <BrowserRouter>
      {/* 화면 전체 주소를 이동해도 언제나 상단 자리를 플로팅 고정 유지 */}
      <TopBar />

      <Routes>
        <Route path="/mainpage" element={<MainPage />} />
        <Route path="/view" element={<ViewDrawing />} />
        <Route path="/drawing" element={<Drawing />} />
        <Route path="/signup" element={<Signup />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
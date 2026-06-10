import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import ViewDrawing from './components/ViewDrawing/ViewDrowing'
import Drawing from './components/Drawing/Drawing'
import Signup from './components/AuthForm/Signup'
import Login from './components/AuthForm/Login'

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/view" element={<ViewDrawing/>} />
        <Route path="/drawing" element={<Drawing />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

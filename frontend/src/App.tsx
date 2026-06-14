import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import ViewDrawing from './components/ViewDrawing/ViewDrowing'
import Drawing from './components/Drawing/Drawing'
import Signup from './components/AuthForm/Signup'

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/view" element={<ViewDrawing/>} />   
        <Route path="/drawing" element={<Drawing />} />   
        <Route path="/signup" element={<Signup />} />    
      </Routes>
    </BrowserRouter>
  )
}

export default App

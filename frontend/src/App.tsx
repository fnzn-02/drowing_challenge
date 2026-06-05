import { useState } from 'react'
import './App.css'
import ViewDrawing from './viewDrowing/ViewDrowing'
import Drawing from './components/Drawing'

function App() {

  return (
    <div>
      <ViewDrawing/>
      <Drawing />
    </div>
  )
}

export default App

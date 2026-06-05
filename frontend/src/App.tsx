
import './App.css'
import { useState } from 'react'
import ViewDrawing from './viewDrowing/ViewDrowing'
import Drawing from './components/Drawing'
import Signup from './components/components1/Signup'

function App() {

  return (
    <div>
      <ViewDrawing/>
      <Drawing />
      <Signup />
    </div>
  )
}

export default App

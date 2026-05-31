import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Tournaments from './pages/Tournaments'
import Teams from './pages/Teams'
import Players from './pages/Players'
import Chat from './pages/Chat'
import Navbar from './components/Navbar'
import PrivateRoute from './components/PrivateRoute'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<PrivateRoute><Navbar /></PrivateRoute>}>
          <Route index element={<Navigate to="/tournaments" />} />
          <Route path="tournaments" element={<Tournaments />} />
          <Route path="teams" element={<Teams />} />
          <Route path="players" element={<Players />} />
          <Route path="chat" element={<Chat />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

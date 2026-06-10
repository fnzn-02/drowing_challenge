import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './Login.css'

const API = 'http://localhost:8080'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const login = async () => {
    setError('')
    setLoading(true)
    try {
      await axios.post(`${API}/auth/login`, { email, password }, { withCredentials: true })
      navigate('/view')
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '로그인에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-box">
        <h2 className="login-title">로그인</h2>

        <div className="login-form">
          <input
            className="login-input"
            type="email"
            placeholder="이메일"
            value={email}
            onChange={e => setEmail(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && login()}
          />
          <input
            className="login-input"
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={e => setPassword(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && login()}
          />
          <button className="login-btn" onClick={login} disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
          <button className="login-btn-secondary" onClick={() => navigate('/signup')}>
            회원가입
          </button>
        </div>

        {error && <p className="login-error">{error}</p>}
      </div>
    </div>
  )
}

export default Login

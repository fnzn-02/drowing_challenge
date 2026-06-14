import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './Signup.css'

const API = 'http://localhost:8080'

function Signup() {
  const navigate = useNavigate()
  const [step, setStep] = useState(1)
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [password, setPassword] = useState('')
  const [passwordConfirm, setPasswordConfirm] = useState('')
  const [nickname, setNickname] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const sendEmail = async () => {
    setError('')
    setLoading(true)
    try {
      await axios.post(`${API}/auth/email`, { email })
      setStep(2)
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '이메일 발송에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const verifyCode = async () => {
    setError('')
    setLoading(true)
    try {
      await axios.post(`${API}/auth/verify`, { email, code })
      setStep(3)
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '인증에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const signup = async () => {
    setError('')
    setLoading(true)
    try {
      await axios.post(`${API}/auth/signup`, { email, password, passwordConfirm, nickname })
      await axios.post(`${API}/auth/login`, { email, password }, { withCredentials: true })
      alert('회원가입이 완료됐습니다!')
      navigate('/')
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="signup-container">
      <div className="signup-box">
        <h2 className="signup-title">회원가입</h2>

        <div className="signup-steps">
          <span className={step >= 1 ? 'step active' : 'step'}>1</span>
          <span className="step-line" />
          <span className={step >= 2 ? 'step active' : 'step'}>2</span>
          <span className="step-line" />
          <span className={step >= 3 ? 'step active' : 'step'}>3</span>
        </div>

        {step === 1 && (
          <div className="signup-form">
            <p className="signup-desc">가입할 이메일을 입력해주세요.</p>
            <input
              className="signup-input"
              type="email"
              placeholder="이메일"
              value={email}
              onChange={e => setEmail(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && sendEmail()}
            />
            <button className="signup-btn" onClick={sendEmail} disabled={loading}>
              {loading ? '발송 중...' : '인증코드 받기'}
            </button>
          </div>
        )}

        {step === 2 && (
          <div className="signup-form">
            <p className="signup-desc">{email}로 인증코드를 발송했어요.</p>
            <input
              className="signup-input"
              type="text"
              placeholder="인증코드 6자리"
              value={code}
              onChange={e => setCode(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && verifyCode()}
            />
            <button className="signup-btn" onClick={verifyCode} disabled={loading}>
              {loading ? '확인 중...' : '인증 확인'}
            </button>
            <button className="signup-btn-secondary" onClick={() => setStep(1)}>
              이메일 다시 입력
            </button>
          </div>
        )}

        {step === 3 && (
          <div className="signup-form">
            <p className="signup-desc">정보를 입력해주세요.</p>
            <input
              className="signup-input"
              type="password"
              placeholder="비밀번호 (8자 이상, 특수문자 포함)"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
            <input
              className="signup-input"
              type="password"
              placeholder="비밀번호 확인"
              value={passwordConfirm}
              onChange={e => setPasswordConfirm(e.target.value)}
            />
            <input
              className="signup-input"
              type="text"
              placeholder="닉네임"
              value={nickname}
              onChange={e => setNickname(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && signup()}
            />
            <button className="signup-btn" onClick={signup} disabled={loading}>
              {loading ? '처리 중...' : '가입하기'}
            </button>
          </div>
        )}

        {error && <p className="signup-error">{error}</p>}
      </div>
    </div>
  )
}

export default Signup

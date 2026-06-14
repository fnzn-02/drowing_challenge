import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './MyPage.css'

const API = 'http://localhost:8080'

interface User {
  id: number
  email: string
  nickname: string
  createdAt: string
}

interface MyPageProps {
  onClose: () => void
}

function MyPage({ onClose }: MyPageProps) {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [tab, setTab] = useState<'info' | 'nickname' | 'password' | 'withdraw'>('info')

  const [nickname, setNickname] = useState('')
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [newPasswordConfirm, setNewPasswordConfirm] = useState('')
  const [withdrawPassword, setWithdrawPassword] = useState('')

  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const fetchUser = async () => {
    try {
      const res = await axios.get(`${API}/mypage`, { withCredentials: true })
      setUser(res.data)
    } catch {
      onClose()
      navigate('/login')
    }
  }

  useEffect(() => {
    axios.get(`${API}/mypage`, { withCredentials: true })
      .then(res => setUser(res.data))
      .catch(() => { onClose(); navigate('/login', { state: { message: '로그인이 필요합니다.' } }) })
  }, [navigate, onClose])

  const handleNickname = async () => {
    setError('')
    setSuccess('')
    setLoading(true)
    try {
      await axios.patch(`${API}/mypage/nickname`, { nickname }, { withCredentials: true })
      setSuccess('닉네임이 변경됐습니다.')
      fetchUser()
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '닉네임 변경에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handlePassword = async () => {
    setError('')
    setSuccess('')
    setLoading(true)
    try {
      await axios.patch(`${API}/mypage/password`, { currentPassword, newPassword, newPasswordConfirm }, { withCredentials: true })
      setSuccess('비밀번호가 변경됐습니다.')
      setCurrentPassword('')
      setNewPassword('')
      setNewPasswordConfirm('')
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '비밀번호 변경에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handleWithdraw = async () => {
    if (!confirm('정말 탈퇴하시겠습니까?')) return
    setError('')
    setLoading(true)
    try {
      await axios.delete(`${API}/mypage/withdraw`, {
        data: { password: withdrawPassword },
        withCredentials: true,
      })
      alert('탈퇴가 완료됐습니다.')
      window.location.href = '/'
    } catch (e: unknown) {
      setError(axios.isAxiosError(e) ? e.response?.data : '회원탈퇴에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const resetMessages = () => {
    setError('')
    setSuccess('')
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">마이페이지</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>

        <div className="modal-tabs">
          <button className={tab === 'info' ? 'tab active' : 'tab'} onClick={() => { setTab('info'); resetMessages() }}>내 정보</button>
          <button className={tab === 'nickname' ? 'tab active' : 'tab'} onClick={() => { setTab('nickname'); resetMessages() }}>닉네임 수정</button>
          <button className={tab === 'password' ? 'tab active' : 'tab'} onClick={() => { setTab('password'); resetMessages() }}>비밀번호 수정</button>
          <button className={tab === 'withdraw' ? 'tab active' : 'tab'} onClick={() => { setTab('withdraw'); resetMessages() }}>회원탈퇴</button>
        </div>

        <div className="modal-content">
          {tab === 'info' && user && (
            <div className="info-section">
              <div className="info-row">
                <span className="info-label">이메일</span>
                <span className="info-value">{user.email}</span>
              </div>
              <div className="info-row">
                <span className="info-label">닉네임</span>
                <span className="info-value">{user.nickname}</span>
              </div>
              <div className="info-row">
                <span className="info-label">가입일</span>
                <span className="info-value">{new Date(user.createdAt).toLocaleDateString('ko-KR')}</span>
              </div>
            </div>
          )}

          {tab === 'nickname' && (
            <div className="form-section">
              <input
                className="modal-input"
                type="text"
                placeholder="새 닉네임"
                value={nickname}
                onChange={e => setNickname(e.target.value)}
              />
              <button className="modal-btn" onClick={handleNickname} disabled={loading}>
                {loading ? '처리 중...' : '변경하기'}
              </button>
            </div>
          )}

          {tab === 'password' && (
            <div className="form-section">
              <input
                className="modal-input"
                type="password"
                placeholder="현재 비밀번호"
                value={currentPassword}
                onChange={e => setCurrentPassword(e.target.value)}
              />
              <input
                className="modal-input"
                type="password"
                placeholder="새 비밀번호 (8자 이상, 특수문자 포함)"
                value={newPassword}
                onChange={e => setNewPassword(e.target.value)}
              />
              <input
                className="modal-input"
                type="password"
                placeholder="새 비밀번호 확인"
                value={newPasswordConfirm}
                onChange={e => setNewPasswordConfirm(e.target.value)}
              />
              <button className="modal-btn" onClick={handlePassword} disabled={loading}>
                {loading ? '처리 중...' : '변경하기'}
              </button>
            </div>
          )}

          {tab === 'withdraw' && (
            <div className="form-section">
              <p className="withdraw-warn">탈퇴하면 모든 데이터가 삭제됩니다.</p>
              <input
                className="modal-input"
                type="password"
                placeholder="비밀번호 확인"
                value={withdrawPassword}
                onChange={e => setWithdrawPassword(e.target.value)}
              />
              <button className="modal-btn danger" onClick={handleWithdraw} disabled={loading}>
                {loading ? '처리 중...' : '탈퇴하기'}
              </button>
            </div>
          )}

          {error && <p className="modal-error">{error}</p>}
          {success && <p className="modal-success">{success}</p>}
        </div>
      </div>
    </div>
  )
}

export default MyPage

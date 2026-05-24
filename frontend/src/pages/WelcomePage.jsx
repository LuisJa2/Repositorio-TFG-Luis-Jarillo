import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, registro } from '../api/api'
import { useAuth } from '../context/AuthContext'

function LoginModal({ onClose, onSwitch }) {
  const { saveSession } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login(email, password)
      saveSession(res.data)
      navigate('/home')
    } catch (err) {
      if (!err.response) {
        setError('No se puede conectar con el servidor. ¿Está corriendo el backend?')
      } else if (err.response.status === 401) {
        setError('Email o contraseña incorrectos.')
      } else {
        setError(err.response?.data?.mensaje ?? 'Error al iniciar sesión.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2 className="modal-title">Iniciar sesión</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" placeholder="tu@email.com" value={email}
              onChange={e => setEmail(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Contraseña</label>
            <input type="password" placeholder="••••••••" value={password}
              onChange={e => setPassword(e.target.value)} required />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <div className="modal-footer">
            <button type="button" className="btn btn-ghost btn-full" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
              {loading ? 'Entrando...' : 'Entrar'}
            </button>
          </div>
        </form>
        <p style={{ textAlign: 'center', marginTop: '16px', fontSize: '14px', color: 'var(--text-secondary)' }}>
          ¿No tienes cuenta?{' '}
          <button onClick={onSwitch} style={{ background: 'none', border: 'none', color: 'var(--primary)', fontWeight: 600, cursor: 'pointer' }}>
            Regístrate
          </button>
        </p>
      </div>
    </div>
  )
}

function RegisterModal({ onClose, onSwitch }) {
  const { saveSession } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (password.length < 6) { setError('La contraseña debe tener al menos 6 caracteres.'); return }
    setLoading(true)
    try {
      const res = await registro(username, email, password)
      saveSession(res.data)
      navigate('/home')
    } catch (err) {
      if (!err.response) {
        setError('No se puede conectar con el servidor. ¿Está corriendo el backend?')
      } else if (err.response.status === 409) {
        setError('Ese email o nombre de usuario ya existe.')
      } else {
        setError(err.response?.data?.mensaje ?? 'Error al registrarse. Inténtalo de nuevo.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2 className="modal-title">Crear cuenta</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nombre de usuario</label>
            <input type="text" placeholder="ej: jugador123" value={username}
              onChange={e => setUsername(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" placeholder="tu@email.com" value={email}
              onChange={e => setEmail(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Contraseña</label>
            <input type="password" placeholder="Mín. 6 caracteres" value={password}
              onChange={e => setPassword(e.target.value)} required />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <div className="modal-footer">
            <button type="button" className="btn btn-ghost btn-full" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
              {loading ? 'Registrando...' : 'Registrarse'}
            </button>
          </div>
        </form>
        <p style={{ textAlign: 'center', marginTop: '16px', fontSize: '14px', color: 'var(--text-secondary)' }}>
          ¿Ya tienes cuenta?{' '}
          <button onClick={onSwitch} style={{ background: 'none', border: 'none', color: 'var(--primary)', fontWeight: 600, cursor: 'pointer' }}>
            Iniciar sesión
          </button>
        </p>
      </div>
    </div>
  )
}

export default function WelcomePage() {
  const [modal, setModal] = useState(null)
  const navigate = useNavigate()
  const { user } = useAuth()

  if (user) { navigate('/home'); return null }

  return (
    <div className="welcome-page">
      <div className="welcome-hero">
        <div className="welcome-logo">🏆</div>
        <h1 className="welcome-title">Arena<span>Mix</span></h1>
        <p className="welcome-subtitle">Crea y gestiona torneos deportivos fácilmente</p>

        <div className="welcome-actions">
          <button className="btn btn-primary" onClick={() => setModal('login')}>
            Iniciar sesión
          </button>
          <button className="btn btn-outline" style={{ borderColor: 'rgba(255,255,255,0.4)', color: 'white' }}
            onClick={() => setModal('register')}>
            Crear cuenta
          </button>
        </div>

        <button className="welcome-guest" onClick={() => navigate('/home')}>
          Explorar torneos sin cuenta →
        </button>
      </div>

      {modal === 'login' && (
        <LoginModal onClose={() => setModal(null)} onSwitch={() => setModal('register')} />
      )}
      {modal === 'register' && (
        <RegisterModal onClose={() => setModal(null)} onSwitch={() => setModal('login')} />
      )}
    </div>
  )
}

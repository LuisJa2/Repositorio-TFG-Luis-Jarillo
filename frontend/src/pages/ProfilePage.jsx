import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { useAuth } from '../context/AuthContext'

const ROL_LABEL = { JUGADOR: 'Jugador', ORGANIZADOR: 'Organizador', ADMIN: 'Administrador' }

export default function ProfilePage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  if (!user) return null

  return (
    <div className="page">
      <Navbar />
      <div className="container" style={{ maxWidth: '500px' }}>
        <div className="page-header" style={{ marginBottom: '28px' }}>
          <h1 className="page-title">Mi Perfil</h1>
        </div>

        <div className="card" style={{ padding: '32px', textAlign: 'center' }}>
          <div className="profile-avatar-lg">
            {user.username?.charAt(0).toUpperCase()}
          </div>

          <h2 style={{ fontSize: '22px', fontWeight: 700, marginBottom: '8px' }}>
            {user.username}
          </h2>

          <div className="role-badge" style={{ justifyContent: 'center', marginBottom: '24px' }}>
            {user.rol === 'ORGANIZADOR' ? '🎯' : user.rol === 'ADMIN' ? '⚙️' : '🎮'}
            {' '}{ROL_LABEL[user.rol] ?? user.rol}
          </div>

          <div className="info-table" style={{ textAlign: 'left', marginBottom: '28px' }}>
            <div className="info-row">
              <span className="info-label">Usuario</span>
              <span className="info-value">{user.username}</span>
            </div>
            <div className="info-row">
              <span className="info-label">Rol</span>
              <span className="info-value">{ROL_LABEL[user.rol] ?? user.rol}</span>
            </div>
            <div className="info-row">
              <span className="info-label">ID</span>
              <span className="info-value" style={{ color: 'var(--text-secondary)', fontFamily: 'monospace' }}>
                #{user.usuarioId}
              </span>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {(user.rol === 'ORGANIZADOR' || user.rol === 'ADMIN') && (
              <button className="btn btn-outline btn-full" onClick={() => navigate('/create')}>
                🏆 Crear torneo
              </button>
            )}
            <button className="btn btn-outline btn-full" onClick={() => navigate('/mis-torneos')}>
              📋 Mis torneos
            </button>
            <button className="btn btn-danger btn-full" onClick={handleLogout}>
              Cerrar sesión
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

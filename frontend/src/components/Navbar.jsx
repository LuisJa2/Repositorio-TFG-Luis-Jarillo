import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user } = useAuth()
  const { pathname } = useLocation()

  return (
    <nav className="navbar">
      <div className="container navbar-inner">
        <Link to="/home" className="navbar-brand">
          <span className="navbar-logo">🏆</span>
          <span>ArenaMix</span>
        </Link>

        <div className="navbar-links">
          <Link to="/home" className={`navbar-link ${pathname === '/home' ? 'active' : ''}`}>
            Torneos
          </Link>
          {user && (
            <Link to="/mis-torneos" className={`navbar-link ${pathname === '/mis-torneos' ? 'active' : ''}`}>
              Mis Torneos
            </Link>
          )}
          {user && (
            <Link to="/create" className="navbar-link navbar-link-create">
              + Crear Torneo
            </Link>
          )}
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          {/* Download APK button */}
          <a
            href="/ArenaMix.apk"
            download="ArenaMix.apk"
            className="btn-download-apk"
            title="Descargar app para Android"
          >
            {/* Android robot icon */}
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" style={{ flexShrink: 0 }}>
              <path d="M6 18c0 .55.45 1 1 1h1v3.5c0 .83.67 1.5 1.5 1.5s1.5-.67 1.5-1.5V19h2v3.5c0 .83.67 1.5 1.5 1.5s1.5-.67 1.5-1.5V19h1c.55 0 1-.45 1-1V8H6v10zm-2.5-1C2.67 17 2 17.67 2 18.5v-9C2 8.67 2.67 8 3.5 8S5 8.67 5 9.5v9c0 .83-.67 1.5-1.5 1.5zm17 0c-.83 0-1.5-.67-1.5-1.5v-9c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5v9c0 .83-.67 1.5-1.5 1.5zM15.53 2.16l1.3-1.3c.2-.2.2-.51 0-.71-.2-.2-.51-.2-.71 0l-1.48 1.48A5.84 5.84 0 0 0 12 1c-.85 0-1.65.19-2.37.52L8.15.04c-.2-.2-.51-.2-.71 0-.2.2-.2.51 0 .71l1.27 1.27C7.34 3.15 6 5.06 6 7h12c0-1.91-1.32-3.8-2.47-4.84zM10 5H9V4h1v1zm5 0h-1V4h1v1z"/>
            </svg>
            <span className="apk-label">Descargar App</span>
          </a>

          {user ? (
            <Link to="/profile" className="navbar-avatar" title={user.username}>
              {user.username?.charAt(0).toUpperCase()}
            </Link>
          ) : (
            <Link to="/" className="btn btn-outline" style={{ padding: '7px 16px', fontSize: '14px' }}>
              Iniciar sesión
            </Link>
          )}
        </div>
      </div>
    </nav>
  )
}

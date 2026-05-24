import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import TournamentCard from '../components/TournamentCard'
import { getTorneos } from '../api/api'
import { useAuth } from '../context/AuthContext'

const SPORTS = [
  { key: 'TODOS', label: 'Todos', emoji: '🏅' },
  { key: 'FUTBOL', label: 'Fútbol', emoji: '⚽' },
  { key: 'PADEL', label: 'Pádel', emoji: '🎾' },
  { key: 'BASKET', label: 'Basket', emoji: '🏀' },
]

export default function HomePage() {
  const [torneos, setTorneos] = useState([])
  const [filtro, setFiltro] = useState('TODOS')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const { user } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    getTorneos()
      .then(res => setTorneos(res.data))
      .catch(err => {
        if (!err.response) {
          setError('No se puede conectar con el servidor. Asegúrate de que el backend está corriendo en localhost:8080.')
        } else if (err.response.status === 401) {
          setError('Inicia sesión para ver los torneos.')
        } else {
          setError('No se pudieron cargar los torneos. Inténtalo de nuevo.')
        }
      })
      .finally(() => setLoading(false))
  }, [])

  const filtered = filtro === 'TODOS' ? torneos : torneos.filter(t => t.deporte === filtro)

  return (
    <div className="page">
      <Navbar />
      <div className="container">
        <div className="page-header" style={{ marginBottom: '24px' }}>
          <h1 className="page-title">Torneos</h1>
          <p className="page-subtitle">Descubre y únete a torneos deportivos</p>
        </div>

        <div style={{ display: 'flex', gap: '10px', marginBottom: '24px', flexWrap: 'wrap' }}>
          {SPORTS.map(s => (
            <button key={s.key} className={`chip ${filtro === s.key ? 'active' : ''}`}
              onClick={() => setFiltro(s.key)}>
              {s.emoji} {s.label}
            </button>
          ))}
        </div>

        {loading && <div className="spinner" />}

        {error && (
          <div className="empty-state">
            <div className="empty-state-icon">{error.includes('servidor') ? '🔌' : '🔒'}</div>
            <h3>{error.includes('servidor') ? 'Backend no disponible' : 'Acceso restringido'}</h3>
            <p style={{ marginBottom: '16px' }}>{error}</p>
            {error.includes('servidor') ? (
              <button className="btn btn-outline" onClick={() => { setLoading(true); setError(null); getTorneos().then(r => setTorneos(r.data)).catch(e => setError(!e.response ? 'No se puede conectar con el servidor. Asegúrate de que el backend está corriendo en localhost:8080.' : 'No se pudieron cargar los torneos.')).finally(() => setLoading(false)) }}>
                Reintentar
              </button>
            ) : (
              <button className="btn btn-primary" onClick={() => navigate('/')}>
                Iniciar sesión
              </button>
            )}
          </div>
        )}

        {!loading && !error && filtered.length === 0 && (
          <div className="empty-state">
            <div className="empty-state-icon">🏟️</div>
            <h3>No hay torneos</h3>
            <p>
              {filtro !== 'TODOS'
                ? `No hay torneos de ${SPORTS.find(s => s.key === filtro)?.label} disponibles.`
                : 'Todavía no hay torneos creados.'}
            </p>
            {user && (
              <button className="btn btn-primary" style={{ marginTop: '16px' }} onClick={() => navigate('/create')}>
                Crear el primero
              </button>
            )}
          </div>
        )}

        {!loading && !error && filtered.length > 0 && (
          <>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
              {filtered.length} torneo{filtered.length !== 1 ? 's' : ''}
            </p>
            <div className="grid-3">
              {filtered.map(t => <TournamentCard key={t.id} torneo={t} />)}
            </div>
          </>
        )}
      </div>
    </div>
  )
}

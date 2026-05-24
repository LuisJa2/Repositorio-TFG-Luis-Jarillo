import { useNavigate } from 'react-router-dom'

const SPORT_EMOJI = { FUTBOL: '⚽', PADEL: '🎾', BASKET: '🏀' }
const SPORT_LABEL = { FUTBOL: 'Fútbol', PADEL: 'Pádel', BASKET: 'Basket' }

function StatusBadge({ estado }) {
  const map = {
    ABIERTO: { label: 'Abierto', cls: 'badge-open' },
    EN_PROGRESO: { label: 'En progreso', cls: 'badge-progress' },
    FINALIZADO: { label: 'Finalizado', cls: 'badge-finished' },
  }
  const s = map[estado] ?? { label: estado, cls: 'badge-finished' }
  return <span className={`badge ${s.cls}`}>{s.label}</span>
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  try {
    return new Date(dateStr).toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' })
  } catch {
    return ''
  }
}

export default function TournamentCard({ torneo }) {
  const navigate = useNavigate()
  return (
    <div className="t-card" onClick={() => navigate(`/tournament/${torneo.id}`)}>
      <div className="t-card-header">
        <div className="t-card-sport-icon">
          {SPORT_EMOJI[torneo.deporte] ?? '🏅'}
        </div>
        <div>
          <div className="t-card-name">{torneo.nombre}</div>
          <div className="t-card-sport-label">{SPORT_LABEL[torneo.deporte] ?? torneo.deporte}</div>
        </div>
      </div>
      <div className="t-card-body">
        <div className="t-card-meta">
          <StatusBadge estado={torneo.estado} />
          <span className="t-card-date">{formatDate(torneo.fechaCreacion)}</span>
        </div>
        {torneo.organizadorUsername && (
          <div className="t-card-organizer">
            👤 {torneo.organizadorUsername}
          </div>
        )}
      </div>
    </div>
  )
}

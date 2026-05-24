import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { getTorneoById, eliminarTorneo, finalizarTorneo } from '../api/api'
import { useAuth } from '../context/AuthContext'

const SPORT_EMOJI  = { FUTBOL: '⚽', PADEL: '🎾', BASKET: '🏀' }
const SPORT_LABEL  = { FUTBOL: 'Fútbol', PADEL: 'Pádel', BASKET: 'Basket' }
const FORMAT_LABEL = { TOURNAMENT: '🏆 Eliminación', LIGA: '📊 Liga', GROUPS_TOURNAMENT: '🔀 Grupos + Eliminación' }

function StatusBadge({ estado }) {
  const map = {
    ABIERTO: { label: 'Abierto', cls: 'badge-open' },
    EN_PROGRESO: { label: 'En progreso', cls: 'badge-progress' },
    FINALIZADO: { label: 'Finalizado', cls: 'badge-finished' },
  }
  const s = map[estado] ?? { label: estado, cls: 'badge-finished' }
  return <span className={`badge ${s.cls}`}>{s.label}</span>
}

function InfoTab({ torneo }) {
  const formatDate = (d) => d ? new Date(d).toLocaleDateString('es-ES', {
    day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
  }) : '—'

  return (
    <div className="card" style={{ padding: '8px 24px' }}>
      <div className="info-table">
        <div className="info-row">
          <span className="info-label">Nombre</span>
          <span className="info-value">{torneo.nombre}</span>
        </div>
        <div className="info-row">
          <span className="info-label">Deporte</span>
          <span className="info-value">{SPORT_EMOJI[torneo.deporte]} {SPORT_LABEL[torneo.deporte] ?? torneo.deporte}</span>
        </div>
        <div className="info-row">
          <span className="info-label">Organizador</span>
          <span className="info-value">👤 {torneo.organizadorUsername ?? '—'}</span>
        </div>
        <div className="info-row">
          <span className="info-label">Estado</span>
          <StatusBadge estado={torneo.estado} />
        </div>
        <div className="info-row">
          <span className="info-label">Formato</span>
          <span className="info-value">{FORMAT_LABEL[torneo.formato] ?? torneo.formato ?? '—'}</span>
        </div>
        <div className="info-row">
          <span className="info-label">Creado</span>
          <span className="info-value">{formatDate(torneo.fechaCreacion)}</span>
        </div>
      </div>
    </div>
  )
}

function EquiposTab() {
  return (
    <div className="empty-state">
      <div className="empty-state-icon">👥</div>
      <h3>Equipos no disponibles</h3>
      <p>La gestión de participantes estará disponible próximamente.</p>
    </div>
  )
}

function BracketTab({ torneo }) {
  return (
    <div className="empty-state">
      <div className="empty-state-icon">🏆</div>
      <h3>Bracket no disponible</h3>
      <p>
        {torneo.estado === 'ABIERTO'
          ? 'El bracket se generará cuando comience el torneo.'
          : 'Los datos del bracket no están disponibles actualmente.'}
      </p>
    </div>
  )
}

export default function TournamentPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [torneo, setTorneo] = useState(null)
  const [tab, setTab] = useState('info')
  const [loading, setLoading] = useState(true)
  const [deleting, setDeleting] = useState(false)
  const [finalizing, setFinalizing] = useState(false)

  useEffect(() => {
    getTorneoById(id)
      .then(res => setTorneo(res.data))
      .catch(() => navigate('/home'))
      .finally(() => setLoading(false))
  }, [id])

  const handleFinalizar = async () => {
    if (!confirm(`¿Marcar "${torneo.nombre}" como FINALIZADO?`)) return
    setFinalizing(true)
    try {
      const res = await finalizarTorneo(id)
      setTorneo(res.data)
    } catch {
      alert('No se pudo finalizar el torneo.')
    } finally {
      setFinalizing(false)
    }
  }

  const handleDelete = async () => {
    if (!confirm(`¿Eliminar el torneo "${torneo.nombre}"? Esta acción no se puede deshacer.`)) return
    setDeleting(true)
    try {
      await eliminarTorneo(id)
      navigate('/mis-torneos')
    } catch {
      alert('No se pudo eliminar el torneo.')
      setDeleting(false)
    }
  }

  if (loading) return (
    <div className="page"><Navbar /><div className="spinner" /></div>
  )

  if (!torneo) return null

  const isOwner = user && user.username === torneo.organizadorUsername
  const tab3Label = torneo.formato === 'LIGA' ? 'Liga'
    : torneo.formato === 'GROUPS_TOURNAMENT' ? 'Grupos'
    : 'Bracket'

  return (
    <div className="page">
      <Navbar />
      <div style={{
        background: 'linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%)',
        padding: '32px 0 40px',
        marginBottom: '-20px'
      }}>
        <div className="container">
          <button onClick={() => navigate(-1)} style={{
            background: 'rgba(255,255,255,0.15)', border: 'none', color: 'white',
            padding: '6px 14px', borderRadius: '20px', fontSize: '14px', cursor: 'pointer',
            marginBottom: '16px'
          }}>
            ← Volver
          </button>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', justifyContent: 'space-between', flexWrap: 'wrap' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '60px', height: '60px', borderRadius: '14px',
                background: 'rgba(255,255,255,0.2)', display: 'flex',
                alignItems: 'center', justifyContent: 'center', fontSize: '32px'
              }}>
                {SPORT_EMOJI[torneo.deporte] ?? '🏅'}
              </div>
              <div>
                <h1 style={{ color: 'white', fontSize: '24px', fontWeight: 800, lineHeight: 1.2 }}>
                  {torneo.nombre}
                </h1>
                <div style={{ color: 'rgba(255,255,255,0.75)', fontSize: '14px', marginTop: '4px' }}>
                  {SPORT_LABEL[torneo.deporte] ?? torneo.deporte}
                </div>
              </div>
            </div>
            <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              {isOwner && torneo.estado !== 'FINALIZADO' && (
                <button className="btn" onClick={handleFinalizar} disabled={finalizing}
                  style={{ background: '#2e7d32', color: 'white', flexShrink: 0 }}>
                  {finalizing ? 'Finalizando...' : '🏆 Finalizar torneo'}
                </button>
              )}
              {isOwner && (
                <button className="btn btn-danger" onClick={handleDelete} disabled={deleting}
                  style={{ flexShrink: 0 }}>
                  {deleting ? 'Eliminando...' : '🗑 Eliminar torneo'}
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: '36px' }}>
        <div className="tabs">
          {[
            { key: 'info', label: 'Información' },
            { key: 'equipos', label: 'Equipos' },
            { key: 'bracket', label: tab3Label },
          ].map(t => (
            <button key={t.key} className={`tab-btn ${tab === t.key ? 'active' : ''}`}
              onClick={() => setTab(t.key)}>
              {t.label}
            </button>
          ))}
        </div>

        {tab === 'info' && <InfoTab torneo={torneo} />}
        {tab === 'equipos' && <EquiposTab />}
        {tab === 'bracket' && <BracketTab torneo={torneo} />}
      </div>
    </div>
  )
}

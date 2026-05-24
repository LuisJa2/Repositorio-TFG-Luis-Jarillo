import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import TournamentCard from '../components/TournamentCard'
import { getMisTorneos, eliminarTorneo } from '../api/api'

export default function MisTorneosPage() {
  const [torneos, setTorneos] = useState([])
  const [loading, setLoading] = useState(true)
  const [deletingId, setDeletingId] = useState(null)
  const navigate = useNavigate()

  const fetchTorneos = () => {
    setLoading(true)
    getMisTorneos()
      .then(res => setTorneos(res.data))
      .catch(() => setTorneos([]))
      .finally(() => setLoading(false))
  }

  useEffect(() => { fetchTorneos() }, [])

  const handleDelete = async (e, torneo) => {
    e.stopPropagation()
    if (!confirm(`¿Eliminar "${torneo.nombre}"? No se puede deshacer.`)) return
    setDeletingId(torneo.id)
    try {
      await eliminarTorneo(torneo.id)
      setTorneos(prev => prev.filter(t => t.id !== torneo.id))
    } catch {
      alert('No se pudo eliminar el torneo.')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="page">
      <Navbar />
      <div className="container">
        <div className="page-header" style={{ marginBottom: '24px' }}>
          <h1 className="page-title">Mis Torneos</h1>
          <p className="page-subtitle">Torneos que has organizado</p>
        </div>

        {loading && <div className="spinner" />}

        {!loading && torneos.length === 0 && (
          <div className="empty-state">
            <div className="empty-state-icon">📋</div>
            <h3>No has creado ningún torneo</h3>
            <p>Crea tu primer torneo y empieza a competir.</p>
            <button className="btn btn-primary" style={{ marginTop: '16px' }} onClick={() => navigate('/create')}>
              + Crear torneo
            </button>
          </div>
        )}

        {!loading && torneos.length > 0 && (
          <>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
              {torneos.length} torneo{torneos.length !== 1 ? 's' : ''}
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {torneos.map(t => (
                <div key={t.id} style={{ position: 'relative' }}
                  onClick={() => navigate(`/tournament/${t.id}`)}>
                  <div className="card" style={{ cursor: 'pointer', padding: '16px 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '16px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                      <div style={{
                        width: '48px', height: '48px', borderRadius: '10px',
                        background: 'var(--primary-light)', display: 'flex',
                        alignItems: 'center', justifyContent: 'center', fontSize: '24px', flexShrink: 0
                      }}>
                        {{ FUTBOL: '⚽', PADEL: '🎾', BASKET: '🏀' }[t.deporte] ?? '🏅'}
                      </div>
                      <div>
                        <div style={{ fontWeight: 700, fontSize: '16px' }}>{t.nombre}</div>
                        <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                          {{ FUTBOL: 'Fútbol', PADEL: 'Pádel', BASKET: 'Basket' }[t.deporte] ?? t.deporte}
                        </div>
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px', flexShrink: 0 }}>
                      <span className={`badge ${{ ABIERTO: 'badge-open', EN_PROGRESO: 'badge-progress', FINALIZADO: 'badge-finished' }[t.estado] ?? 'badge-finished'}`}>
                        {{ ABIERTO: 'Abierto', EN_PROGRESO: 'En progreso', FINALIZADO: 'Finalizado' }[t.estado] ?? t.estado}
                      </span>
                      <button
                        className="btn btn-danger"
                        style={{ padding: '6px 14px', fontSize: '13px' }}
                        disabled={deletingId === t.id}
                        onClick={e => handleDelete(e, t)}
                      >
                        {deletingId === t.id ? '...' : '🗑'}
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  )
}

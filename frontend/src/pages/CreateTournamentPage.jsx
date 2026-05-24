import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { crearTorneo } from '../api/api'
import { useAuth } from '../context/AuthContext'

const SPORTS = [
  { key: 'FUTBOL',  label: 'Fútbol',  emoji: '⚽' },
  { key: 'PADEL',   label: 'Pádel',   emoji: '🎾' },
  { key: 'BASKET',  label: 'Basket',  emoji: '🏀' },
]

const FORMATS = [
  { key: 'TOURNAMENT',        label: 'Eliminación',             emoji: '🏆', desc: 'Rondas eliminatorias hasta el campeón' },
  { key: 'LIGA',              label: 'Liga',                    emoji: '📊', desc: 'Todos contra todos, clasificación por puntos' },
  { key: 'GROUPS_TOURNAMENT', label: 'Grupos + Eliminación',    emoji: '🔀', desc: 'Fase de grupos, el 60% pasa a eliminatoria' },
]

export default function CreateTournamentPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [nombre,  setNombre]  = useState('')
  const [deporte, setDeporte] = useState('')
  const [formato, setFormato] = useState('TOURNAMENT')
  const [error,   setError]   = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!deporte) { setError('Selecciona un deporte.'); return }
    setError('')
    setLoading(true)
    try {
      const res = await crearTorneo({
        nombre,
        deporte,
        organizadorId: Number(user.usuarioId),
        formato,
      })
      navigate(`/tournament/${res.data.id}`)
    } catch (err) {
      setError(err.response?.data?.mensaje ?? 'Error al crear el torneo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <Navbar />
      <div className="container" style={{ maxWidth: '600px' }}>
        <div className="page-header" style={{ marginBottom: '28px' }}>
          <h1 className="page-title">Crear Torneo</h1>
          <p className="page-subtitle">Configura tu nuevo torneo deportivo</p>
        </div>

        <form className="card" style={{ padding: '28px' }} onSubmit={handleSubmit}>
          {/* Nombre */}
          <div className="form-group">
            <label>Nombre del torneo</label>
            <input
              type="text"
              placeholder="ej: Copa de Verano 2025"
              value={nombre}
              onChange={e => setNombre(e.target.value)}
              required
              maxLength={100}
            />
          </div>

          {/* Deporte */}
          <div className="form-group" style={{ marginBottom: '24px' }}>
            <label>Deporte</label>
            <div style={{ display: 'flex', gap: '12px', marginTop: '4px' }}>
              {SPORTS.map(s => (
                <button
                  key={s.key}
                  type="button"
                  onClick={() => setDeporte(s.key)}
                  style={{
                    flex: 1,
                    padding: '14px 8px',
                    borderRadius: 'var(--radius)',
                    border: `2px solid ${deporte === s.key ? 'var(--green)' : 'var(--divider)'}`,
                    background: deporte === s.key ? 'var(--green-light)' : 'var(--white)',
                    color: deporte === s.key ? 'var(--green)' : 'var(--text-secondary)',
                    fontWeight: 600,
                    fontSize: '14px',
                    cursor: 'pointer',
                    transition: 'all 0.2s',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    gap: '6px',
                  }}
                >
                  <span style={{ fontSize: '28px' }}>{s.emoji}</span>
                  {s.label}
                </button>
              ))}
            </div>
          </div>

          {/* Formato */}
          <div className="form-group" style={{ marginBottom: '24px' }}>
            <label>Formato</label>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '6px' }}>
              {FORMATS.map(f => (
                <button
                  key={f.key}
                  type="button"
                  onClick={() => setFormato(f.key)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '14px',
                    padding: '14px 16px',
                    borderRadius: 'var(--radius)',
                    border: `2px solid ${formato === f.key ? 'var(--primary)' : 'var(--divider)'}`,
                    background: formato === f.key ? 'rgba(232,101,10,0.06)' : 'var(--white)',
                    cursor: 'pointer',
                    textAlign: 'left',
                    transition: 'all 0.2s',
                  }}
                >
                  <span style={{ fontSize: '24px' }}>{f.emoji}</span>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '14px', color: formato === f.key ? 'var(--primary)' : 'var(--text)' }}>
                      {f.label}
                    </div>
                    <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                      {f.desc}
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </div>

          {error && <p className="error-msg" style={{ marginBottom: '16px' }}>{error}</p>}

          <div style={{ display: 'flex', gap: '10px' }}>
            <button type="button" className="btn btn-ghost btn-full" onClick={() => navigate(-1)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
              {loading ? 'Creando...' : '🏆 Crear torneo'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

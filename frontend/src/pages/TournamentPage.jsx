import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import {
  getTorneoById, eliminarTorneo, finalizarTorneo,
  getParticipantes, addParticipante, removeParticipante,
  getPartidos, generarPartidos, siguienteRonda,
  actualizarResultado, simularPartido,
} from '../api/api'
import { useAuth } from '../context/AuthContext'

const SPORT_EMOJI  = { FUTBOL: '⚽', PADEL: '🎾', BASKET: '🏀' }
const SPORT_LABEL  = { FUTBOL: 'Fútbol', PADEL: 'Pádel', BASKET: 'Basket' }
const FORMAT_LABEL = { TOURNAMENT: '🏆 Eliminación', LIGA: '📊 Liga', GROUPS_TOURNAMENT: '🔀 Grupos + Eliminación' }

function StatusBadge({ estado }) {
  const map = {
    ABIERTO:     { label: 'Abierto',     cls: 'badge-open' },
    EN_PROGRESO: { label: 'En progreso', cls: 'badge-progress' },
    FINALIZADO:  { label: 'Finalizado',  cls: 'badge-finished' },
  }
  const s = map[estado] ?? { label: estado, cls: 'badge-finished' }
  return <span className={`badge ${s.cls}`}>{s.label}</span>
}

// ── Tab Información ────────────────────────────────────────────────────────────
function InfoTab({ torneo }) {
  const fmt = (d) => d ? new Date(d).toLocaleDateString('es-ES', {
    day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
  }) : '—'
  return (
    <div className="card" style={{ padding: '8px 24px' }}>
      <div className="info-table">
        {[
          ['Nombre',      torneo.nombre],
          ['Deporte',     `${SPORT_EMOJI[torneo.deporte] ?? ''} ${SPORT_LABEL[torneo.deporte] ?? torneo.deporte}`],
          ['Organizador', `👤 ${torneo.organizadorUsername ?? '—'}`],
          ['Estado',      <StatusBadge key="s" estado={torneo.estado} />],
          ['Formato',     FORMAT_LABEL[torneo.formato] ?? torneo.formato ?? '—'],
          ['Creado',      fmt(torneo.fechaCreacion)],
        ].map(([label, value]) => (
          <div className="info-row" key={label}>
            <span className="info-label">{label}</span>
            <span className="info-value">{value}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

// ── Tab Equipos ────────────────────────────────────────────────────────────────
function EquiposTab({ torneo, user }) {
  const [participantes, setParticipantes] = useState([])
  const [nombre, setNombre] = useState('')
  const [loading, setLoading] = useState(true)
  const [adding, setAdding] = useState(false)

  const cargar = useCallback(async () => {
    setLoading(true)
    try {
      const res = await getParticipantes(torneo.id)
      setParticipantes(res.data)
    } finally { setLoading(false) }
  }, [torneo.id])

  useEffect(() => { cargar() }, [cargar])

  const handleAdd = async () => {
    if (!nombre.trim()) return
    setAdding(true)
    try {
      await addParticipante(torneo.id, { nombre: nombre.trim() })
      setNombre('')
      cargar()
    } catch { alert('Error al añadir participante.') }
    finally { setAdding(false) }
  }

  const handleRemove = async (pid) => {
    if (!confirm('¿Eliminar este participante?')) return
    try {
      await removeParticipante(torneo.id, pid)
      cargar()
    } catch { alert('Error al eliminar participante.') }
  }

  if (loading) return <div className="spinner" style={{ margin: '40px auto' }} />

  const canEdit = user && torneo.estado === 'ABIERTO'

  return (
    <div className="card" style={{ padding: '20px 24px' }}>
      {canEdit && (
        <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
          <input
            value={nombre}
            onChange={e => setNombre(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleAdd()}
            placeholder="Nombre del equipo o jugador..."
            style={{ flex: 1 }}
          />
          <button className="btn btn-primary" onClick={handleAdd} disabled={adding}>
            {adding ? '...' : '+ Añadir'}
          </button>
        </div>
      )}

      {participantes.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">👥</div>
          <h3>Sin participantes</h3>
          <p>{canEdit ? 'Añade los equipos o jugadores.' : 'No hay participantes registrados.'}</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <p style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '4px' }}>
            {participantes.length} participante{participantes.length !== 1 ? 's' : ''}
          </p>
          {participantes.map((p, i) => (
            <div key={p.id} style={{
              display: 'flex', alignItems: 'center', justifyContent: 'space-between',
              padding: '12px 16px', borderRadius: 'var(--radius)',
              background: 'var(--bg)', border: '1px solid var(--divider)'
            }}>
              <span style={{ fontWeight: 600 }}>
                <span style={{ color: 'var(--text-secondary)', marginRight: '10px', fontSize: '13px' }}>#{i + 1}</span>
                {p.nombre}
              </span>
              {canEdit && (
                <button
                  onClick={() => handleRemove(p.id)}
                  style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#e53935', fontSize: '18px' }}
                  title="Eliminar"
                >🗑</button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

// ── Tab Bracket / Liga ─────────────────────────────────────────────────────────
function PartidosTab({ torneo, user }) {
  const [partidos, setPartidos] = useState([])
  const [loading, setLoading] = useState(true)
  const [scores, setScores] = useState({}) // { partidoId: { local, visitante } }

  const cargar = useCallback(async () => {
    setLoading(true)
    try {
      const res = await getPartidos(torneo.id)
      setPartidos(res.data)
    } finally { setLoading(false) }
  }, [torneo.id])

  useEffect(() => { cargar() }, [cargar])

  const handleGenerar = async () => {
    try {
      await generarPartidos(torneo.id)
      cargar()
    } catch (err) {
      alert(err.response?.data?.mensaje ?? 'Error al generar partidos.')
    }
  }

  const handleSimular = async (id) => {
    try {
      await simularPartido(id)
      cargar()
    } catch { alert('Error al simular.') }
  }

  const handleGuardar = async (id) => {
    const s = scores[id]
    if (!s) return
    const pL = parseInt(s.local ?? 0)
    const pV = parseInt(s.visitante ?? 0)
    try {
      await actualizarResultado(id, { puntosLocal: pL, puntosVisitante: pV })
      cargar()
    } catch { alert('Error al guardar resultado.') }
  }

  const handleSiguienteRonda = async () => {
    try {
      const res = await siguienteRonda(torneo.id)
      alert(res.data?.mensaje ?? 'Siguiente ronda generada.')
      cargar()
    } catch (err) {
      alert(err.response?.data?.mensaje ?? 'Error al avanzar ronda.')
    }
  }

  if (loading) return <div className="spinner" style={{ margin: '40px auto' }} />

  const esLiga = torneo.formato === 'LIGA' || torneo.formato === 'GROUPS_TOURNAMENT'
  const esTournament = torneo.formato === 'TOURNAMENT'

  // Agrupar por ronda
  const rondas = {}
  partidos.forEach(p => {
    if (!rondas[p.ronda]) rondas[p.ronda] = []
    rondas[p.ronda].push(p)
  })
  const rondasOrdenadas = Object.keys(rondas).map(Number).sort((a, b) => a - b)
  const maxRonda = rondasOrdenadas[rondasOrdenadas.length - 1]
  const rondaActualDone = rondas[maxRonda]?.every(p => p.estado === 'FINALIZADO')

  // Etiqueta de ronda para TOURNAMENT
  const labelRonda = (ronda, total) => {
    const restantes = total - ronda
    if (restantes === 0) return '🏆 Final'
    if (restantes === 1) return '🥈 Semifinales'
    if (restantes === 2) return '🎯 Cuartos de final'
    return `Ronda ${ronda}`
  }

  // Clasificación para LIGA
  const standings = {}
  if (esLiga) {
    partidos.forEach(p => {
      if (!standings[p.nombreLocal])    standings[p.nombreLocal]    = { pj:0,g:0,e:0,pe:0,gf:0,gc:0,pts:0 }
      if (!standings[p.nombreVisitante]) standings[p.nombreVisitante] = { pj:0,g:0,e:0,pe:0,gf:0,gc:0,pts:0 }
      if (p.estado === 'FINALIZADO') {
        const l = standings[p.nombreLocal]
        const v = standings[p.nombreVisitante]
        l.pj++; v.pj++
        l.gf += p.puntosLocal;    l.gc += p.puntosVisitante
        v.gf += p.puntosVisitante; v.gc += p.puntosLocal
        if (p.puntosLocal > p.puntosVisitante)  { l.g++; l.pts+=3; v.pe++ }
        else if (p.puntosLocal < p.puntosVisitante) { v.g++; v.pts+=3; l.pe++ }
        else { l.e++; l.pts++; v.e++; v.pts++ }
      }
    })
  }
  const standingsList = Object.entries(standings)
    .map(([nombre, s]) => ({ nombre, ...s, gd: s.gf - s.gc }))
    .sort((a, b) => b.pts - a.pts || b.gd - a.gd || b.gf - a.gf)

  return (
    <div>
      {/* Sin partidos */}
      {partidos.length === 0 && (
        <div className="card" style={{ padding: '32px', textAlign: 'center' }}>
          <div style={{ fontSize: '48px', marginBottom: '12px' }}>🎯</div>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '16px' }}>
            {torneo.estado === 'ABIERTO'
              ? 'Añade participantes y genera los partidos.'
              : 'No hay partidos generados.'}
          </p>
          {user && torneo.estado !== 'FINALIZADO' && (
            <button className="btn btn-primary" onClick={handleGenerar}>
              ⚡ Generar partidos
            </button>
          )}
        </div>
      )}

      {/* Clasificación LIGA */}
      {esLiga && standingsList.length > 0 && (
        <div className="card" style={{ padding: '16px 20px', marginBottom: '20px', overflowX: 'auto' }}>
          <h3 style={{ fontWeight: 700, marginBottom: '12px' }}>📊 Clasificación</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ color: 'var(--text-secondary)', borderBottom: '1px solid var(--divider)' }}>
                {['#','Equipo','PJ','G','E','P','GF','GC','GD','Pts'].map(h => (
                  <th key={h} style={{ padding: '6px 8px', textAlign: h==='Equipo'?'left':'center', fontWeight: 600 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {standingsList.map((s, i) => (
                <tr key={s.nombre} style={{ borderBottom: '1px solid var(--divider)' }}>
                  <td style={{ padding: '8px', textAlign:'center', color:'var(--text-secondary)' }}>{i+1}</td>
                  <td style={{ padding: '8px', fontWeight: 600 }}>{s.nombre}</td>
                  {[s.pj,s.g,s.e,s.pe,s.gf,s.gc,s.gd>=0?`+${s.gd}`:s.gd,s.pts].map((v,j) => (
                    <td key={j} style={{ padding:'8px', textAlign:'center', fontWeight: j===7?700:400,
                      color: j===7?'var(--primary)':'inherit' }}>{v}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Partidos por ronda */}
      {rondasOrdenadas.map(ronda => (
        <div key={ronda} style={{ marginBottom: '20px' }}>
          <h3 style={{ fontWeight: 700, marginBottom: '10px', fontSize: '15px' }}>
            {esLiga ? '⚽ Partidos' : labelRonda(ronda, rondasOrdenadas.length)}
          </h3>
          {rondas[ronda].map(partido => {
            const done = partido.estado === 'FINALIZADO'
            const ganadorLocal = done && partido.puntosLocal > partido.puntosVisitante
            const ganadorVis   = done && partido.puntosVisitante > partido.puntosLocal
            const s = scores[partido.id] ?? { local: '', visitante: '' }

            return (
              <div key={partido.id} className="card" style={{
                padding: '14px 18px', marginBottom: '10px',
                display: 'flex', alignItems: 'center', gap: '12px', flexWrap: 'wrap'
              }}>
                {/* Local */}
                <span style={{ flex: 1, fontWeight: ganadorLocal ? 700 : 400,
                  color: ganadorLocal ? 'var(--primary)' : 'inherit', textAlign: 'right' }}>
                  {ganadorLocal ? '🏆 ' : ''}{partido.nombreLocal}
                </span>

                {/* Marcador */}
                {done ? (
                  <span style={{ fontWeight: 800, fontSize: '18px', minWidth: '60px', textAlign: 'center' }}>
                    {partido.puntosLocal} – {partido.puntosVisitante}
                  </span>
                ) : user && torneo.estado !== 'FINALIZADO' ? (
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <input type="number" min="0" max="99" value={s.local}
                      onChange={e => setScores(prev => ({ ...prev, [partido.id]: { ...s, local: e.target.value } }))}
                      style={{ width: '52px', textAlign: 'center', padding: '6px' }}
                    />
                    <span style={{ fontWeight: 700 }}>–</span>
                    <input type="number" min="0" max="99" value={s.visitante}
                      onChange={e => setScores(prev => ({ ...prev, [partido.id]: { ...s, visitante: e.target.value } }))}
                      style={{ width: '52px', textAlign: 'center', padding: '6px' }}
                    />
                  </div>
                ) : (
                  <span style={{ color: 'var(--text-secondary)', minWidth: '60px', textAlign: 'center' }}>vs</span>
                )}

                {/* Visitante */}
                <span style={{ flex: 1, fontWeight: ganadorVis ? 700 : 400,
                  color: ganadorVis ? 'var(--primary)' : 'inherit' }}>
                  {ganadorVis ? '🏆 ' : ''}{partido.nombreVisitante}
                </span>

                {/* Botones */}
                {!done && user && torneo.estado !== 'FINALIZADO' && (
                  <div style={{ display: 'flex', gap: '6px', flexShrink: 0 }}>
                    <button className="btn" style={{ padding: '6px 12px', fontSize: '13px', background: '#1565c0', color: '#fff' }}
                      onClick={() => handleGuardar(partido.id)}>
                      ✓ Guardar
                    </button>
                    <button className="btn" style={{ padding: '6px 12px', fontSize: '13px', background: '#6a1b9a', color: '#fff' }}
                      onClick={() => handleSimular(partido.id)}>
                      🎲 Simular
                    </button>
                  </div>
                )}
              </div>
            )
          })}
        </div>
      ))}

      {/* Siguiente ronda (solo TOURNAMENT) */}
      {esTournament && partidos.length > 0 && rondaActualDone && user && torneo.estado !== 'FINALIZADO' && (
        <div style={{ textAlign: 'center', marginTop: '8px' }}>
          <button className="btn btn-primary" onClick={handleSiguienteRonda}>
            Siguiente ronda →
          </button>
        </div>
      )}

      {/* Botón generar si ya hay partidos */}
      {partidos.length > 0 && user && torneo.estado !== 'FINALIZADO' && (
        <div style={{ textAlign: 'center', marginTop: '12px' }}>
          <button className="btn btn-ghost" style={{ fontSize: '13px' }} onClick={handleGenerar}>
            🔄 Reiniciar partidos
          </button>
        </div>
      )}
    </div>
  )
}

// ── Página principal ───────────────────────────────────────────────────────────
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
    } catch { alert('No se pudo finalizar el torneo.') }
    finally { setFinalizing(false) }
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

  if (loading) return <div className="page"><Navbar /><div className="spinner" /></div>
  if (!torneo) return null

  const isOwner = user && user.username === torneo.organizadorUsername
  const tab3Label = torneo.formato === 'LIGA' ? 'Liga'
    : torneo.formato === 'GROUPS_TOURNAMENT' ? 'Grupos'
    : 'Bracket'

  return (
    <div className="page">
      <Navbar />

      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%)',
        padding: '32px 0 40px', marginBottom: '-20px'
      }}>
        <div className="container">
          <button onClick={() => navigate(-1)} style={{
            background: 'rgba(255,255,255,0.15)', border: 'none', color: 'white',
            padding: '6px 14px', borderRadius: '20px', fontSize: '14px', cursor: 'pointer', marginBottom: '16px'
          }}>← Volver</button>

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
                <h1 style={{ color: 'white', fontSize: '24px', fontWeight: 800, lineHeight: 1.2 }}>{torneo.nombre}</h1>
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
                <button className="btn btn-danger" onClick={handleDelete} disabled={deleting} style={{ flexShrink: 0 }}>
                  {deleting ? 'Eliminando...' : '🗑 Eliminar torneo'}
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="container" style={{ paddingTop: '36px' }}>
        <div className="tabs">
          {[
            { key: 'info',    label: 'Información' },
            { key: 'equipos', label: 'Equipos' },
            { key: 'bracket', label: tab3Label },
          ].map(t => (
            <button key={t.key} className={`tab-btn ${tab === t.key ? 'active' : ''}`}
              onClick={() => setTab(t.key)}>
              {t.label}
            </button>
          ))}
        </div>

        {tab === 'info'    && <InfoTab torneo={torneo} />}
        {tab === 'equipos' && <EquiposTab torneo={torneo} user={user} />}
        {tab === 'bracket' && <PartidosTab torneo={torneo} user={user} />}
      </div>
    </div>
  )
}

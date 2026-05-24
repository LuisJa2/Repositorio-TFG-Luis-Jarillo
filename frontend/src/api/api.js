import axios from 'axios'

// En producción (Vercel) VITE_API_URL apunta al backend de Railway.
// En desarrollo usa el proxy de Vite (/api → localhost:8080).
const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'

const api = axios.create({ baseURL: BASE_URL, timeout: 8000 })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const login = (email, password) =>
  api.post('/auth/login', { email, password })

export const registro = (username, email, password) =>
  api.post('/auth/registro', { username, email, password })

export const getTorneos = () => api.get('/torneos')
export const getMisTorneos = () => api.get('/torneos/mis-torneos')
export const getTorneoById = (id) => api.get(`/torneos/${id}`)
export const crearTorneo = (data) => api.post('/torneos', data)
export const eliminarTorneo = (id) => api.delete(`/torneos/${id}`)
export const finalizarTorneo = (id) => api.patch(`/torneos/${id}/finalizar`)

export default api

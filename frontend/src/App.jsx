import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import { Component } from 'react'
import WelcomePage from './pages/WelcomePage'
import HomePage from './pages/HomePage'
import TournamentPage from './pages/TournamentPage'
import MisTorneosPage from './pages/MisTorneosPage'
import CreateTournamentPage from './pages/CreateTournamentPage'
import ProfilePage from './pages/ProfilePage'

// Error boundary to surface any runtime crash instead of blank page
class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { error: null }
  }
  static getDerivedStateFromError(error) {
    return { error }
  }
  render() {
    if (this.state.error) {
      return (
        <div style={{
          padding: '40px', fontFamily: 'monospace',
          background: '#fff1f0', border: '1px solid #f5222d',
          margin: '20px', borderRadius: '8px'
        }}>
          <h2 style={{ color: '#f5222d' }}>⚠️ Error de la aplicación</h2>
          <pre style={{ marginTop: '12px', fontSize: '13px', whiteSpace: 'pre-wrap' }}>
            {this.state.error?.message}
            {'\n\n'}
            {this.state.error?.stack}
          </pre>
          <button onClick={() => window.location.reload()}
            style={{ marginTop: '16px', padding: '8px 16px', background: '#f5222d', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
            Recargar
          </button>
        </div>
      )
    }
    return this.props.children
  }
}

function ProtectedRoute({ children }) {
  const { user } = useAuth()
  return user ? children : <Navigate to="/" replace />
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<WelcomePage />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/tournament/:id" element={<TournamentPage />} />
      <Route path="/mis-torneos" element={<ProtectedRoute><MisTorneosPage /></ProtectedRoute>} />
      <Route path="/create" element={<ProtectedRoute><CreateTournamentPage /></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </AuthProvider>
    </ErrorBoundary>
  )
}

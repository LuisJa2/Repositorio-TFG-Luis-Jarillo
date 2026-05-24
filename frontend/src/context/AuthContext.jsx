import { createContext, useContext, useState, useCallback } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const token = localStorage.getItem('token')
    if (!token) return null
    return {
      token,
      username: localStorage.getItem('username'),
      usuarioId: localStorage.getItem('usuarioId'),
      rol: localStorage.getItem('rol'),
    }
  })

  const saveSession = useCallback((data) => {
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('usuarioId', String(data.usuarioId))
    localStorage.setItem('rol', data.rol)
    setUser({ token: data.token, username: data.username, usuarioId: data.usuarioId, rol: data.rol })
  }, [])

  const logout = useCallback(() => {
    localStorage.clear()
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, saveSession, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)

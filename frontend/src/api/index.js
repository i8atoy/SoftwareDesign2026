import axios from 'axios'

const AUTH_URL = 'http://localhost:8081'
const API_URL  = 'http://localhost:8080'
const TEAM_URL = 'http://localhost:8082'

const api = axios.create({ baseURL: API_URL })

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const authApi = axios.create({ baseURL: AUTH_URL })
export const teamApi = axios.create({ baseURL: TEAM_URL })

teamApi.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default api

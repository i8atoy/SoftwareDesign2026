import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { authApi } from '../api/axios'

export default function Login() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      const res = await authApi.post('/api/auth/login', form)
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('roles', JSON.stringify(res.data.roles))
      localStorage.setItem('managedTeamId', res.data.managedTeamId ?? '')
      navigate('/tournaments')
    } catch {
      setError(t('errorInvalid'))
    }
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}><h2>{t('welcomeBack')}</h2></div>
        <div style={styles.body}>
          {error && <div style={styles.error}>{error}</div>}
          <form onSubmit={handleSubmit}>
            <label style={styles.label}>{t('username')}</label>
            <input style={styles.input} value={form.username}
              onChange={e => setForm({...form, username: e.target.value})} required />
            <label style={styles.label}>{t('password')}</label>
            <input style={styles.input} type="password" value={form.password}
              onChange={e => setForm({...form, password: e.target.value})} required />
            <button style={styles.btn} type="submit">{t('login')}</button>
          </form>
          <p style={{textAlign:'center', marginTop:'1rem', fontSize:'0.9rem'}}>
            {t('newHere')} <Link to="/register">{t('register')}</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

const styles = {
  page: { minHeight:'100vh', display:'flex', alignItems:'center', justifyContent:'center', background:'#f0f2f5' },
  card: { width:'400px', borderRadius:'8px', boxShadow:'0 2px 12px rgba(0,0,0,0.15)', overflow:'hidden', background:'white' },
  header: { background:'#1a1e22', color:'white', padding:'2rem', textAlign:'center' },
  body: { padding:'2rem' },
  label: { display:'block', marginBottom:'4px', fontWeight:'600', fontSize:'0.85rem', textTransform:'uppercase', color:'#666' },
  input: { width:'100%', padding:'10px', marginBottom:'1rem', border:'1px solid #ddd', borderRadius:'4px', fontSize:'1rem' },
  btn: { width:'100%', padding:'12px', background:'#1a1e22', color:'white', border:'none', borderRadius:'4px', cursor:'pointer', fontWeight:'bold' },
  error: { background:'#f8d7da', color:'#721c24', padding:'10px', borderRadius:'4px', marginBottom:'1rem', fontSize:'0.9rem' }
}

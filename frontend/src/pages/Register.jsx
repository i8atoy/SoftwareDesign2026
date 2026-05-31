import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { authApi } from '../api/axios'

export default function Register() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [form, setForm] = useState({ userName: '', password: '' })
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      await authApi.post('/api/auth/register', form)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data || t('errorTaken'))
    }
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}><h2>{t('createAccount')}</h2></div>
        <div style={styles.body}>
          {error && <div style={styles.error}>{error}</div>}
          <form onSubmit={handleSubmit}>
            <label style={styles.label}>{t('username')}</label>
            <input style={styles.input} value={form.userName}
              onChange={e => setForm({...form, userName: e.target.value})} required />
            <label style={styles.label}>{t('password')}</label>
            <input style={styles.input} type="password" value={form.password}
              onChange={e => setForm({...form, password: e.target.value})} required />
            <button style={styles.btn} type="submit">{t('register')}</button>
          </form>
          <p style={{textAlign:'center', marginTop:'1rem', fontSize:'0.9rem'}}>
            {t('alreadyHave')} <Link to="/login">{t('login')}</Link>
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

import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import api from '../api/axios'

export default function Players() {
  const { t } = useTranslation()
  const [players, setPlayers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/api/players')
      .then(res => setPlayers(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>{t('loading')}</p>

  return (
    <div>
      <h1 style={{ marginBottom:'1.5rem' }}>{t('players')}</h1>
      <div style={styles.grid}>
        {players.map(p => (
          <div key={p.id} style={styles.card}>
            <img src={p.photoUrl} alt={p.name} style={styles.photo}
              onError={e => e.target.style.display='none'} />
            <div style={styles.body}>
              <h4>{p.name}</h4>
              <p style={styles.pos}>{p.position}</p>
              <span style={styles.badge}>Age: {p.age}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

const styles = {
  grid: { display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(160px, 1fr))', gap:'1rem' },
  card: { background:'white', borderRadius:'8px', boxShadow:'0 2px 8px rgba(0,0,0,0.08)', overflow:'hidden' },
  photo: { width:'100%', height:'180px', objectFit:'cover', objectPosition:'top', background:'#3e4c54' },
  body: { padding:'0.75rem', textAlign:'center' },
  pos: { color:'#666', fontSize:'0.85rem', margin:'0.25rem 0' },
  badge: { background:'#6c757d', color:'white', padding:'2px 8px', borderRadius:'20px', fontSize:'0.75rem' }
}

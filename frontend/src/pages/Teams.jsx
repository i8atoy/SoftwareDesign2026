import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import api from '../api/axios'

export default function Teams() {
  const { t } = useTranslation()
  const [teams, setTeams] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/api/teams')
      .then(res => setTeams(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>{t('loading')}</p>

  return (
    <div>
      <h1 style={{ marginBottom:'1.5rem' }}>{t('teams')}</h1>
      <div style={styles.grid}>
        {teams.map(team => (
          <div key={team.id} style={styles.card}>
            <img src={team.photoUrl} alt={team.name} style={styles.logo}
              onError={e => e.target.style.display='none'} />
            <div style={styles.body}>
              <h3>{team.name}</h3>
              <p style={styles.meta}>🌍 {team.country}</p>
              <span style={styles.badge}>{team.vrsPoints} VRS</span>
              <div style={styles.players}>
                {team.players?.map(p => (
                  <div key={p.id} style={styles.player}>
                    <img src={p.photoUrl} alt={p.name} style={styles.avatar}
                      onError={e => e.target.style.display='none'} />
                    <div>
                      <div style={{fontWeight:'bold', fontSize:'0.9rem'}}>{p.name}</div>
                      <div style={{fontSize:'0.75rem', color:'#666'}}>{p.position}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

const styles = {
  grid: { display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(280px, 1fr))', gap:'1.5rem' },
  card: { background:'white', borderRadius:'8px', boxShadow:'0 2px 8px rgba(0,0,0,0.08)', overflow:'hidden' },
  logo: { width:'100%', height:'150px', objectFit:'contain', background:'#212529', padding:'1rem' },
  body: { padding:'1.25rem' },
  meta: { color:'#666', fontSize:'0.9rem', margin:'0.25rem 0 0.75rem' },
  badge: { background:'#007bff', color:'white', padding:'3px 10px', borderRadius:'20px', fontSize:'0.8rem' },
  players: { marginTop:'1rem', borderTop:'1px solid #eee', paddingTop:'0.75rem', display:'flex', flexDirection:'column', gap:'0.5rem' },
  player: { display:'flex', alignItems:'center', gap:'0.75rem' },
  avatar: { width:'36px', height:'36px', borderRadius:'50%', objectFit:'cover', background:'#3e4c54' }
}

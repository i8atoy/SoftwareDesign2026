import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import api from '../api/axios'

export default function Tournaments() {
  const { t } = useTranslation()
  const [tournaments, setTournaments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ name:'', location:'', prizeMoney:0, vrsPoints:0 })
  const [search, setSearch] = useState({ query:'', location:'' })

  const roles = JSON.parse(localStorage.getItem('roles') || '[]')
  const isAdmin = roles.includes('ADMIN')
  const isManager = roles.includes('MANAGER')
  const managedTeamId = localStorage.getItem('managedTeamId')

  async function load(q, l) {
    try {
      setLoading(true)
      const params = {}
      if (q) params.query = q
      if (l) params.location = l
      const res = await api.get('/api/tournaments', { params })
      setTournaments(res.data)
    } catch { setError('Failed to load tournaments') }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  async function handleSave(e) {
    e.preventDefault()
    try {
      if (editing) {
        await api.put(`/api/tournaments/${editing.id}`, form)
      } else {
        await api.post('/api/tournaments', form)
      }
      setShowForm(false); setEditing(null)
      setForm({ name:'', location:'', prizeMoney:0, vrsPoints:0 })
      load()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save')
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this tournament?')) return
    try { await api.delete(`/api/tournaments/${id}`); load() }
    catch { setError('Failed to delete') }
  }

  async function handleJoin(id) {
    try { await api.post(`/api/tournaments/${id}/join`); load() }
    catch (err) { setError(err.response?.data || 'Failed to join') }
  }

  async function handleLeave(id) {
    if (!confirm('Leave this tournament?')) return
    try { await api.post(`/api/tournaments/${id}/leave`); load() }
    catch { setError('Failed to leave') }
  }

  function startEdit(t) {
    setEditing(t)
    setForm({ name:t.name, location:t.location, prizeMoney:t.prizeMoney, vrsPoints:t.vrsPoints })
    setShowForm(true)
  }

  if (loading) return <p>{t('loading')}</p>

  return (
    <div>
      <div style={styles.header}>
        <h1>{t('tournaments')}</h1>
        {isAdmin && <button style={styles.btn} onClick={() => setShowForm(true)}>{t('createTournament')}</button>}
      </div>

      <div style={styles.searchBar}>
        <input placeholder={t('name')} value={search.query} style={styles.searchInput}
          onChange={e => setSearch({...search, query: e.target.value})} />
        <input placeholder={t('location')} value={search.location} style={styles.searchInput}
          onChange={e => setSearch({...search, location: e.target.value})} />
        <button style={styles.btn} onClick={() => load(search.query, search.location)}>🔍</button>
        <button style={styles.btnSecondary} onClick={() => { setSearch({query:'',location:''}); load() }}>✕</button>
      </div>

      {error && <div style={styles.error}>{error}</div>}

      {showForm && (
        <div style={styles.modal}>
          <div style={styles.modalBox}>
            <h2>{editing ? t('edit') : t('createTournament')}</h2>
            <form onSubmit={handleSave}>
              <label style={styles.label}>{t('name')}</label>
              <input style={styles.input} value={form.name} onChange={e => setForm({...form, name:e.target.value})} required />
              <label style={styles.label}>{t('location')}</label>
              <input style={styles.input} value={form.location} onChange={e => setForm({...form, location:e.target.value})} required />
              <label style={styles.label}>{t('prizeMoney')}</label>
              <input style={styles.input} type="number" value={form.prizeMoney} onChange={e => setForm({...form, prizeMoney:e.target.value})} />
              <label style={styles.label}>{t('vrsPoints')}</label>
              <input style={styles.input} type="number" value={form.vrsPoints} onChange={e => setForm({...form, vrsPoints:e.target.value})} />
              <div style={{display:'flex', gap:'1rem', marginTop:'1rem'}}>
                <button style={styles.btn} type="submit">{t('save')}</button>
                <button style={styles.btnSecondary} type="button"
                  onClick={() => { setShowForm(false); setEditing(null) }}>{t('cancel')}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div style={styles.grid}>
        {tournaments.map(tournament => (
            <div key={tournament.id} style={styles.card}>
              <div style={styles.cardHeader}><h3>{tournament.name}</h3></div>
              <div style={styles.cardBody}>
                <p style={styles.meta}>📍 {tournament.location}</p>
                <div style={styles.badges}>
                  <span style={styles.badge}>{tournament.vrsPoints} VRS</span>
                  <span style={{...styles.badge, background:'#28a745'}}>${tournament.prizeMoney?.toLocaleString()}</span>
                </div>

                {isAdmin && (
                    <div style={styles.actions}>
                      <button style={styles.btnSm} onClick={() => startEdit(tournament)}>{t('edit')}</button>
                      <button style={{...styles.btnSm, background:'#dc3545'}} onClick={() => handleDelete(tournament.id)}>{t('delete')}</button>
                    </div>
                )}

                {isManager && managedTeamId && (
                    <div style={styles.actions}>
                      {tournament.teamIds?.includes(Number(managedTeamId))
                          ? <button style={{...styles.btnSm, background:'#ffc107', color:'#000'}} onClick={() => handleLeave(tournament.id)}>{t('leave')}</button>
                          : <button style={{...styles.btnSm, background:'#28a745'}} onClick={() => handleJoin(tournament.id)}>{t('join')}</button>
                      }
                    </div>
                )}

                <div style={styles.teamSection}>
                  <small style={styles.sectionTitle}>{t('participating')}</small>
                  {tournament.teams?.length > 0 ? tournament.teams.map(team => (
                      <div key={team.id} style={styles.teamRow}>
                        <img src={team.photoUrl} alt={team.name} style={styles.teamLogo}
                             onError={e => e.target.style.display='none'} />
                        <span>{team.name}</span>
                      </div>
                  )) : <small style={{color:'#999'}}>{t('noTeams')}</small>}
                </div>
              </div>
            </div>
        ))}
      </div>
    </div>
  )
}

const styles = {
  header: { display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:'1.5rem' },
  searchBar: { display:'flex', gap:'0.5rem', marginBottom:'1.5rem' },
  searchInput: { padding:'8px 12px', border:'1px solid #ddd', borderRadius:'4px', fontSize:'0.95rem' },
  grid: { display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(320px, 1fr))', gap:'1.5rem' },
  card: { background:'white', borderRadius:'8px', boxShadow:'0 2px 8px rgba(0,0,0,0.08)', overflow:'hidden' },
  cardHeader: { background:'#1a1e22', color:'white', padding:'1.25rem', textAlign:'center' },
  cardBody: { padding:'1.25rem' },
  meta: { color:'#666', fontSize:'0.9rem', marginBottom:'0.75rem' },
  badges: { display:'flex', gap:'0.5rem', marginBottom:'1rem' },
  badge: { background:'#007bff', color:'white', padding:'3px 10px', borderRadius:'20px', fontSize:'0.8rem' },
  actions: { display:'flex', gap:'0.5rem', marginBottom:'1rem' },
  btnSm: { padding:'5px 12px', background:'#1a1e22', color:'white', border:'none', borderRadius:'4px', cursor:'pointer', fontSize:'0.85rem' },
  teamSection: { borderTop:'1px solid #eee', paddingTop:'0.75rem', marginTop:'0.5rem' },
  sectionTitle: { color:'#999', textTransform:'uppercase', fontSize:'0.7rem', fontWeight:'bold', display:'block', marginBottom:'0.5rem' },
  teamRow: { display:'flex', alignItems:'center', gap:'0.5rem', padding:'4px 0' },
  teamLogo: { width:'28px', height:'28px', objectFit:'contain', background:'#212529', borderRadius:'4px', padding:'2px' },
  btn: { padding:'8px 16px', background:'#1a1e22', color:'white', border:'none', borderRadius:'4px', cursor:'pointer', fontWeight:'bold' },
  btnSecondary: { padding:'8px 16px', background:'#6c757d', color:'white', border:'none', borderRadius:'4px', cursor:'pointer' },
  modal: { position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:1000 },
  modalBox: { background:'white', padding:'2rem', borderRadius:'8px', width:'480px', maxWidth:'90vw' },
  label: { display:'block', marginBottom:'4px', fontWeight:'600', fontSize:'0.85rem', textTransform:'uppercase', color:'#666' },
  input: { width:'100%', padding:'10px', marginBottom:'1rem', border:'1px solid #ddd', borderRadius:'4px', fontSize:'1rem' },
  error: { background:'#f8d7da', color:'#721c24', padding:'10px', borderRadius:'4px', marginBottom:'1rem' }
}

import { Link, Outlet, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import i18n from '../i18n/i18n'

export default function Navbar() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const username = localStorage.getItem('username')
  const roles = JSON.parse(localStorage.getItem('roles') || '[]')

  function logout() {
    localStorage.clear()
    navigate('/login')
  }

  function switchLang(lang) {
    i18n.changeLanguage(lang)
    localStorage.setItem('lang', lang)
  }

  return (
    <>
      <nav style={styles.nav}>
        <div style={styles.brand}>🏆 TourneyPro</div>
        <div style={styles.links}>
          <Link to="/tournaments" style={styles.link}>{t('tournaments')}</Link>
          <Link to="/teams" style={styles.link}>{t('teams')}</Link>
          <Link to="/players" style={styles.link}>{t('players')}</Link>
          <Link to="/chat" style={styles.link}>{t('chat')}</Link>
        </div>
        <div style={styles.right}>
          <span style={styles.user}>👤 {username}</span>
          <button onClick={() => switchLang('en')} style={styles.langBtn}>EN</button>
          <button onClick={() => switchLang('ro')} style={styles.langBtn}>RO</button>
          <button onClick={logout} style={styles.logoutBtn}>{t('logout')}</button>
        </div>
      </nav>
      <div style={{ padding: '2rem' }}>
        <Outlet />
      </div>
    </>
  )
}

const styles = {
  nav: { background: '#1a1e22', color: 'white', display: 'flex', alignItems: 'center',
    padding: '0 2rem', height: '60px', gap: '2rem' },
  brand: { fontWeight: 'bold', fontSize: '1.2rem', marginRight: 'auto' },
  links: { display: 'flex', gap: '1.5rem' },
  link: { color: 'white', textDecoration: 'none', fontSize: '0.95rem' },
  right: { display: 'flex', alignItems: 'center', gap: '0.75rem', marginLeft: 'auto' },
  user: { fontSize: '0.85rem', color: '#adb5bd' },
  langBtn: { background: 'transparent', color: '#adb5bd', border: '1px solid #adb5bd',
    borderRadius: '4px', padding: '2px 8px', cursor: 'pointer', fontSize: '0.8rem' },
  logoutBtn: { background: '#dc3545', color: 'white', border: 'none',
    borderRadius: '4px', padding: '6px 14px', cursor: 'pointer' }
}

import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import i18n from '../i18n/i18n'

export default function Layout() {
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
    <div>
      <nav className="navbar">
        <div className="navbar-brand">🏆 TourneyPro</div>
        <div className="navbar-links">
          <NavLink to="/tournaments">{t('tournaments')}</NavLink>
          <NavLink to="/teams">{t('teams')}</NavLink>
          <NavLink to="/players">{t('players')}</NavLink>
          <NavLink to="/chat">{t('chat')}</NavLink>
        </div>
        <div className="navbar-right">
          <span className="navbar-user">{username}</span>
          <button className="lang-btn" onClick={() => switchLang('en')}>EN</button>
          <button className="lang-btn" onClick={() => switchLang('ro')}>RO</button>
          <button className="btn btn-outline" onClick={logout}>{t('logout')}</button>
        </div>
      </nav>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}

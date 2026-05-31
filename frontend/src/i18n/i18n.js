import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

const resources = {
  en: {
    translation: {
      tournaments: 'Tournaments', teams: 'Teams', players: 'Players', chat: 'Chat',
      login: 'Login', logout: 'Logout', register: 'Register',
      username: 'Username', password: 'Password',
      createTournament: 'Create Tournament', edit: 'Edit', delete: 'Delete',
      join: 'Join', leave: 'Leave', save: 'Save', cancel: 'Cancel',
      name: 'Name', location: 'Location', prizeMoney: 'Prize Money', vrsPoints: 'VRS Points',
      noTeams: 'No teams registered yet', participating: 'Participating Teams',
      welcomeBack: 'Welcome Back', createAccount: 'Create Account',
      alreadyHave: 'Already have an account?', newHere: 'New here?',
      sendMessage: 'Send', typeMessage: 'Type a message...',
      errorInvalid: 'Invalid username or password', errorTaken: 'Username already taken',
      loading: 'Loading...', noData: 'No data available',
    }
  },
  ro: {
    translation: {
      tournaments: 'Turnee', teams: 'Echipe', players: 'Jucători', chat: 'Chat',
      login: 'Autentificare', logout: 'Deconectare', register: 'Înregistrare',
      username: 'Utilizator', password: 'Parolă',
      createTournament: 'Creează Turneu', edit: 'Editează', delete: 'Șterge',
      join: 'Alătură-te', leave: 'Părăsește', save: 'Salvează', cancel: 'Anulează',
      name: 'Nume', location: 'Locație', prizeMoney: 'Premiu', vrsPoints: 'Puncte VRS',
      noTeams: 'Nicio echipă înregistrată', participating: 'Echipe Participante',
      welcomeBack: 'Bine ai revenit', createAccount: 'Creează Cont',
      alreadyHave: 'Ai deja un cont?', newHere: 'Ești nou?',
      sendMessage: 'Trimite', typeMessage: 'Scrie un mesaj...',
      errorInvalid: 'Utilizator sau parolă incorectă', errorTaken: 'Utilizatorul există deja',
      loading: 'Se încarcă...', noData: 'Nu există date',
    }
  }
}

i18n.use(initReactI18next).init({
  resources, lng: localStorage.getItem('lang') || 'en', interpolation: { escapeValue: false }
})

export default i18n

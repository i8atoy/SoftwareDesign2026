import { useState, useEffect, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export default function Chat() {
  const { t } = useTranslation()
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [connected, setConnected] = useState(false)
  const clientRef = useRef(null)
  const bottomRef = useRef(null)
  const username = localStorage.getItem('username')

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        setConnected(true)
        client.subscribe('/topic/chat', msg => {
          const body = JSON.parse(msg.body)
          setMessages(prev => [...prev, body])
        })
      },
      onDisconnect: () => setConnected(false),
      reconnectDelay: 3000
    })
    client.activate()
    clientRef.current = client
    return () => client.deactivate()
  }, [])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  function sendMessage(e) {
    e.preventDefault()
    if (!input.trim() || !connected) return
    clientRef.current.publish({
      destination: '/app/chat',
      body: JSON.stringify({ sender: username, content: input.trim() })
    })
    setInput('')
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>{t('chat')}</h2>
        <span style={{...styles.dot, background: connected ? '#28a745' : '#dc3545'}} />
      </div>
      <div style={styles.messages}>
        {messages.map((m, i) => (
          <div key={i} style={{...styles.msg, alignSelf: m.sender === username ? 'flex-end' : 'flex-start'}}>
            <div style={{...styles.bubble, background: m.sender === username ? '#1a1e22' : '#e9ecef',
              color: m.sender === username ? 'white' : '#1a1e22'}}>
              {m.sender !== username && <div style={styles.sender}>{m.sender}</div>}
              <div>{m.content}</div>
              <div style={styles.time}>{new Date(m.timestamp).toLocaleTimeString()}</div>
            </div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>
      <form style={styles.form} onSubmit={sendMessage}>
        <input style={styles.input} value={input} placeholder={t('typeMessage')}
          onChange={e => setInput(e.target.value)} disabled={!connected} />
        <button style={styles.btn} type="submit" disabled={!connected}>{t('sendMessage')}</button>
      </form>
    </div>
  )
}

const styles = {
  container: { background:'white', borderRadius:'8px', boxShadow:'0 2px 8px rgba(0,0,0,0.08)',
    height:'calc(100vh - 120px)', display:'flex', flexDirection:'column' },
  header: { padding:'1rem 1.5rem', borderBottom:'1px solid #eee', display:'flex', alignItems:'center', gap:'0.75rem' },
  dot: { width:'10px', height:'10px', borderRadius:'50%' },
  messages: { flex:1, overflowY:'auto', padding:'1rem', display:'flex', flexDirection:'column', gap:'0.5rem' },
  msg: { display:'flex', maxWidth:'70%' },
  bubble: { padding:'0.6rem 1rem', borderRadius:'12px', fontSize:'0.95rem' },
  sender: { fontWeight:'bold', fontSize:'0.8rem', marginBottom:'2px', color:'#666' },
  time: { fontSize:'0.7rem', opacity:0.6, marginTop:'4px', textAlign:'right' },
  form: { padding:'1rem', borderTop:'1px solid #eee', display:'flex', gap:'0.75rem' },
  input: { flex:1, padding:'10px', border:'1px solid #ddd', borderRadius:'4px', fontSize:'0.95rem' },
  btn: { padding:'10px 20px', background:'#1a1e22', color:'white', border:'none', borderRadius:'4px', cursor:'pointer' }
}

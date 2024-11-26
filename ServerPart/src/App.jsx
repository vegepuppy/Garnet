import { useState } from 'react'
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css'
import

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [userName, setUserName] = useState('');

  return (
    <>
      <div>Garnet Web</div>
      <div>登录/注册</div>
      <div>用户名</div>
      <input type='text' name='UserName'></input>
    </>
  )
}

export default App

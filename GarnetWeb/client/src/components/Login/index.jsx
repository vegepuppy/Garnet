import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:3001/login", {
        username,
        password,
      });

      if (response.data.success) {
        setMessage("登录成功！");
        navigate('/dashboard')
      } else {
        setMessage("用户名或密码错误！");
      }
    } catch (error) {
      console.error("登录失败", error);
      setMessage("登录失败，请稍后重试。");
    }
  };

  return (
    <div>
      <h1>用户登录</h1>
      <form onSubmit={handleSubmit}>
        <label>
          用户名:
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <br />
        <label>
          密码:
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <br />
        <button type="submit">登录</button>
      </form>
      {message && <p>{message}</p>}
    </div>
  );
};

export default Login;

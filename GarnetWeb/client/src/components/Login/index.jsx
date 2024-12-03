import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import {Button} from "react-bootstrap";
import {Form} from "react-bootstrap";
import {FloatingLabel} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.css';
import Accordion from 'react-bootstrap/Accordion';

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
        navigate("/dashboard");
      } else {
        setMessage("用户名或密码错误！");
      }
    } catch (error) {
      console.error("登录失败", error);
      setMessage("登录失败，请稍后重试。");
    }
  };

  return (
    <div >
      <Form onSubmit={handleSubmit} className="w-auto">
        <h3>登录Garnet</h3>
        <FloatingLabel label="用户名" controlId="floatingInput" className="mb-3">
          <Form.Control
              type="text"
              onChange={e => setUsername(e.target.value)}
              value={username}
              placeholder="Username"
              required/>
        </FloatingLabel>
        <FloatingLabel label="密码" controlId="floatingPassword" className={"mb-3"}>
          <Form.Control
              type="password"
              value={password}
              placeholder="Password"
              onChange={(e) => setPassword(e.target.value)}
              required
          />
        </FloatingLabel>
        <Button type="submit" size="lg" variant="outline-primary">登录</Button>
      </Form>
      {message && <p>{message}</p>}
    </div>
  );
};

export default Login;

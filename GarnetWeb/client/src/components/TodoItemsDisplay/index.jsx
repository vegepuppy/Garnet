import React, { useState, useEffect } from 'react';
import "bootstrap/dist/css/bootstrap.css"
import {ListGroup} from "react-bootstrap";
import "../../App.css"

function TodoItemDisplay(){
  const [data, setData] = useState([]); // 存储从后端获取的数据
  const [loading, setLoading] = useState(true); // 加载状态
  const [error, setError] = useState(null); // 错误状态

  // 获取数据
  useEffect(() => {
    fetch('http://localhost:3001/todoitem') // 确保URL与后端路径一致
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        setData(data); // 保存数据到state
        setLoading(false); // 数据加载完成
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  // 渲染组件
  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <container className="max-">
      <h1>待办事项</h1>
      <ListGroup className="w-100 min-vw-62" >
        {data.map(item => (
          <ListGroup.Item key={item.id} className="list-group-item d-flex align-items-center justify-content-between">
              <span className="left-text" >{item.task} </span>
              <span className="right-text">{` ${item.duedate} ${item.done? "(已完成)":"(未完成)"}`}</span>
          </ListGroup.Item>
        ))}
      </ListGroup>
    </container>
  );
};

export default TodoItemDisplay;
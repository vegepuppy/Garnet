import React, { useState, useEffect } from 'react';

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
    <div>
      <h1>TodoItems</h1>
      <ul>
        {data.map(item => (
          <li key={item.id}>
            {item.done? <div>done</div> : <div>not done</div> }
            <div>{`${item.task} -- ${item.duedate}`}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TodoItemDisplay;
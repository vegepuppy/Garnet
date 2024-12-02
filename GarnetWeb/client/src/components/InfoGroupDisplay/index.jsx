import { useState } from "react";
import { useEffect } from "react";
import OneInfoGroup from "../OneInfoGroup";

export default function InfoGroupDisplay() {
  const [loading, setLoading] = useState(true); //要改！！！！！！！！！！！
  const [allInfoGroup, setAllInfoGroup] = useState([]);
  const [error, setError] = useState(null); // 错误状态

  useEffect(() => {
    fetch("http://localhost:3001/infogroup")
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        setAllInfoGroup(data); // 保存数据到state
        setLoading(false); // 数据加载完成
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <>
      <h1>InfoItems</h1>
      <div>
        {allInfoGroup.map((item) => (
          <OneInfoGroup infoGroup={item} key={item.id}/>
        ))}
      </div>
    </>
  );
}

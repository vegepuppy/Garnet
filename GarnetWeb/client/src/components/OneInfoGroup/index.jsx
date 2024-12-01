import { useRef, useState } from "react";
import { useEffect } from "react";

export default function OneInfoGroup({ infoGroup }) {
  const [folded, setfold] = useState(true);
  const [loading, setLoading] = useState(true);
  const [infoItems, setInfoItems] = useState([]);

  // 获取数据
  useEffect(() => {
    fetch(`http://localhost:3001/infoitem`)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        setInfoItems(data); // 保存数据到state
        setLoading(false); // 数据加载完成
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return <div>loading...</div>;
  }

  return (
    <>
      <div>{`infoGroup:${infoGroup.name}`}</div>
      {folded ? (
        <div onClick={() => setfold(false)}>unfold</div>
      ) : (
        <div onClick={() => setfold(true)}>fold</div>
      )}
      {!folded && (
        <ul>
          {infoItems.map((item) => (
              item.belong === infoGroup.id && (
                <li key={item.id}><a href={item.content}>{item.display}</a></li>
              )
            )
        )}
        </ul>
      )}
    </>
  );
}

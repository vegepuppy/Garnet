import { useState } from "react";
import { useEffect } from "react";

export default function OneInfoGroup({ infoGroup }) {
  const [folded, setfold] = useState(true);
  const [loading, setLoading] = useState(true);
  const [infoItems, setInfoItems] = useState([]); //整个数据库里面所有的InfoItem

  const [showInputDialog, setShowInputDialog] = useState(false); //是否显示输入框用于让用户输入链接
  const [inputLink, setInputLink] = useState('');// 输入框里面的内容

  // 从后端获取数据，获得的是所有的InfoItem，而不是当前InfoGroup里面的InfoItem
  useEffect(() => {
    fetch(`http://localhost:3001/infoitem`)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        setInfoItems(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  // 处理用户添加数据，暂时只支持添加链接
  function handleAddLink() {
    setShowInputDialog(true);
  }

  function handleConfirm(){
    // console.log('user input link:', inputLink, `in infoGroup ${infoGroup.name}`);
    setShowInputDialog(false);// 关闭输入框
    setInputLink('')// 清空框里面的内容
  }

  function handleInputChange(){
    setInputLink(event.target.value);
  }

  if (loading) {
    return <div>loading...</div>;
  }

  /* 查找的逻辑是：从后端获取所有的InfoItem，不管是不是这个InfoGroup里面的
  之后判断：如果InfoItem的belong与这个InfoGroup的id相同，那么就把这个InfoItem显示出来
  这样会引入性能问题（数据取多了），但是目前数据量不太大，而且本地调试不涉及网络通信，先敏捷开发
  */
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
          {infoItems.map(
            (item) =>
              item.belong === infoGroup.id && (
                <li key={item.id}>
                  <a href={item.content}>{item.display}</a>
                </li>
              )
          )}
        </ul>
      )}
      <div onClick={handleAddLink}>添加链接</div>
      {showInputDialog && (
        <div>
          <input
            type="text"
            value={inputLink}
            onChange={handleInputChange}
            placeholder="请输入内容"
          />
          <button onClick={handleConfirm}>确认</button>
        </div>
      )}
    </>
  );
}

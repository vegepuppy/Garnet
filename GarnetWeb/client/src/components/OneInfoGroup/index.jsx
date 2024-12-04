import { useState } from "react";
import { useEffect } from "react";
import {Accordion, ListGroup, Stack, Form, Button} from "react-bootstrap";
import "../../App.css"
import "bootstrap/dist/css/bootstrap.min.css"



// eslint-disable-next-line react/prop-types
export default function OneInfoGroup({ infoGroup }) {
  const [folded, setfold] = useState(true);
  const [loading, setLoading] = useState(true);
  const [infoItems, setInfoItems] = useState([]); //整个数据库里面所有的InfoItem

  const [showInputDialog, setShowInputDialog] = useState(false); //是否显示输入框用于让用户输入链接
  const [inputLink, setInputLink] = useState(""); // 输入框里面的内容

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
        // setError(err.message);
        setLoading(false);
      });
  }, []);

  // 处理用户添加数据，暂时只支持添加链接
  function handleAddLink() {
    setShowInputDialog(true);
  }

  async function handleConfirm() {
    console.log(
      "user input link:",
      inputLink,
      `in infoGroup ${infoGroup.name}`
    ); // 将用户输入打印出来

    let pageTitle = "";// 爬到的网页标题

    fetch(`http://localhost:3001/gettitle?url=${inputLink}`)
      .then((response) => response.json())
      .then((data) => {
        console.log("网页标题:", data.title);
        if (data.title === null) pageTitle = "无效链接:" + inputLink;
        else pageTitle = data.title;
      })
      .catch((error) => console.error("Error:", error));

    // 停下来等两秒，否则在爬取到标题之前就把newInfoItem加进去了
    await new Promise((r) => setTimeout(r, 2000));

    // 把用户的输入作为一个InfoItem发往后端
    const newInfoItem = {
      belong: infoGroup.id,
      content: inputLink,
      display: pageTitle,
    };
    console.log("data", newInfoItem);
    const API_URL = "http://localhost:3001/newinfoitem";

    // 更新前端UI，显示新加入的InfoItem
    setInfoItems([...infoItems, newInfoItem]);

    fetch(API_URL, {
      method: "POST", // 使用 POST 方法
      headers: {
        "Content-Type": "application/json", // 指定发送的数据类型为 JSON
      },
      body: JSON.stringify(newInfoItem), // 将数据转换为 JSON 字符串
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json(); // 解析响应为 JSON
      })
      .then((responseData) => {
        console.log("服务器响应:", responseData);
      })
      .catch((error) => {
        console.error("请求出错:", error);
      });

    setShowInputDialog(false); // 关闭输入框
    setInputLink(""); // 清空框里面的内容
  }

  function handleInputChange() {
    setInputLink(event.target.value);
  }

  if (loading) {
    return <div>loading...</div>;
  }
  console.log("infoitems:", infoItems);

  /* 查找显示的逻辑是：从后端获取所有的InfoItem，不管是不是这个InfoGroup里面的
  之后判断：如果InfoItem的belong与这个InfoGroup的id相同，那么就把这个InfoItem显示出来
  这样会引入性能问题（数据取多了），但是目前数据量不太大，而且本地调试不涉及网络通信，先敏捷开发
  */
  return (
    <Accordion.Item eventKey={infoGroup.id} >
      <Accordion.Header>{infoGroup.name}</Accordion.Header>
      <Accordion.Body>
        <Stack gap = {4} alignItems="left" textAlign="left">
          {infoItems.map(
            (item) =>
              item.belong === infoGroup.id && (
                <a key={item.id} className="text-start"
                   href={item.content} >
                  {item.display}
                </a>
              )
          )}
          <Stack direction="horizontal" gap={3}>
            <Form.Control
                className="me-auto w-90"
                placeholder="请输入链接"
                onClick={handleAddLink}
                type="text"
                value={inputLink}
                onChange={handleInputChange}
            />
            <Button variant="secondary" onClick={handleConfirm} className="mw-100">添加链接</Button>
          </Stack>
        </Stack>

      </Accordion.Body>
    </Accordion.Item>
  );
}

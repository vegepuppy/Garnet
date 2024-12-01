const express = require("express");
const axios = require('axios');
const cheerio = require('cheerio');

// const InfoGroup = require("../classes/InfoGroup");
const PORT = process.env.PORT || 3001;

const users = [];
let infoItems = [];
let todoItems = [];
let infoGroups = [];
let newInfoItems = [];
/* 所有用户在网页端创建的InfoItem，创建时就已经选择好了InfoGroup
InfoItem同步的逻辑是：用户在前端创建后，点击确定时数据发送到后端，在newInfoItems数组里面暂存
用户在Android app获取后端的数据，获取之后服务器将数组清空  
*/

const app = express();
const cors = require("cors");
app.use(cors({ origin: "http://localhost:5173" })); //允许前端通信
app.use(express.json());

app.post("/register", (req, res) => {
  const { username, password } = req.body;

  const userExists = users.some((user) => user.username === username);
  if (userExists) {
    return res
      .status(400)
      .json({ success: false, message: "Username already exists." });
  }

  users.push({ username, password });

  console.log("registered users:", users);

  res
    .status(200)
    .json({ success: true, message: "user registered successfully." });
});

app.post("/infogroup", (req, res) => {
  infoGroups = [...req.body]

  console.log("receiving infoGroups");
  console.log("req.body:", req.body);

  if (!Array.isArray(infoItems)) {
    return res
      .status(400)
      .json({ success: false, message: "Invalid data format" });
  }
  console.log("Received infoGroups:", infoGroups);
});

app.post("/login", (req, res) => {
  const { username, password } = req.body;

  // 验证用户是否存在并且密码正确
  const user = users.find(
    (user) => user.username === username && user.password === password
  );
  if (!user) {
    console.log(
      `input username: ${username}, password:${password}, login failed!`
    );
    return res
      .status(401)
      .json({ success: false, message: "Invalid username or password" });
  }

  res.status(200).json({ success: true, message: "Login successful" });

  console.log(`username: ${username}, password:${password} login successfully`);
});

app.post("/infoitem", (req, res) => {
  infoItems = [...req.body];

  if (!Array.isArray(infoItems)) {
    return res
      .status(400)
      .json({ success: false, message: "Invalid data format" });
  }
  console.log("Received infoItems:", infoItems);

  res
    .status(200)
    .json({ success: true, message: "Data uploaded successfully" });
});

app.post('/newinfoitem', (req, res) => {
  let newInfoItem = req.body;

  console.log("newInfoItem:", newInfoItem);

  newInfoItems.push(newInfoItem);

  console.log("newInfoItems(ARRAY):", newInfoItems);

  res
    .status(200)
    .json({ success: true, message: "Data uploaded successfully" });
});

app.post("/todoitem", (req, res) => {
  todoItems = [...req.body];

  if (!Array.isArray(todoItems)) {
    return res
      .status(400)
      .json({ success: false, message: "Invalid data format" });
  }

  console.log("Received todo:", todoItems);

  res
    .status(200)
    .json({ success: true, message: "Data uploaded successfully" });
});

app.get("/infoitem", (req, res) => {
  res.json(infoItems);
});

app.get("/todoitem", (req, res) => {
  res.json(todoItems);
});

app.get("/infogroup", (req, res) =>{
  res.json(infoGroups);
})

app.get('/newinfoitem', (req, res) =>{
  res.json(newInfoItems);

  newInfoItems = []; //清空数组，防止重复读取
})

app.get('/gettitle', async (req, res) => {
  const { url } = req.query;
  if (!url) {
    return res.status(400).send('URL is required');
  }
  try {
    const response = await axios.get(url);
    const $ = cheerio.load(response.data);
    const title = $('title').text();
    res.json({ title });
  } catch (error) {
    res.status(500).send('Error fetching the page');
  }
});

app.listen(PORT, () => {
  console.log(`Server listening on ${PORT}`);
});

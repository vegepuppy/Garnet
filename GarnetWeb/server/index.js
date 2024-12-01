const express = require("express");
const InfoGroup = require("../classes/InfoGroup");
const PORT = process.env.PORT || 3001;

const users = [];
let infoItems = [];
let todoItems = [];
let infoGroups = [];

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
  req.body.forEach((element) => {
    let oneInfoGroup = new InfoGroup(element.id, element.name);
    infoGroups.push(oneInfoGroup);
  });

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

app.listen(PORT, () => {
  console.log(`Server listening on ${PORT}`);
});

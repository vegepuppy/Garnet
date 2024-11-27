const express = require("express");

const PORT = process.env.PORT || 3001;

const app = express();
app.use(express.json());

app.get("/api", (req, res) => {
    res.json({ message: "Hello from server!" });
  });

app.post('/api', (req, res) => {
    const {username} = req.body;
    console.log('Received username from Android:', username);
    res.status(200).json({success:true, message:'Username Received!'});
});

app.listen(PORT, () => {
  console.log(`Server listening on ${PORT}`);
});
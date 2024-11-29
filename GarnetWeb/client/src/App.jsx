import React from "react";
import "./App.css";
import Login from "./components";
import InfoItemDisplay from "./components/InfoItemsDisplay";
import TodoItemDisplay from "./components/TodoItemsDisplay";

function App() {
  return (
    <div className="App">
      <Login />
      <InfoItemDisplay/>
      <TodoItemDisplay/>
    </div>
  );
}

export default App;


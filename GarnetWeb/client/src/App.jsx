import React from "react";
import "./App.css";

function App() {
  const [data, setData] = React.useState(null);

  React.useEffect(() => {
    fetch("/api")
      .then((res) => res.json())
      .then((data) => setData(data.message));

      console.log(data);
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <p>{data ? data: "Loading..."}</p>
      </header>
    </div>
  );
}

export default App;
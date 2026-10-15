import { useState } from "react";

import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";

import Header from "./components/Header/header.jsx";
import Footer from "./components/Footer/Footer.jsx";
import Login from "./components/Login/Login.jsx";

function App() {
  // Reset authentication whenever the page is loaded or refreshed
  const [authData, setAuthData] = useState(null);

  return (
    <div className="app">
      {!authData ? (
        <Login onLoginSuccess={setAuthData} />
      ) : (
        <>
          <Header />

          <main className="app-body">
            <div className="container">
              <h1>Student Management Dashboard</h1>
              <p>
                Your main student list, forms, or tables will go right here.
              </p>
            </div>
          </main>

          <Footer />
        </>
      )}
    </div>
  );
}

export default App;
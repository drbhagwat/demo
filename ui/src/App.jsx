import { useState, useEffect } from "react";
import axios from "axios";

import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";

import Header from "./components/Header/header.jsx";
import Footer from "./components/Footer/Footer.jsx";
import Login from "./components/Login/Login.jsx";

function App() {
  // Reset authentication whenever the page is loaded or refreshed
  const [authData, setAuthData] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!authData) return;

    const fetchUsers = async () => {
      setLoading(true);
      setError("");

      try {
        const response = await axios.get("http://localhost:8080/api/v1/users", {
          headers: {
            Authorization: `${authData.tokenType} ${authData.accessToken}`,
          },
        });
        setUsers(response.data);
      } catch (err) {
        setError(err.response?.data?.message || "Failed to load users.");
      } finally {
        setLoading(false);
      }
    };
    fetchUsers();
  }, [authData]);

  if (!authData) {
    return <Login onLoginSuccess={setAuthData} />;
  }

  return (
    <div className="app">
      <Header />

      <main className="app-body">
        <div className="container py-4">
          <h1>User Management</h1>

          {loading && <p>Loading users...</p>}

          {error && <p className="text-danger">{error}</p>}

          {!loading && !error && (
            <div className="table-responsive">
              <table className="table table-striped table-bordered">
                <thead className="table-dark">
                  <tr>
                    <th>Username</th>
                    <th>Roles</th>
                    <th>Created At</th>
                    <th>Enabled</th>
                    <th>Account Locked</th>
                  </tr>
                </thead>

                <tbody>
                  {users.length > 0 ? (
                    users.map((user) => (
                      <tr key={user.id}>
                        <td>{user.username}</td>
                        <td>{user.roles.join(", ")}</td>
                        <td>
                          {user.createdAt
                            ? new Date(user.createdAt).toLocaleString()
                            : "-"}
                        </td>
                        <td>{user.enabled ? "Yes" : "No"}</td>
                        <td>{user.accountNonLocked ? "No" : "Yes"}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="5" className="text-center">
                        No users found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}

export default App;

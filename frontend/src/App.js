import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import Dashboard from "./components/Dashboard";
import ProjectForm from "./components/ProjectForm";
import ProjectList from "./components/ProjectList";
import Chat from "./components/Chat";
import "./styles/App.css";

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem("token"));

    const handleLogin = () => {
        setIsAuthenticated(true);
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("username");
        setIsAuthenticated(false);
    };

    return (
        <Router>
            <div className="App">
                <header className="App-header">
                    <h1>Student Collaboration Platform</h1>
                    {isAuthenticated && (
                        <div>
                            <span>Welcome, {localStorage.getItem("username")}!</span>
                            <button onClick={handleLogout} style={{ marginLeft: "10px" }}>
                                Logout
                            </button>
                        </div>
                    )}
                </header>

                <Routes>
                    <Route
                        path="/login"
                        element={
                            isAuthenticated ? (
                                <Navigate to="/dashboard" />
                            ) : (
                                <Login onLogin={handleLogin} />
                            )
                        }
                    />
                    <Route
                        path="/register"
                        element={
                            isAuthenticated ? (
                                <Navigate to="/dashboard" />
                            ) : (
                                <Register />
                            )
                        }
                    />
                    <Route
                        path="/dashboard"
                        element={
                            isAuthenticated ? (
                                <Dashboard />
                            ) : (
                                <Navigate to="/login" />
                            )
                        }
                    />
                    <Route
                        path="/create-project"
                        element={
                            isAuthenticated ? (
                                <ProjectForm />
                            ) : (
                                <Navigate to="/login" />
                            )
                        }
                    />
                    <Route
                        path="/projects"
                        element={
                            isAuthenticated ? (
                                <ProjectList />
                            ) : (
                                <Navigate to="/login" />
                            )
                        }
                    />
                    <Route
                        path="/chat/:projectId"
                        element={
                            isAuthenticated ? (
                                <Chat />
                            ) : (
                                <Navigate to="/login" />
                            )
                        }
                    />
                    <Route
                        path="/"
                        element={
                            isAuthenticated ? (
                                <Navigate to="/dashboard" />
                            ) : (
                                <Navigate to="/login" />
                            )
                        }
                    />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
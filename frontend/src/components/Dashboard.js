import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMyProjects } from "../services/Api";
import "../styles/Dashboard.css";

function Dashboard() {
    const [myProjects, setMyProjects] = useState([]);
    const navigate = useNavigate();
    const fullName = localStorage.getItem("fullName");

    useEffect(() => {
        fetchMyProjects();
    }, []);

    const fetchMyProjects = async () => {
        try {
            const response = await getMyProjects();
            setMyProjects(response.data);
        } catch (err) {
            console.error("Error fetching projects:", err);
        }
    };

    return (
        <div className="dashboard-container">
            <h2>Welcome, {fullName}!</h2>
            
            <div className="dashboard-actions">
                <button 
                    className="btn-primary" 
                    onClick={() => navigate("/create-project")}
                >
                    Create New Project
                </button>
                <button 
                    className="btn-secondary" 
                    onClick={() => navigate("/projects")}
                >
                    Browse All Projects
                </button>
            </div>

            <div className="my-projects-section">
                <h3>My Projects ({myProjects.length})</h3>
                {myProjects.length === 0 ? (
                    <p className="no-projects">
                        You haven't joined any projects yet. Browse available projects or create your own!
                    </p>
                ) : (
                    <div className="project-grid">
                        {myProjects.map((project) => (
                            <div key={project.id} className="project-card">
                                <h4>{project.title}</h4>
                                <p className="project-description">{project.description}</p>
                                <div className="project-info">
                                    <span className="info-badge">
                                        👥 {project.memberCount}/{project.maxMembers} members
                                    </span>
                                    <span className={`status-badge ${project.status.toLowerCase()}`}>
                                        {project.status}
                                    </span>
                                </div>
                                <div className="project-meta">
                                    <small>Created by: {project.creatorName}</small>
                                </div>
                                <button 
                                    className="btn-chat"
                                    onClick={() => navigate(`/chat/${project.id}`)}
                                >
                                    Open Chat
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboard;
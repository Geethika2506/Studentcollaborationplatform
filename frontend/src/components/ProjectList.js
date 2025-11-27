import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getProjects, joinProject } from "../services/Api";
import "../styles/ProjectList.css";

function ProjectList() {
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState("all");
    const navigate = useNavigate();
    const currentUserId = parseInt(localStorage.getItem("userId"));

    useEffect(() => {
        fetchProjects();
    }, []);

    const fetchProjects = async () => {
        try {
            setLoading(true);
            const response = await getProjects();
            setProjects(response.data);
        } catch (err) {
            console.error("Error fetching projects:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleJoin = async (projectId) => {
        try {
            await joinProject(projectId);
            alert("Successfully joined the project!");
            fetchProjects();
        } catch (err) {
            console.error(err);
            alert(err.response?.data?.error || "Failed to join project");
        }
    };

    const filteredProjects = projects.filter((project) => {
        if (filter === "open") return project.status === "OPEN";
        if (filter === "full") return project.memberCount >= project.maxMembers;
        return true;
    });

    if (loading) {
        return <div className="loading">Loading projects...</div>;
    }

    return (
        <div className="project-list-container">
            <div className="list-header">
                <h2>Available Projects</h2>
                <button 
                    className="btn-secondary"
                    onClick={() => navigate("/dashboard")}
                >
                    Back to Dashboard
                </button>
            </div>

            <div className="filter-bar">
                <button
                    className={filter === "all" ? "active" : ""}
                    onClick={() => setFilter("all")}
                >
                    All ({projects.length})
                </button>
                <button
                    className={filter === "open" ? "active" : ""}
                    onClick={() => setFilter("open")}
                >
                    Open
                </button>
                <button
                    className={filter === "full" ? "active" : ""}
                    onClick={() => setFilter("full")}
                >
                    Full
                </button>
            </div>

            {filteredProjects.length === 0 ? (
                <p className="no-projects">No projects found.</p>
            ) : (
                <div className="project-grid">
                    {filteredProjects.map((project) => {
                        const isFull = project.memberCount >= project.maxMembers;
                        const isCreator = project.creatorId === currentUserId;
                        
                        return (
                            <div key={project.id} className="project-card">
                                <h3>{project.title}</h3>
                                <p className="project-description">{project.description}</p>
                                
                                <div className="project-info">
                                    <span className="info-badge">
                                        👥 {project.memberCount}/{project.maxMembers} members
                                    </span>
                                    <span className={`status-badge ${project.status.toLowerCase()}`}>
                                        {project.status}
                                    </span>
                                    {isFull && <span className="full-badge">FULL</span>}
                                </div>

                                <div className="project-meta">
                                    <small>Created by: {project.creatorName}</small>
                                    <small>
                                        Created: {new Date(project.createdAt).toLocaleDateString()}
                                    </small>
                                </div>

                                {!isCreator && !isFull && (
                                    <button
                                        className="btn-join"
                                        onClick={() => handleJoin(project.id)}
                                    >
                                        Join Project
                                    </button>
                                )}
                                {isCreator && (
                                    <button
                                        className="btn-view"
                                        onClick={() => navigate(`/chat/${project.id}`)}
                                    >
                                        View Project
                                    </button>
                                )}
                                {isFull && !isCreator && (
                                    <button className="btn-disabled" disabled>
                                        Project Full
                                    </button>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}

export default ProjectList;
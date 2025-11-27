import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProject } from "../services/Api";
import "../styles/ProjectForm.css";

function ProjectForm() {
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        maxMembers: 10,
        deadline: ""
    });
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!formData.title || !formData.description) {
            setError("Title and description are required");
            return;
        }

        try {
            const projectData = {
                ...formData,
                maxMembers: parseInt(formData.maxMembers),
                deadline: formData.deadline ? formData.deadline : null
            };

            await createProject(projectData);
            alert("Project created successfully!");
            navigate("/dashboard");
        } catch (err) {
            console.error(err);
            setError("Failed to create project. Please try again.");
        }
    };

    return (
        <div className="form-container">
            <div className="form-card">
                <h2>Create New Project</h2>
                {error && <div className="error-message">{error}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Project Title *</label>
                        <input
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            placeholder="Enter project title"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Description *</label>
                        <textarea
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Describe your project..."
                            rows="5"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Max Members</label>
                        <input
                            type="number"
                            name="maxMembers"
                            value={formData.maxMembers}
                            onChange={handleChange}
                            min="2"
                            max="50"
                        />
                    </div>

                    <div className="form-group">
                        <label>Deadline (Optional)</label>
                        <input
                            type="datetime-local"
                            name="deadline"
                            value={formData.deadline}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-actions">
                        <button type="submit" className="btn-primary">
                            Create Project
                        </button>
                        <button 
                            type="button" 
                            className="btn-secondary"
                            onClick={() => navigate("/dashboard")}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ProjectForm;
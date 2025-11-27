import React, { useState } from "react";
import ProjectForm from "./components/ProjectForm";
import ProjectList from "./components/ProjectList";


function ProjectsPage() {
    const [refreshFlag, setRefreshFlag] = useState(false);

    const refreshProjects = () => setRefreshFlag(prev => !prev);

    return (
        <div>
            <ProjectForm onProjectCreated={refreshProjects} />
            <ProjectList key={refreshFlag} />
        </div>
    );
}

export default ProjectsPage;

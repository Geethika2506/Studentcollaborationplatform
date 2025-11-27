import React, { useEffect, useState } from "react";
import Chat from "./Chat";
import { getProjects } from "../services/Api";

function ProjectDetails({ projectId, userId }) {
  const [project, setProject] = useState(null);

  useEffect(() => {
    async function fetchProject() {
      const projects = await getProjects();
      const selected = projects.find((p) => p.id === projectId);
      setProject(selected);
    }
    fetchProject();
  }, [projectId]);

  if (!project) return <div>Loading project...</div>;

  return (
    <div>
      <h2>{project.title}</h2>
      <p>{project.description}</p>
      <h3>Registered Students:</h3>
      <ul>
        {project.students.map((s) => (
          <li key={s.id}>{s.name}</li>
        ))}
      </ul>

      <h3>Group Chat</h3>
      <Chat projectId={projectId} userId={userId} />
    </div>
  );
}

export default ProjectDetails;

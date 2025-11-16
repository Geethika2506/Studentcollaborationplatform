package service;

import model.Project;

import java.util.ArrayList;

public class ProjectManager {

    private ArrayList<Project> projects;

    public ProjectManager() {
        this.projects = new ArrayList<>();
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public Project findByTitle(String title) {
        for (Project p : projects) {
            if (p.getTitle().equalsIgnoreCase(title)) {
                return p;
            }
        }
        return null; // not found
    }
}

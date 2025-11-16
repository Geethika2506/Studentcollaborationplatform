package service;

import model.Project;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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

    public void exportProjectsToFile(String fileName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {

            if (projects.isEmpty()) {
                out.println("No projects found.");
                return;
            }

            for (Project p : projects) {
                out.println("Title: " + p.getTitle());
                out.println("Description: " + p.getDescription());
                out.println("Creator: " + p.getCreator().getName());
                out.println("------------------------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

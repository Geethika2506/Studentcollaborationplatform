import model.Student;
import model.Project;
import model.ChatRoom;
import model.Message;
import service.ProjectManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Simulate logged-in student (you)
        Student currentStudent = new Student("Juliette", "jjanne.ieu2023@student.ie.edu");

        // Manager to store all projects
        ProjectManager projectManager = new ProjectManager();

        boolean running = true;

        while (running) {
            System.out.println("\n=== Idea Bloom ===");
            System.out.println("Logged in as: " + currentStudent.getName());
            System.out.println("1) Create new project");
            System.out.println("2) List all projects");
            System.out.println("3) Open first project chat (demo)");
            System.out.println("0) Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createProject(scanner, projectManager, currentStudent);
                    break;
                case "2":
                    listProjects(projectManager);
                    break;
                case "3":
                    openChatDemo(scanner, projectManager, currentStudent);
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    private static void createProject(Scanner scanner,
                                      ProjectManager projectManager,
                                      Student creator) {
        System.out.print("Enter project title: ");
        String title = scanner.nextLine();

        System.out.print("Enter project description: ");
        String description = scanner.nextLine();

        Project project = new Project(title, description, creator);
        projectManager.addProject(project);

        System.out.println("Project '" + title + "' created successfully!");
    }

    private static void listProjects(ProjectManager projectManager) {
        System.out.println("\n--- All Projects ---");
        if (projectManager.getProjects().isEmpty()) {
            System.out.println("No projects yet.");
            return;
        }

        for (Project p : projectManager.getProjects()) {
            System.out.println("- " + p.getTitle() +
                    " (creator: " + p.getCreator().getName() + ")");
        }
    }

    // Very simple chat demo: uses the first project in the list
    private static void openChatDemo(Scanner scanner,
                                     ProjectManager projectManager,
                                     Student currentStudent) {
        if (projectManager.getProjects().isEmpty()) {
            System.out.println("No projects available. Create one first.");
            return;
        }

        Project first = projectManager.getProjects().get(0);
        ChatRoom chat = first.getChatRoom();

        System.out.println("\n--- Chat for project: " + first.getTitle() + " ---");

        // Show existing messages
        for (Message m : chat.getMessages()) {
            System.out.println(m.getSender().getName() + ": " + m.getContent());
        }

        System.out.print("Type a message (or just press Enter to go back): ");
        String content = scanner.nextLine();

        if (!content.isBlank()) {
            chat.addMessage(new Message(currentStudent, content));
            System.out.println("Message sent.");
        }
    }
}

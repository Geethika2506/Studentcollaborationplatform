import model.Student;
import model.Instructor;
import model.Admin;
import model.Project;
import model.ChatRoom;
import model.Message;
import service.ProjectManager;

public class Main {
    public static void main(String[] args) {

        // Create users
        Student s = new Student("Juliette", "jjanne.ieu2023@student.ie.edu");
        Instructor i = new Instructor("Andrea", "amontana.ieu2023@student.ie.edu");
        Admin a = new Admin("Admin", "admin@email.com");

        // Create a project manager
        ProjectManager projectManager = new ProjectManager();

        // Create a project
        Project p = new Project(
                "CP Study Group",
                "A group to practice competitive programming problems.",
                s // creator is Juliette
        );

        // Add another member
        Student g = new Student("Geethika", "gkonda.ieu2023@student.ie.edu");
        p.addMember(g);

        // Store the project in the manager
        projectManager.addProject(p);

        // List all projects
        System.out.println("All projects in the system:");
        for (Project proj : projectManager.getProjects()) {
            System.out.println("- " + proj.getTitle() + " (creator: " + proj.getCreator().getName() + ")");
        }
        System.out.println();

        // ---- Simple chat demo ----
        ChatRoom chat = p.getChatRoom();

        chat.addMessage(new Message(s, "Hey team! Welcome to the CP Study Group."));
        chat.addMessage(new Message(g, "Hi! Excited to start solving problems."));
        chat.addMessage(new Message(a, "Reminder: keep the chat friendly and on-topic."));

        System.out.println("Chat for project: " + p.getTitle());
        for (Message m : chat.getMessages()) {
            System.out.println(m.getSender().getName() + ": " + m.getContent());
        }

        // Example of findByTitle
        Project found = projectManager.findByTitle("CP Study Group");
        if (found != null) {
            System.out.println("\nFound project by title: " + found.getTitle());
        }
    }
}

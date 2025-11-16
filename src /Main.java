import model.Student;
import model.Instructor;
import model.Admin;
import model.Project;

public class Main {
    public static void main(String[] args) {

        // Create users
        Student s = new Student("Juliette", "jjanne.ieu2023@student.ie.edu");
        Instructor i = new Instructor("Andrea", "amontana.ieu2023@student.ie.edu");
        Admin a = new Admin("Admin", "admin@email.com");

        // Print user names
        System.out.println(s.getName());
        System.out.println(i.getName());
        System.out.println(a.getName());

        // Create a project
        Project p = new Project(
                "CP Study Group",
                "A group to study java basics.",
                s // creator is Juliette
        );

        // Add another member
        p.addMember(new Student("Geethika", "gkonda.ieu2023@student.ie.edu"));

        // Print project info
        System.out.println("Project: " + p.getTitle());
        System.out.println("Creator: " + p.getCreator().getName());
        System.out.println("Members:");

        for (Student member : p.getMembers()) {
            System.out.println("- " + member.getName());
        }
    }
}

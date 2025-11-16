import model.Project;
import model.Student;
import org.junit.jupiter.api.Test;
import service.ProjectManager;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectManagerTest {

    @Test
    public void testAddProjectIncreasesSize() {
        ProjectManager manager = new ProjectManager();
        Student creator = new Student("Juliette", "juliette@example.com");
        Project p = new Project("Test Project", "Description", creator);

        int before = manager.getProjects().size();
        manager.addProject(p);
        int after = manager.getProjects().size();

        assertEquals(before + 1, after);
    }

    @Test
    public void testFindByTitleReturnsProject() {
        ProjectManager manager = new ProjectManager();
        Student creator = new Student("Juliette", "juliette@example.com");

        Project p = new Project("AI Study Group", "ML basics", creator);
        manager.addProject(p);

        Project found = manager.findByTitle("AI Study Group");

        assertNotNull(found);
        assertEquals("AI Study Group", found.getTitle());
    }

    @Test
    public void testFindByTitleReturnsNullWhenNotFound() {
        ProjectManager manager = new ProjectManager();

        Project result = manager.findByTitle("Nonexistent");

        assertNull(result);
    }
}

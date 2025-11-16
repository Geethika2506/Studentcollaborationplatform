package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Project;
import model.Student;
import service.ProjectManager;

public class IdeaBloomApp extends Application {

    private ProjectManager projectManager = new ProjectManager();
    private ListView<String> projectListView = new ListView<>();

    @Override
    public void start(Stage stage) {
        // temporary "logged in" user
        Student currentStudent = new Student("Juliette", "jjanne.ieu2023@student.ie.edu");

        Label titleLabel = new Label("Idea Bloom - Projects");

        // input fields
        TextField titleField = new TextField();
        titleField.setPromptText("Project title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Project description");

        Button createProjectButton = new Button("Create project");

        createProjectButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();

            if (title.isBlank()) {
                System.out.println("Title cannot be empty");
                return;
            }

            Project p = new Project(title, description, currentStudent);
            projectManager.addProject(p);

            // clear inputs
            titleField.clear();
            descriptionField.clear();

            refreshProjectList();
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(
                titleLabel,
                projectListView,
                titleField,
                descriptionField,
                createProjectButton
        );

        Scene scene = new Scene(root, 400, 350);
        stage.setTitle("Idea Bloom");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshProjectList() {
        projectListView.getItems().clear();
        for (Project p : projectManager.getProjects()) {
            projectListView.getItems().add(
                    p.getTitle() + " (by " + p.getCreator().getName() + ")"
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

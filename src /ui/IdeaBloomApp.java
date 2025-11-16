package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Message;
import model.Project;
import model.Student;
import service.ProjectManager;

public class IdeaBloomApp extends Application {

    private ProjectManager projectManager = new ProjectManager();
    private ListView<Project> projectListView = new ListView<>();
    private Student currentStudent; // logged in user (you)

    @Override
    public void start(Stage stage) {
        // pretend you are logged in
        currentStudent = new Student("Juliette", "jjanne.ieu2023@student.ie.edu");

        Label titleLabel = new Label("Idea Bloom - Projects");

        // input fields for new project
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

            titleField.clear();
            descriptionField.clear();

            refreshProjectList();
        });

        Button openChatButton = new Button("Open chat for selected project");
        openChatButton.setOnAction(e -> {
            Project selected = projectListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                System.out.println("Please select a project first.");
                return;
            }
            openChatWindow(selected);
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(
                titleLabel,
                projectListView,
                titleField,
                descriptionField,
                createProjectButton,
                openChatButton
        );

        Scene scene = new Scene(root, 450, 400);
        stage.setTitle("Idea Bloom");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshProjectList() {
        projectListView.getItems().setAll(projectManager.getProjects());
    }

    private void openChatWindow(Project project) {
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat - " + project.getTitle());

        ListView<String> messagesView = new ListView<>();
        TextField messageField = new TextField();
        messageField.setPromptText("Type a message");
        Button sendButton = new Button("Send");

        // fill messages initially
        refreshMessages(messagesView, project);

        sendButton.setOnAction(e -> {
            String text = messageField.getText();
            if (text.isBlank()) {
                return;
            }

            project.getChatRoom().addMessage(new Message(currentStudent, text));
            messageField.clear();
            refreshMessages(messagesView, project);
        });

        VBox root = new VBox(10, messagesView, messageField, sendButton);
        Scene scene = new Scene(root, 400, 300);
        chatStage.setScene(scene);
        chatStage.show();
    }

    private void refreshMessages(ListView<String> messagesView, Project project) {
        messagesView.getItems().clear();
        for (Message m : project.getChatRoom().getMessages()) {
            messagesView.getItems().add(m.getSender().getName() + ": " + m.getContent());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

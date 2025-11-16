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
import javafx.scene.control.ComboBox;

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

        Button viewMembersButton = new Button("View members of selected project");
        viewMembersButton.setOnAction(e -> {
            Project selected = projectListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                System.out.println("Please select a project first.");
                return;
            }
            openMembersWindow(selected);
        });

        // NEW: export button
        Button exportButton = new Button("Export projects to file");
        exportButton.setOnAction(e -> {
            projectManager.exportProjectsToFile("projects_export.txt");
            System.out.println("Projects exported to projects_export.txt");
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(
                titleLabel,
                projectListView,
                titleField,
                descriptionField,
                createProjectButton,
                openChatButton,
                viewMembersButton,
                exportButton
        );

        Scene scene = new Scene(root, 480, 430);
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

        // list of messages
        ListView<String> messagesView = new ListView<>();

        // choose which member is speaking
        ComboBox<Student> senderBox = new ComboBox<>();
        senderBox.getItems().addAll(project.getMembers());
        // try to select the logged-in student by default
        if (project.getMembers().contains(currentStudent)) {
            senderBox.getSelectionModel().select(currentStudent);
        } else if (!project.getMembers().isEmpty()) {
            senderBox.getSelectionModel().selectFirst();
        }

        senderBox.setPromptText("Select sender");

        // message input
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

            Student sender = senderBox.getSelectionModel().getSelectedItem();
            if (sender == null) {
                // fall back to currentStudent if nothing selected
                sender = currentStudent;
            }

            project.getChatRoom().addMessage(new Message(sender, text));
            messageField.clear();
            refreshMessages(messagesView, project);
        });

        VBox root = new VBox(10, messagesView, senderBox, messageField, sendButton);
        Scene scene = new Scene(root, 400, 320);
        chatStage.setScene(scene);
        chatStage.show();
    }


    private void refreshMessages(ListView<String> messagesView, Project project) {
        messagesView.getItems().clear();
        for (Message m : project.getChatRoom().getMessages()) {
            messagesView.getItems().add(m.getSender().getName() + ": " + m.getContent());
        }
    }

    private void openMembersWindow(Project project) {
        Stage membersStage = new Stage();
        membersStage.setTitle("Members - " + project.getTitle());

        ListView<String> membersView = new ListView<>();
        TextField nameField = new TextField();
        nameField.setPromptText("Student name");

        TextField emailField = new TextField();
        emailField.setPromptText("Student email");

        Button addMemberButton = new Button("Add member");

        // show current members
        refreshMembers(membersView, project);

        addMemberButton.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();

            if (name.isBlank() || email.isBlank()) {
                System.out.println("Name and email cannot be empty");
                return;
            }

            Student newMember = new Student(name, email);
            project.addMember(newMember);
            refreshMembers(membersView, project);

            nameField.clear();
            emailField.clear();
        });

        VBox root = new VBox(10, membersView, nameField, emailField, addMemberButton);
        Scene scene = new Scene(root, 320, 280);
        membersStage.setScene(scene);
        membersStage.show();
    }

    private void refreshMembers(ListView<String> membersView, Project project) {
        membersView.getItems().clear();
        for (Student s : project.getMembers()) {
            membersView.getItems().add(s.getName());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


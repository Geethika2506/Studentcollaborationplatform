package model;

import java.util.ArrayList;

public class Project {

    private String title;
    private String description;
    private Student creator;
    private ArrayList<Student> members;
    private ChatRoom chatRoom;

    public Project(String title, String description, Student creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.members = new ArrayList<>();
        this.members.add(creator);  // creator is automatically a member

        this.chatRoom = new ChatRoom(); // create a chat for this project
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Student getCreator() {
        return creator;
    }

    public ArrayList<Student> getMembers() {
        return members;
    }

    public void addMember(Student s) {
        members.add(s);
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    @Override
    public String toString() {
        return title + " (by " + creator.getName() + ")";
    }
}


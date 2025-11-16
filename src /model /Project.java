package model;

import java.util.ArrayList;

public class Project {

    private String title;
    private String description;
    private Student creator;
    private ArrayList<Student> members;

    public Project(String title, String description, Student creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.members = new ArrayList<>();

        // creator is automatically a member
        this.members.add(creator);
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
}

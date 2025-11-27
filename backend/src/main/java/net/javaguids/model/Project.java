package net.javaguides.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "projects")
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observer_id")
    private User observer;

    @ManyToMany
    @JoinTable(name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_activity", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "last_activity")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private Map<Long, LocalDateTime> memberActivity = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "project_member_roles", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Map<Long, ProjectRole> memberRoles = new HashMap<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.OPEN;

    private int maxMembers = 10;

    public enum ProjectStatus { OPEN, IN_PROGRESS, COMPLETED, CLOSED }
    public enum ProjectRole { ADMIN, OBSERVER, MEMBER }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public User getObserver() { return observer; }
    public void setObserver(User observer) { this.observer = observer; }

    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }

    public Set<Message> getMessages() { return messages; }
    public void setMessages(Set<Message> messages) { this.messages = messages; }

    public Map<Long, LocalDateTime> getMemberActivity() { return memberActivity; }
    public void setMemberActivity(Map<Long, LocalDateTime> memberActivity) { this.memberActivity = memberActivity; }

    public Map<Long, ProjectRole> getMemberRoles() { return memberRoles; }
    public void setMemberRoles(Map<Long, ProjectRole> memberRoles) { this.memberRoles = memberRoles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
}

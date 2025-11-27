package net.javaguides.service;

import net.javaguides.model.Message;
import net.javaguides.model.Project;
import net.javaguides.model.User;
import net.javaguides.repository.MessageRepository;
import net.javaguides.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Project createProject(Project project, User creator) {
        // Ensure managed creator
        User managed = userService.findById(creator.getId()).orElse(creator);
        project.setCreator(managed);
        project.getMembers().add(managed);
        project.setObserver(managed); // default
        project.getMemberActivity().put(managed.getId(), LocalDateTime.now());
        project.getMemberRoles().put(managed.getId(), Project.ProjectRole.ADMIN);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() { return projectRepository.findAll(); }

    public List<Project> getOpenProjects() { return projectRepository.findByStatus(Project.ProjectStatus.OPEN); }

    public Optional<Project> getProjectById(Long id) { return projectRepository.findById(id); }

    public List<Project> getProjectsByCreator(Long creatorId) { return projectRepository.findByCreatorId(creatorId); }

    public List<Project> getProjectsByMember(Long memberId) { return projectRepository.findByMembersId(memberId); }

    @Transactional
    public Project joinProject(Long projectId, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getMembers().size() >= project.getMaxMembers()) throw new RuntimeException("Project full");
        if (project.getMembers().contains(user)) throw new RuntimeException("Already a member");

        User managed = userService.findById(user.getId()).orElse(user);
        project.getMembers().add(managed);
        project.getMemberActivity().put(managed.getId(), LocalDateTime.now());
        project.getMemberRoles().put(managed.getId(), Project.ProjectRole.MEMBER);

        Message joinMessage = new Message();
        joinMessage.setProject(project);
        joinMessage.setSender(managed);
        joinMessage.setContent(managed.getFullName() + " joined the project");
        joinMessage.setType(Message.MessageType.JOIN);
        messageRepository.save(joinMessage);

        return projectRepository.save(project);
    }

    @Transactional
    public Project appointObserver(Long projectId, Long observerId, User creator) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getCreator().getId().equals(creator.getId())) throw new RuntimeException("Only creator can appoint");

        User observer = userService.findById(observerId).orElseThrow(() -> new RuntimeException("Observer not found"));
        if (!project.getMembers().contains(observer)) throw new RuntimeException("Observer must be member");

        project.setObserver(observer);
        project.getMemberRoles().put(observer.getId(), Project.ProjectRole.OBSERVER);
        return projectRepository.save(project);
    }

    @Transactional
    public Project kickMember(Long projectId, Long memberId, User requester) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Project.ProjectRole requesterRole = project.getMemberRoles().get(requester.getId());
        if (requesterRole == null) throw new RuntimeException("You are not a member");
        if (requesterRole != Project.ProjectRole.ADMIN && requesterRole != Project.ProjectRole.OBSERVER) throw new RuntimeException("No permission");

        User member = userService.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Project.ProjectRole memberRole = project.getMemberRoles().get(memberId);
        if (memberRole == Project.ProjectRole.ADMIN) throw new RuntimeException("Cannot kick admin");
        if (requesterRole == Project.ProjectRole.OBSERVER && memberRole == Project.ProjectRole.OBSERVER) throw new RuntimeException("Observers cannot kick other observers");

        project.getMembers().remove(member);
        project.getMemberActivity().remove(memberId);
        project.getMemberRoles().remove(memberId);

        Message kickMessage = new Message();
        kickMessage.setProject(project);
        kickMessage.setSender(requester);
        kickMessage.setContent(member.getFullName() + " was removed from the project");
        kickMessage.setType(Message.MessageType.KICK);
        messageRepository.save(kickMessage);

        return projectRepository.save(project);
    }

    @Transactional
    public void updateMemberActivity(Long projectId, Long userId) {
        Project p = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        p.getMemberActivity().put(userId, LocalDateTime.now());
        projectRepository.save(p);
    }

    public Project updateProject(Project p) { return projectRepository.save(p); }
}

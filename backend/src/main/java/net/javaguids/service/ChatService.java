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

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Transactional
    public Message sendMessage(Long projectId, User sender, String content) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getMembers().contains(sender)) {
            throw new RuntimeException("Not a member of this project");
        }

        Message message = new Message();
        message.setProject(project);
        message.setSender(sender);
        message.setContent(content);
        message.setType(Message.MessageType.CHAT);

        // Update member activity
        projectService.updateMemberActivity(projectId, sender.getId());

        return messageRepository.save(message);
    }

    public List<Message> getProjectMessages(Long projectId) {
        return messageRepository.findByProjectIdOrderBySentAtAsc(projectId);
    }

    public List<Message> getRecentMessages(Long projectId, int limit) {
        return messageRepository.findTop50ByProjectIdOrderBySentAtDesc(projectId);
    }

    @Transactional
    public Message sendSystemMessage(Long projectId, String content) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Message message = new Message();
        message.setProject(project);
        message.setSender(project.getCreator()); // Use creator as system sender
        message.setContent(content);
        message.setType(Message.MessageType.SYSTEM);

        return messageRepository.save(message);
    }
}
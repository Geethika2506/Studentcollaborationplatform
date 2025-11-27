package net.javaguids.service;

import net.javaguides.repository.ProjectRepository;
import net.javaguides.repository.UserRepository;
import net.javaguides.dto.MessageDTO;
import net.javaguides.model.Project;
import net.javaguides.model.User;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatSocketService {

    private final SocketIOServer server;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ChatSocketService(SocketIOServer server, ProjectRepository projectRepository, UserRepository userRepository) {
        this.server = server;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository; // FIXED

        server.addEventListener("message", MessageDTO.class, onMessage());
        server.addEventListener("kick", Long.class, onKick());
        server.start();
    }

    private DataListener<MessageDTO> onMessage() {
        return (client, data, ackSender) -> {
            Project project = projectRepository.findById(data.getProjectId()).orElseThrow();
            User user = userRepository.findById(data.getSenderId()).orElseThrow();

            // Update activity
            project.getMemberActivity().put(user.getId(), LocalDateTime.now());
            projectRepository.save(project);

            // Broadcast message to all connected clients
            server.getBroadcastOperations().sendEvent("message", data);
        };
    }

    private DataListener<Long> onKick() {
        return (client, projectId, ackSender) -> {
            Project project = projectRepository.findById(projectId).orElseThrow();
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

            Set<User> kicked = project.getMembers().stream()
                    .filter(u -> project.getMemberActivity().get(u.getId()) == null
                            || project.getMemberActivity().get(u.getId()).isBefore(threshold))
                    .collect(Collectors.toSet());

            project.getMembers().removeAll(kicked);
            projectRepository.save(project);

            // Notify clients about kicked users
            server.getBroadcastOperations().sendEvent("kicked", kicked.stream().map(User::getUsername).toArray());
        };
    }
}

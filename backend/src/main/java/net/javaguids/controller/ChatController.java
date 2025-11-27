package net.javaguides.controller;
import net.javaguides.service.ChatService;


import net.javaguides.model.Message;
import net.javaguides.model.User;
import net.javaguides.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/projects/{projectId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Long projectId, @RequestBody Map<String, String> payload) {
        try {
            User sender = getCurrentUser();
            String content = payload.get("content");

            Message message = chatService.sendMessage(projectId, sender, content);
            return ResponseEntity.ok(convertToDTO(message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/projects/{projectId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long projectId) {
        try {
            List<Message> messages = chatService.getProjectMessages(projectId);
            List<Map<String, Object>> messageDTOs = messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(messageDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/projects/{projectId}/messages/recent")
    public ResponseEntity<?> getRecentMessages(@PathVariable Long projectId,
                                               @RequestParam(defaultValue = "50") int limit) {
        try {
            List<Message> messages = chatService.getRecentMessages(projectId, limit);
            List<Map<String, Object>> messageDTOs = messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(messageDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> convertToDTO(Message message) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", message.getId());
        dto.put("content", message.getContent());
        dto.put("sentAt", message.getSentAt());
        dto.put("type", message.getType());
        dto.put("senderId", message.getSender().getId());
        dto.put("senderName", message.getSender().getFullName());
        dto.put("senderUsername", message.getSender().getUsername());
        return dto;
    }
}
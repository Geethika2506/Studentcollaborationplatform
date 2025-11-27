package net.javaguides.controller;

import net.javaguides.model.Project;
import net.javaguides.model.User;
import net.javaguides.service.ProjectService;
import net.javaguides.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        try {
            User creator = getCurrentUser();
            Project createdProject = projectService.createProject(project, creator);
            return ResponseEntity.ok(convertToDTO(createdProject));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProjects() {
        try {
            List<Project> projects = projectService.getOpenProjects();
            List<Map<String, Object>> projectDTOs = projects.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projectDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            return ResponseEntity.ok(convertToDetailedDTO(project));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-projects")
    public ResponseEntity<?> getMyProjects() {
        try {
            User user = getCurrentUser();
            List<Project> projects = projectService.getProjectsByMember(user.getId());
            List<Map<String, Object>> projectDTOs = projects.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projectDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinProject(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            Project project = projectService.joinProject(id, user);
            // Make sure the user is added to chat room here if needed
            return ResponseEntity.ok(convertToDTO(project));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/{projectId}/appoint-observer/{observerId}")
    public ResponseEntity<?> appointObserver(@PathVariable Long projectId, @PathVariable Long observerId) {
        try {
            User creator = getCurrentUser();
            Project project = projectService.appointObserver(projectId, observerId, creator);
            return ResponseEntity.ok(convertToDTO(project));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<?> kickMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        try {
            User requester = getCurrentUser();
            Project project = projectService.kickMember(projectId, memberId, requester);
            return ResponseEntity.ok(Map.of("message", "Member kicked successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> convertToDTO(Project project) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", project.getId());
        dto.put("title", project.getTitle());
        dto.put("description", project.getDescription());
        dto.put("creatorName", project.getCreator().getFullName());
        dto.put("creatorId", project.getCreator().getId());
        dto.put("memberCount", project.getMembers().size());
        dto.put("maxMembers", project.getMaxMembers());
        dto.put("status", project.getStatus());
        dto.put("createdAt", project.getCreatedAt());
        dto.put("deadline", project.getDeadline());
        return dto;
    }

    private Map<String, Object> convertToDetailedDTO(Project project) {
        Map<String, Object> dto = convertToDTO(project);

        List<Map<String, Object>> members = project.getMembers().stream()
                .map(member -> {
                    Map<String, Object> memberDTO = new HashMap<>();
                    memberDTO.put("id", member.getId());
                    memberDTO.put("username", member.getUsername());
                    memberDTO.put("fullName", member.getFullName());
                    memberDTO.put("lastActivity", project.getMemberActivity().get(member.getId()));
                    return memberDTO;
                })
                .collect(Collectors.toList());

        dto.put("members", members);
        dto.put("observerId", project.getObserver() != null ? project.getObserver().getId() : null);
        dto.put("observerName", project.getObserver() != null ? project.getObserver().getFullName() : null);

        return dto;
    }
}
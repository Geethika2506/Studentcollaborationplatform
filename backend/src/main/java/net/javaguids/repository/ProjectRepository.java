package net.javaguides.repository;

import net.javaguides.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Spring Data JPA derived query method
    List<Project> findByStatus(Project.ProjectStatus status);

    // Optional: find projects by creator
    List<Project> findByCreatorId(Long creatorId);

    // Optional: find projects by member
    List<Project> findByMembersId(Long memberId);
}

package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.TaskAttachment;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepo extends JpaRepository<TaskAttachment, UUID> {
    List<TaskAttachment> findByTaskId(UUID taskId);
}

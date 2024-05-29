package ru.jilly.trelloclone.service;

import org.springframework.web.multipart.MultipartFile;
import ru.jilly.trelloclone.dto.attachments.AttachmentResponse;
import ru.jilly.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    List<AttachmentResponse> addAttachmentsToTask(UUID taskId, List<MultipartFile> attachments, User currentUser);

    List<AttachmentResponse> getAttachmentsByTaskId(UUID taskId);
    void removeAttachmentFromTaskById(UUID taskId, UUID attachmentId, User currentUser);
}

package ru.jilly.trelloclone.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.jilly.trelloclone.dto.attachments.AttachmentResponse;
import ru.jilly.trelloclone.dto.checklist.AddChecklistRequest;
import ru.jilly.trelloclone.dto.checklist.ChecklistResponse;
import ru.jilly.trelloclone.dto.comment.CommentRequest;
import ru.jilly.trelloclone.dto.comment.CommentResponse;
import ru.jilly.trelloclone.dto.task.TaskFilterRequest;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.dto.task.TaskResponse;
import ru.jilly.trelloclone.dto.task.TaskStatusRequest;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.utils.validation.UpdateValidation;

import java.util.List;
import java.util.UUID;

public interface TaskController {
    /**
     *
     * @return
     */
    ResponseEntity<List<TaskResponse>> getAllTasks();

    /**
     *
     * @param taskFilter
     * @return
     */
    ResponseEntity<List<TaskResponse>> getAllTasksWithFilters(@Valid @RequestBody(required = false) TaskFilterRequest taskFilter);

    /**
     *
     * @param id
     * @return
     */
    ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id);

    /**
     *
     * @param id
     * @param taskDto
     * @param userPrincipal
     * @return
     */
    ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID id,
                                                @Validated({UpdateValidation.class}) @RequestBody TaskRequest taskDto,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     *
     * @param id
     * @param userPrincipal
     * @return
     */
    ResponseEntity<?> deleteTaskById(@PathVariable UUID id,
                                     @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     *
     * @param id
     * @param taskStatusRequest
     * @param userPrincipal
     * @return
     */
    ResponseEntity<TaskResponse> updateTaskStatusById(@PathVariable UUID id,
                                                      @Valid @RequestBody TaskStatusRequest taskStatusRequest,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     *
     * @param id
     * @param commentDto
     * @param userPrincipal
     * @return
     */
    ResponseEntity<CommentResponse> addCommentToTaskById(@PathVariable UUID id,
                                                         @Valid @RequestBody CommentRequest commentDto,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal);
    ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable UUID id);
    ResponseEntity<List<AttachmentResponse>> addAttachmentsToTaskById(@PathVariable UUID id,
                                                                      @Valid @RequestPart List<MultipartFile> attachments,
                                                                      @AuthenticationPrincipal UserPrincipal userPrincipal);
    ResponseEntity<List<AttachmentResponse>> getAttachmentsByTaskId(@PathVariable UUID id);
    ResponseEntity<TaskResponse> addExecutorToTaskById(@PathVariable UUID id,
                                                       @PathVariable UUID executorId,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal);
    ResponseEntity<?> removeExecutorFromTaskById(@PathVariable UUID id,
                                                 @PathVariable UUID executorId,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal);
    ResponseEntity<?> removeAttachmentFromTaskById(@PathVariable UUID id,
                                                   @PathVariable UUID attachmentId,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal);
    ResponseEntity<ChecklistResponse> addChecklistToTaskById(@PathVariable UUID id,
                                                             @Valid @RequestBody AddChecklistRequest checklistDto,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal);
    ResponseEntity<List<ChecklistResponse>> getChecklistsByTaskId(@PathVariable UUID id);
}

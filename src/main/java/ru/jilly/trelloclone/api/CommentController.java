package ru.jilly.trelloclone.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.jilly.trelloclone.dto.checkitem.CheckItemResponse;
import ru.jilly.trelloclone.dto.comment.CommentRequest;
import ru.jilly.trelloclone.dto.comment.CommentResponse;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.security.UserPrincipal;

import java.util.UUID;

public interface CommentController {
    /**
     * Обновляет {@link Task} по {@link UUID}
     * @param id {@link UUID} {@link Task}
     * @param commentDto DTO {@link CommentRequest}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возащает DTO {@link CommentResponse}
     */
    ResponseEntity<CommentResponse> updateTaskById(@PathVariable UUID id,
                                                   @Valid @RequestBody CommentRequest commentDto,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Удаляет {@link Task} по {@link UUID}
     * @param id {@link UUID} {@link Task}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает {@link ResponseEntity}
     */
    ResponseEntity<?>deleteTaskById(@PathVariable UUID id,
                   @AuthenticationPrincipal UserPrincipal userPrincipal);
}

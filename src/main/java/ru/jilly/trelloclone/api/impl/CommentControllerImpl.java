package ru.jilly.trelloclone.api.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jilly.trelloclone.api.CommentController;
import ru.jilly.trelloclone.dto.comment.CommentRequest;
import ru.jilly.trelloclone.dto.comment.CommentResponse;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.service.CommentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comments related actions")
public class CommentControllerImpl implements CommentController {
    private final CommentService commentService;

    @Operation(summary = "Update comment by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<CommentResponse> updateTaskById(@PathVariable UUID id,
                                                          @Valid @RequestBody CommentRequest commentDto,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(commentService.updateCommentById(id, commentDto, userPrincipal.getUser()));
    }

    @Operation(summary = "Delete comment by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<?> deleteTaskById(@PathVariable UUID id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentService.deleteCommentById(id, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

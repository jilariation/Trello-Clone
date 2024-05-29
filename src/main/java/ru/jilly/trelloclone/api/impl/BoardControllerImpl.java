package ru.jilly.trelloclone.api.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.jilly.trelloclone.api.BoardController;
import ru.jilly.trelloclone.dto.board.AddBoardRequest;
import ru.jilly.trelloclone.dto.board.BoardRequest;
import ru.jilly.trelloclone.dto.board.BoardResponse;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.service.BoardService;
import ru.jilly.trelloclone.service.TaskService;
import ru.jilly.trelloclone.dto.task.TaskFilterRequest;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.dto.task.TaskResponse;
import ru.jilly.trelloclone.service.UserService;
import ru.jilly.trelloclone.utils.validation.UpdateValidation;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "Boards related actions")
public class BoardControllerImpl implements BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final TaskService taskService;

    @Operation(summary = "Get all boards")
    @SecurityRequirement(name = "JWT")
    @GetMapping("")
    @Override
    public ResponseEntity<List<BoardResponse>> findAll() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @Operation(summary = "Get Board by id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<BoardResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @Operation(summary = "Add board")
    @SecurityRequirement(name = "JWT")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<BoardResponse> create(@Valid @RequestBody AddBoardRequest boardDto,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(boardService.addBoard(boardDto, userPrincipal.getUser()), HttpStatus.CREATED);
    }

    @Operation(summary = "Update board by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<BoardResponse> updateById(@PathVariable UUID id,
                                                        @Validated({UpdateValidation.class}) @RequestBody BoardRequest boardDto,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(boardService.updateBoardById(id, boardDto, userPrincipal.getUser()));
    }

    @Operation(summary = "Delete board by id if user is creator")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<?> deleteById(@PathVariable UUID id,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boardService.deleteBoardById(id, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Add task to board by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<TaskResponse> addTaskToBoardById(@PathVariable UUID id,
                                                                @Valid @RequestBody TaskRequest taskDto,
                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(taskService.addTaskToBoardById(id, taskDto, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get tasks on board by id")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{id}/tasks")
    @Override
    public ResponseEntity<List<TaskResponse>> getTasksByBoardId(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTasksByBoardId(id));
    }

    @Operation(summary = "Get tasks by board by id with filter")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/tasks/filters")
    @Override
    public ResponseEntity<List<TaskResponse>> getTasksByBoardIdWithFilter(@PathVariable UUID id,
                                                                          @Valid @RequestBody TaskFilterRequest taskFilterRequest) {
        return ResponseEntity.ok(taskService.getTasksByBoardIdWithFilters(id, taskFilterRequest));
    }

    @Operation(summary = "Get board members")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<List<UserResponse>> getBoardMembersById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUsersByBoardId(id),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Add member to board by id")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<BoardResponse> addMemberToBoardById(@PathVariable UUID id,
                                                                    @PathVariable UUID memberId,
                                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(boardService.addMemberToBoardById(id, memberId, userPrincipal.getUser()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Delete member from board by id")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<?> removeMemberFromBoardById(@PathVariable UUID id,
                                                        @PathVariable UUID memberId,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boardService.removeMemberFromBoardById(id, memberId, userPrincipal.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
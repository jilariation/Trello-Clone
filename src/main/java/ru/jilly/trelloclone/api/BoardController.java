package ru.jilly.trelloclone.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.jilly.trelloclone.dto.board.AddBoardRequest;
import ru.jilly.trelloclone.dto.board.BoardRequest;
import ru.jilly.trelloclone.dto.board.BoardResponse;
import ru.jilly.trelloclone.dto.task.TaskFilterRequest;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.dto.task.TaskResponse;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.Board;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface BoardController {
    /**
     * Находит все объекты сущности в БД
     * @return Возвращает {@link List} с DTO {@link BoardResponse}
     */
    ResponseEntity<List<BoardResponse>> findAll();

    /**
     * Находит объект сущности по {@link UUID}
     * @param uuid {@link UUID} сущности
     * @return Возращает DTO {@link BoardResponse}
     */
    ResponseEntity<BoardResponse> findById(UUID uuid);

    /**
     * Создает новую сущность и добавляет ее в базу данных
     * @param createDto DTO {@link } для создания сущности
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает DTO {@link BoardResponse} только что созданной сущности
     */
    ResponseEntity<BoardResponse> create(AddBoardRequest createDto, UserPrincipal userPrincipal);

    /**
     * Обновляет сущность по ее {@link UUID} и DTO {@link BoardRequest}
     * @param uuid {@link UUID} сущности
     * @param entityDto {@link BoardRequest} DTO сущности
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает DTO {@link BoardResponse}
     */
    ResponseEntity<BoardResponse> updateById(UUID uuid, BoardRequest entityDto, UserPrincipal userPrincipal);

    /**
     * Удаляет сущность по ее {@link UUID}
     * @param uuid {@link UUID} сущности
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает {@link ResponseEntity}
     */
    ResponseEntity<?> deleteById(UUID uuid, UserPrincipal userPrincipal);

    /**
     * Добавляет {@link Task} в {@link Board} по его {@link UUID}
     * @param id {@link UUID} доски {@link Board}
     * @param taskDto DTO {@link TaskRequest}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает DTO {@link TaskResponse}
     */
    ResponseEntity<TaskResponse> addTaskToBoardById(@PathVariable UUID id,
                                                    @Valid @RequestBody TaskRequest taskDto,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Находит {@link Task} по {@link UUID} {@link Board}
     * @param id {@link UUID} доски {@link Board}
     * @return Возращает {@link List} с DTO {@link TaskResponse}
     */
    ResponseEntity<List<TaskResponse>> getTasksByBoardId(@PathVariable UUID id);

    /**
     * Находит {@link Task} по {@link UUID} {@link Board} с учетом фильтра {@link TaskFilterRequest}
     * @param id {@link UUID} доски {@link Board}
     * @param taskFilterRequest Фильтр {@link TaskFilterRequest}
     * @return Возращает {@link List} с DTO {@link TaskResponse}
     */
    ResponseEntity<List<TaskResponse>> getTasksByBoardIdWithFilter(@PathVariable UUID id,
                                                                   @Valid @RequestBody TaskFilterRequest taskFilterRequest);

    /**
     * Находит всех участников {@link Board} по {@link UUID}
     * @param id {@link UUID} {@link Board}
     * @return Возращает {@link List} с DTO {@link UserResponse}
     */
    ResponseEntity<List<UserResponse>> getBoardMembersById(@PathVariable UUID id);

    /**
     * Добаляет участника в {@link Board} по его {@link UUID}
     * @param id {@link UUID} {@link Board}
     * @param memberId {@link UUID} участника
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает DTO {@link BoardResponse}
     */
    ResponseEntity<BoardResponse> addMemberToBoardById(@PathVariable UUID id,
                                                       @PathVariable UUID memberId,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Удаляет участника из {@link Board} по {@link UUID} участника
     * @param id {@link UUID} {@link Board}
     * @param memberId {@link UUID} участника
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает {@link ResponseEntity}
     */
    ResponseEntity<?> removeMemberFromBoardById(@PathVariable UUID id,
                                                @PathVariable UUID memberId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal);
}

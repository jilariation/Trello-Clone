package ru.jilly.trelloclone.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;
import ru.jilly.trelloclone.dto.checkitem.CheckItemResponse;
import ru.jilly.trelloclone.dto.checklist.ChecklistRequest;
import ru.jilly.trelloclone.dto.checklist.ChecklistResponse;
import ru.jilly.trelloclone.entity.CheckItem;
import ru.jilly.trelloclone.entity.Checklist;
import ru.jilly.trelloclone.entity.enums.Status;
import ru.jilly.trelloclone.security.UserPrincipal;

import java.util.Set;
import java.util.UUID;

public interface ChecklistController {
    /**
     * Удаляет {@link Checklist} по его {@link UUID}
     * @param id {@link UUID} {@link Checklist}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает {@link ResponseEntity}
     */
    ResponseEntity<?> deleteChecklistById(@PathVariable UUID id,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Обновляет {@link Checklist} по его {@link UUID}
     * @param id {@link UUID} {@link Checklist}
     * @param checklistDto DTO {@link ChecklistRequest}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возащает DTO {@link ChecklistResponse}
     */
    ResponseEntity<ChecklistResponse> updateChecklistById(@PathVariable UUID id,
                                                          @Valid @RequestBody ChecklistRequest checklistDto,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Добавляет {@link Set<CheckItemRequest>} в {@link Checklist}
     * @param id {@link UUID} {@link Checklist}
     * @param checkItemsDto DTO {@link Set<CheckItemRequest>}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возащает DTO {@link ChecklistResponse}
     */
    ResponseEntity<ChecklistResponse> addCheckItemsToChecklist(@PathVariable UUID id,
                                                               @Valid @RequestBody Set<CheckItemRequest> checkItemsDto,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Обновляет {@link CheckItem} в {@link Checklist} по {@link UUID}
     * @param id {@link UUID} {@link Checklist}
     * @param checkItemId {@link UUID} {@link CheckItem}
     * @param checkItemDto DTO {@link CheckItemRequest}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возащает DTO {@link CheckItemResponse}
     */
    ResponseEntity<CheckItemResponse> updateCheckItemOnChecklist(@PathVariable UUID id,
                                                                 @PathVariable UUID checkItemId,
                                                                 @Valid @RequestBody CheckItemRequest checkItemDto,
                                                                 @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Удаляет {@link CheckItem} в {@link Checklist} по {@link UUID}
     * @param id {@link UUID} {@link Checklist}
     * @param checkItemId {@link UUID} {@link CheckItem}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возращает {@link ResponseEntity}
     */
    ResponseEntity<?> deleteCheckItemFromChecklist(@PathVariable UUID id,
                                                   @PathVariable UUID checkItemId,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal);

    /**
     * Обновляет {@link Status} {@link CheckItem}
     * @param id {@link UUID} {@link Checklist}
     * @param checkItemId {@link UUID} {@link CheckItem}
     * @param userPrincipal Пользователь, сформированный для Spring Security
     * @return Возащает DTO {@link CheckItemResponse}
     */
    ResponseEntity<CheckItemResponse> updateCheckItemStatus(@PathVariable UUID id,
                                                            @PathVariable UUID checkItemId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal);
}

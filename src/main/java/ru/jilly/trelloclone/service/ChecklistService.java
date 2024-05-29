package ru.jilly.trelloclone.service;

import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.dto.checklist.ChecklistRequest;
import ru.jilly.trelloclone.dto.checklist.ChecklistResponse;
import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;
import ru.jilly.trelloclone.dto.checklist.AddChecklistRequest;
import ru.jilly.trelloclone.entity.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChecklistService {
    ChecklistResponse addChecklistToTask(UUID taskId, AddChecklistRequest checklistDto, User currentUser);
    List<ChecklistResponse> getChecklistsByTaskId(UUID taskId);
    void deleteChecklistById(UUID id, User currentUser);
    ChecklistResponse updateChecklistById(UUID checklistId, ChecklistRequest checklistDto, User currentUser);

    @Transactional
    ChecklistResponse addCheckItemsToChecklistById(UUID checklistId, Set<CheckItemRequest> checkItemsDto,
                                                   User currentUser);
}

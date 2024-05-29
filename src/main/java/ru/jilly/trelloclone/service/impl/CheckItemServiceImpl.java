package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.repo.CheckItemRepo;
import ru.jilly.trelloclone.service.TaskService;
import ru.jilly.trelloclone.utils.mapper.CheckItemMapper;
import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;
import ru.jilly.trelloclone.dto.checkitem.CheckItemResponse;
import ru.jilly.trelloclone.entity.CheckItem;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.service.CheckItemService;
import ru.jilly.trelloclone.utils.exception.ResourceMismatchException;
import ru.jilly.trelloclone.utils.exception.ResourceNotFoundException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckItemServiceImpl implements CheckItemService {
    private final CheckItemRepo checkItemRepo;
    private final TaskService taskService;
    private final CheckItemMapper checkItemMapper;

    @Override
    @Transactional
    public CheckItemResponse updateCheckItemById(UUID checklistId, UUID checkItemId,
                                                 CheckItemRequest checkItemDto, User currentUser) {
        CheckItem checkItemToUpdate = getExistingCheckItem(checkItemId);
        validateChecklistOwnership(checklistId, checkItemToUpdate);

        Task task = checkItemToUpdate.getChecklist().getTask();
        taskService.validatePermissions(task, currentUser);

        checkItemMapper.mergeDtoIntoEntity(checkItemDto, checkItemToUpdate);
        CheckItem updatedCheckItem = checkItemRepo.save(checkItemToUpdate);

        log.info("Checkitem with id: {} updated by user with id: {}", checkItemId, currentUser.getId());
        return checkItemMapper.toDto(updatedCheckItem);
    }

    @Override
    @Transactional
    public void deleteCheckItemById(UUID checklistId, UUID checkItemId, User currentUser) {
        CheckItem checkItemToDelete = getExistingCheckItem(checkItemId);
        validateChecklistOwnership(checklistId, checkItemToDelete);

        Task task = checkItemToDelete.getChecklist().getTask();
        taskService.validatePermissions(task, currentUser);

        checkItemRepo.deleteById(checkItemId);
        log.info("Checkitem with id: {} deleted by user with id: {}", checkItemId, currentUser.getId());
    }

    @Override
    @Transactional
    public CheckItemResponse updateCheckItemStatus(UUID checklistId, UUID checkItemId, User currentUser) {
        CheckItem checkItemToUpdate = getExistingCheckItem(checkItemId);
        validateChecklistOwnership(checklistId, checkItemToUpdate);

        Task task = checkItemToUpdate.getChecklist().getTask();
        taskService.validatePermissions(task, currentUser);

        boolean currentStatus = checkItemToUpdate.isChecked();
        checkItemToUpdate.setChecked(!currentStatus);

        CheckItem updatedCheckItem = checkItemRepo.save(checkItemToUpdate);
        log.info("Checkitem with id: {} updated by user with id: {}", checkItemId, currentUser.getId());
        return checkItemMapper.toDto(updatedCheckItem);
    }

    private CheckItem getExistingCheckItem(UUID id) {
        return checkItemRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private void validateChecklistOwnership(UUID checklistId, CheckItem checkItem) {
        if (!checkItem.getChecklist().getId().equals(checklistId)) {
            throw new ResourceMismatchException("Checkitem with id: " + checkItem.getId() +
                    " doesn't belong to checklist with id: " + checklistId);
        }
    }
}

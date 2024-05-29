package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;
import ru.jilly.trelloclone.dto.checklist.AddChecklistRequest;
import ru.jilly.trelloclone.dto.checklist.ChecklistRequest;
import ru.jilly.trelloclone.dto.checklist.ChecklistResponse;
import ru.jilly.trelloclone.entity.CheckItem;
import ru.jilly.trelloclone.entity.Checklist;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.repo.ChecklistRepo;
import ru.jilly.trelloclone.service.ChecklistService;
import ru.jilly.trelloclone.service.TaskService;
import ru.jilly.trelloclone.utils.exception.ResourceNotFoundException;
import ru.jilly.trelloclone.utils.mapper.CheckItemMapper;
import ru.jilly.trelloclone.utils.mapper.ChecklistMapper;
import ru.jilly.trelloclone.utils.mapper.TaskMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {
    private final ChecklistRepo checklistRepo;
    private final ChecklistMapper checklistMapper;
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final CheckItemMapper checkItemMapper;

    @Override
    @Transactional
    public ChecklistResponse addChecklistToTask(UUID taskId, AddChecklistRequest checklistDto, User currentUser) {
        Task task = taskMapper.toEntity(taskService.getTaskById(taskId));

        taskService.validatePermissions(task, currentUser);

        Checklist checklist = checklistMapper.toEntity(checklistDto);
        checklist.setTask(task);

        Checklist savedChecklist = checklistRepo.save(checklist);
        log.info("Checklist with id: {} added for task with id: {}", savedChecklist.getId(), taskId);
        return checklistMapper.toDto(savedChecklist);
    }

    @Override
    public List<ChecklistResponse> getChecklistsByTaskId(UUID taskId) {
        return checklistRepo.findByTaskId(taskId).stream()
                .map(checklistMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChecklistById(UUID id, User currentUser) {
        Checklist checklist = getExistingChecklist(id);
        Task task = checklist.getTask();

        taskService.validatePermissions(task, currentUser);

        checklistRepo.deleteById(id);
        log.info("Checklist with id: {} deleted by user with id: {}", id, currentUser.getId());
    }

    @Override
    @Transactional
    public ChecklistResponse updateChecklistById(UUID id, ChecklistRequest checklistDto, User currentUser) {
        Checklist checklist = getExistingChecklist(id);
        Task task = checklist.getTask();

        taskService.validatePermissions(task, currentUser);

        checklistMapper.mergeEntityToDto(checklistDto, checklist);
        Checklist updatedChecklist = checklistRepo.save(checklist);
        log.info("Checklist with id: {} updated by user with id: {}", id, currentUser.getId());
        return checklistMapper.toDto(updatedChecklist);
    }

    @Override
    @Transactional
    public ChecklistResponse addCheckItemsToChecklistById(UUID checklistId, Set<CheckItemRequest> checkItemsDto,
                                                          User currentUser) {
        Checklist checklist = getExistingChecklist(checklistId);
        Task task = checklist.getTask();

        taskService.validatePermissions(task, currentUser);

        Set<CheckItem> checkItemsToAdd = checkItemsDto.stream()
                .map(checkItemMapper::toEntity)
                .peek(c -> c.setChecklist(checklist))
                .collect(Collectors.toSet());

        Set<CheckItem> checkItems = new HashSet<>(checklist.getCheckItems());
        checkItems.addAll(checkItemsToAdd);
        checklist.setCheckItems(checkItems);

        Checklist savedChecklist = checklistRepo.save(checklist);

        log.info("{} check items added for checklist with id: {}", checkItemsToAdd.size(), savedChecklist.getId());
        return checklistMapper.toDto(savedChecklist);

    }

    private Checklist getExistingChecklist(UUID id) {
        return checklistRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }
}

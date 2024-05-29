package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.dto.board.BoardResponse;
import ru.jilly.trelloclone.entity.enums.Status;
import ru.jilly.trelloclone.entity.id.TaskTagKey;
import ru.jilly.trelloclone.repo.TaskRepo;
import ru.jilly.trelloclone.service.BoardService;
import ru.jilly.trelloclone.service.TagService;
import ru.jilly.trelloclone.service.TaskService;
import ru.jilly.trelloclone.service.UserService;
import ru.jilly.trelloclone.utils.mapper.BoardMapper;
import ru.jilly.trelloclone.utils.mapper.TaskMapper;
import ru.jilly.trelloclone.dto.task.TaskFilterRequest;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.dto.task.TaskResponse;
import ru.jilly.trelloclone.dto.task.TaskStatusRequest;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.Board;
import ru.jilly.trelloclone.entity.Tag;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.repo.spec.TaskSpecification;
import ru.sushchenko.trelloclone.service.*;
import ru.jilly.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.jilly.trelloclone.utils.exception.ResourceMismatchException;
import ru.jilly.trelloclone.utils.exception.ResourceNotFoundException;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepo taskRepo;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TagService tagService;
    private final BoardService boardService;
    private final BoardMapper boardMapper;

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepo.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasksWithFilters(TaskFilterRequest taskFilter) {
        List<Task> task;
        if(taskFilter != null) {
            Specification<Task> spec = getSpecificationFromFilter(taskFilter);
            Pageable pageable = getPageableFromFilter(taskFilter);
            task = taskRepo.findAll(spec, pageable).getContent();
        } else {
            task = taskRepo.findAll();
        }
        return task.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        return taskMapper.toDto(getExistingTask(id));
    }

    @Override
    @Transactional
    public TaskResponse addTask(TaskRequest taskDto, User creator) {
        Task task = taskMapper.toEntity(taskDto);
        enrichTask(task);

        task.setCreator(creator);
        task.setExecutors(createExecutorsFromDto(taskDto));
        task.setTags(createTagsFromDto(taskDto, task));

        Task savedTask = taskRepo.save(task);
        log.info("Task with id: {} created", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskById(UUID id, TaskRequest taskDto, User currentUser) {
        Task task = getExistingTask(id);

        validatePermissions(task, currentUser);

        task.setUpdatedAt(new Date());
        task.setClosedAt(updateClosedAt(taskDto.getStatus()));
        task.setExecutors(createExecutorsFromDto(taskDto));
        task.setTags(createTagsFromDto(taskDto, task));

        taskMapper.mergeDtoIntoEntity(taskDto, task);

        Task savedTask = taskRepo.save(task);
        tagService.deleteTagsByTask(savedTask);
        log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse addExecutorToTaskById(UUID id, UUID executorId, User currentUser) {
        Task task = getExistingTask(id);

        validateOwnership(task, currentUser);

        UserResponse executorDto = userService.getUserById(executorId);
        User executor = userMapper.toEntity(executorDto);

        Set<User> executorsToUpdate = new HashSet<>(task.getExecutors());
        executorsToUpdate.add(executor);

        task.setExecutors(executorsToUpdate);
        Task savedTask = taskRepo.save(task);
        log.info("Task with id: {} updated by user with id: {}", savedTask.getId(), currentUser.getId());
        return taskMapper.toDto(savedTask);

    }

    @Override
    @Transactional
    public void removeExecutorFromTaskById(UUID id, UUID executorId, User currentUser) {
        Task task = getExistingTask(id);

        validateOwnership(task, currentUser);

        Set<User> executorsToUpdate = new HashSet<>(task.getExecutors());
        boolean isRemoved = executorsToUpdate.removeIf(e -> e.getId().equals(executorId));

        if(isRemoved) {
            task.setExecutors(executorsToUpdate);
            Task savedTask = taskRepo.save(task);
            log.info("Executor with id: {} removed from task with id: {} by user with id: {}", executorId,
                    savedTask.getId(), currentUser.getId());
        } else {
            throw new ResourceMismatchException("Executor with id: " + executorId +
                    " doesn't belong to task with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteTaskById(UUID id, User currentUser) {
        Task task = getExistingTask(id);

        validateOwnership(task, currentUser);

        taskRepo.deleteById(id);
        log.info("Task with id: {} deleted by user with id: {}", id, currentUser.getId());
    }

    @Override
    public void validatePermissions(Task task, User currentUser) {
        if(!checkIfCreator(task, currentUser) && !checkIfExecutor(task, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), task.getId());
        }
    }

    @Override
    public void validateOwnership(Task task, User currentUser) {
        if(!checkIfCreator(task, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), task.getId());
        }
    }

    private Task getExistingTask(UUID id) {
        return taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatusById(UUID id, TaskStatusRequest taskStatusRequest, User currentUser) {
        Task task = getExistingTask(id);

        validatePermissions(task, currentUser);

        task.setStatus(taskStatusRequest.getStatus());
        task.setUpdatedAt(new Date());
        task.setClosedAt(updateClosedAt(taskStatusRequest.getStatus()));

        Task savedTask = taskRepo.save(task);
        log.info("Status for task with id: {} changed for - {}", savedTask.getId(), taskStatusRequest.getStatus());

        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse addTaskToBoardById(UUID id, TaskRequest taskDto, User user) {
        Board board = boardMapper.toEntity(boardService.getBoardById(id));

        boardService.validatePermissions(board, user);

        Task task = taskMapper.toEntity(taskDto);
        enrichTask(task);

        task.setCreator(user);
        task.setBoard(board);
        task.setExecutors(createExecutorsFromDto(taskDto));
        task.setTags(createTagsFromDto(taskDto, task));

        Task savedTask = taskRepo.save(task);
        log.info("Task with id: {} created", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    public List<TaskResponse> getTasksByBoardId(UUID id) {
        return taskRepo.findByBoardId(id).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByBoardIdWithFilters(UUID boardId, TaskFilterRequest taskFilterRequest) {
        BoardResponse board = boardService.getBoardById(boardId);

        List<Task> task;
        if(taskFilterRequest != null) {
            taskFilterRequest.setBoardId(board.getId());
            Specification<Task> spec = getSpecificationFromFilter(taskFilterRequest);
            Pageable pageable = getPageableFromFilter(taskFilterRequest);
            task = taskRepo.findAll(spec, pageable).getContent();
        } else {
            task = taskRepo.findByBoardId(board.getId());
        }
        return task.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getCreatedTasksByUserId(UUID id) {
        return taskRepo.findByCreatorId(id).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAssignedTasksByUserId(UUID id) {
        return taskRepo.findByExecutorId(id).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    private Pageable getPageableFromFilter(TaskFilterRequest taskFilter) {
        int index = taskFilter.getPageIndex() != null ? taskFilter.getPageIndex() : 0;
        int size = taskFilter.getPageSize() != null ? taskFilter.getPageSize() : 50;
        String sort = taskFilter.getSort() != null ? taskFilter.getSort().getValue() : null;
        if(sort != null) {
            return PageRequest.of(index, size, Sort.by(sort));
        } else {
            return PageRequest.of(index, size);
        }
    }

    private Specification<Task> getSpecificationFromFilter(TaskFilterRequest taskFilter) {
        return TaskSpecification.filterTasks(taskFilter.getPriority(), taskFilter.getStatus(),
                taskFilter.getTags(), taskFilter.getCreatorId(), taskFilter.getEndDate(), taskFilter.getBoardId());
    }

    private Set<Tag> createTagsFromDto(TaskRequest taskDto, Task task) {
        if(taskDto.getTags() != null) {
            return taskDto.getTags().stream()
                    .map(tag -> {
                        TaskTagKey tagKey = new TaskTagKey(task.getId(), tag);
                        Tag newTag = new Tag();
                        newTag.setId(tagKey);
                        newTag.setTask(task);
                        return newTag;
                    })
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    private Date updateClosedAt(Status status) {
        return status == Status.DONE ? new Date() : null;
    }

    private Set<User> createExecutorsFromDto(TaskRequest taskDto) {
        if(taskDto.getExecutorIds() == null || taskDto.getExecutorIds().isEmpty()) {
            return new HashSet<>();
        } else {
            return userService.getUsersByIdIn(taskDto.getExecutorIds()).stream()
                    .map(userMapper::toEntity)
                    .collect(Collectors.toSet());
        }
    }

    private void enrichTask(Task task) {
        task.setCreatedAt(new Date());
        task.setExecutors(new HashSet<>());
        task.setComments(new HashSet<>());
        task.setAttachments(new HashSet<>());
        task.setTags(new HashSet<>());
        task.setChecklists(new HashSet<>());
        task.setCommentsCount(0L);
        task.setAttachmentsCount(0L);
        task.setCheckItemsCount(0L);
        task.setCheckItemsCheckedCount(0L);
    }

    private boolean checkIfCreator(Task task, User currentUser) {
        return Objects.equals(task.getCreator().getId(), currentUser.getId());
    }

    private boolean checkIfExecutor(Task task, User currentUser) {
        return task.getExecutors().stream()
                .anyMatch(executor -> Objects.equals(executor.getId(), currentUser.getId()));
    }
}

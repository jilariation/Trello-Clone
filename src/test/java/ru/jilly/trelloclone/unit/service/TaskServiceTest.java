package ru.jilly.trelloclone.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.entity.enums.Priority;
import ru.jilly.trelloclone.entity.enums.Status;
import ru.jilly.trelloclone.repo.TaskRepo;
import ru.jilly.trelloclone.service.TagService;
import ru.jilly.trelloclone.service.impl.TaskServiceImpl;
import ru.jilly.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.jilly.trelloclone.utils.mapper.TaskMapper;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.dto.task.TaskResponse;
import ru.jilly.trelloclone.service.UserService;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    @Mock
    private TaskRepo taskRepo;

    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TagService tagService;
    @Mock
    private UserMapper userMapper;


    private TaskRequest taskRequest;
    private User creator;
    private User executor;
    private UserResponse executorResponse;
    private Task task;
    private Task savedTask;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock task request dto
        taskRequest = new TaskRequest();
        taskRequest.setName("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setPriority(Priority.HIGH);
        taskRequest.setStatus(Status.TODO);
        taskRequest.setEndDate(new Date());
        Set<UUID> executorIds = new HashSet<>();
        executorIds.add(UUID.randomUUID());
        taskRequest.setExecutorIds(executorIds);
        Set<String> tags = new HashSet<>();
        tags.add("Tag1");
        taskRequest.setTags(tags);

        creator = new User();
        creator.setId(UUID.randomUUID());

        UUID executorId = taskRequest.getExecutorIds().iterator().next();
        executor = new User();
        executor.setId(executorId);
        executorResponse = new UserResponse();
        executorResponse.setId(executorId);

        task = new Task();
        task.setId(UUID.randomUUID());
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setStatus(taskRequest.getStatus());
        task.setEndDate(taskRequest.getEndDate());
        task.setCreator(creator);
        task.setExecutors(Set.of(executor));

        // mock task returned by taskRepo.save
        savedTask = new Task();
        savedTask.setId(task.getId());
        savedTask.setName(taskRequest.getName());
        savedTask.setDescription(taskRequest.getDescription());
        savedTask.setPriority(taskRequest.getPriority());
        savedTask.setStatus(taskRequest.getStatus());
        savedTask.setEndDate(taskRequest.getEndDate());
        savedTask.setCreator(creator);
        savedTask.setExecutors(Set.of(executor));

        // mock task returned by taskMapper.toDto
        taskResponse = new TaskResponse();
        taskResponse.setId(savedTask.getId());
        taskResponse.setName(savedTask.getName());
        taskResponse.setDescription(savedTask.getDescription());
        taskResponse.setPriority(savedTask.getPriority());
        taskResponse.setStatus(savedTask.getStatus());
        taskResponse.setEndDate(savedTask.getEndDate());
        taskResponse.setExecutors(Set.of(executorResponse));
    }

    @Test
    void shouldGetAllTasks() {
        when(taskRepo.findAll()).thenReturn(List.of(task, task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskResponse);

        List<TaskResponse> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals(taskResponse, tasks.get(0));
        assertEquals(taskResponse, tasks.get(1));

        verify(taskRepo).findAll();
        verify(taskMapper, times(2)).toDto(any(Task.class));
    }

    @Test
    void shouldGetTaskById() {
        when(taskRepo.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.getTaskById(UUID.randomUUID());

        assertNotNull(response);
        assertEquals(task.getId(), response.getId());
        assertEquals(task.getName(), response.getName());

        verify(taskRepo, times(1)).findById(any(UUID.class));
        verify(taskMapper, times(1)).toDto(any(Task.class));
    }

    @Test
    void shouldValidatePermissions() {
        assertDoesNotThrow(() -> taskService.validatePermissions(task, creator));
    }

    @Test
    void shouldNotValidatePermissionsAndThrowException() {
        User anotherUser1 = new User();
        anotherUser1.setId(UUID.randomUUID());
        User anotherUser2 = new User();
        anotherUser2.setId(UUID.randomUUID());

        task.setCreator(anotherUser1);
        task.setExecutors(Set.of(anotherUser2));

        assertThrows(NotEnoughPermissionsException.class, () -> taskService.validatePermissions(task, creator));
    }

    @Test
    void shouldValidateOwnership() {
        assertDoesNotThrow(() -> taskService.validateOwnership(task, creator));
    }

    @Test
    void shouldNotValidateOwnershipAndThrowException() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        task.setCreator(anotherUser);

        assertThrows(NotEnoughPermissionsException.class, () -> taskService.validateOwnership(task, creator));
    }

    @Test
    void shouldAddTask() {
        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(taskRepo.save(task)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(taskResponse);

        when(userService.getUsersByIdIn(taskRequest.getExecutorIds())).thenReturn(Set.of(executorResponse));
        when(userMapper.toEntity(executorResponse)).thenReturn(executor);

        TaskResponse result = taskService.addTask(taskRequest, creator);

        verify(taskMapper).toEntity(taskRequest);
        verify(taskRepo).save(task);
        verify(taskMapper).toDto(savedTask);
        verify(userService).getUsersByIdIn(taskRequest.getExecutorIds());
        verify(userMapper).toEntity(executorResponse);

        assertNotNull(result);
        assertEquals(taskResponse.getId(), result.getId());
        assertEquals(taskResponse.getName(), result.getName());
    }

    @Test
    void shouldUpdateTaskById() {
        String updatedName = "updated name";
        taskRequest.setName(updatedName);
        savedTask.setName(updatedName);

        when(taskRepo.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(userService.getUsersByIdIn(taskRequest.getExecutorIds())).thenReturn(Set.of(executorResponse));
        when(taskRepo.save(task)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(taskResponse);
        doNothing().when(tagService).deleteTagsByTask(savedTask);

        TaskResponse result = taskService.updateTaskById(task.getId(), taskRequest, creator);

        assertNotNull(result);
        assertEquals(taskResponse.getId(), result.getId());
        assertEquals(taskResponse.getName(), result.getName());

        verify(taskRepo).findById(task.getId());
        verify(userService).getUsersByIdIn(taskRequest.getExecutorIds());
        verify(taskMapper).mergeDtoIntoEntity(taskRequest, task);
        verify(taskRepo).save(task);
        verify(taskMapper).toDto(savedTask);
        verify(tagService).deleteTagsByTask(savedTask);
    }

    @Test
    void shouldAddExecutorToTaskById() {
        when(taskRepo.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(userService.getUserById(any(UUID.class))).thenReturn(executorResponse);
        when(userMapper.toEntity(executorResponse)).thenReturn(executor);
        when(taskRepo.save(task)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(taskResponse);

        TaskResponse response = taskService.addExecutorToTaskById(task.getId(), executor.getId(), creator);

        assertEquals(1, response.getExecutors().size());
        assertTrue(response.getExecutors().stream().anyMatch(userResponse -> userResponse.getId().equals(executor.getId())));
        verify(taskRepo, times(1)).save(task);
    }

    @Test
    void shouldRemoveExecutorFromTaskById() {
        when(taskRepo.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(taskRepo.save(task)).thenReturn(savedTask);

        taskService.removeExecutorFromTaskById(task.getId(), executor.getId(), creator);

        assertFalse(task.getExecutors().contains(executor));

        verify(taskRepo).findById(task.getId());
        verify(taskRepo).save(task);
    }

    @Test
    void shouldDeleteTaskById() {
        when(taskRepo.findById(any(UUID.class))).thenReturn(Optional.of(task));

        taskService.deleteTaskById(UUID.randomUUID(), creator);

        verify(taskRepo, times(1)).findById(any(UUID.class));
        verify(taskRepo, times(1)).deleteById(any(UUID.class));
    }
}

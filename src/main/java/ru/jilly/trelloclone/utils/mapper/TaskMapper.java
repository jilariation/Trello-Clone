package ru.jilly.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.dto.task.TaskResponse;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    public TaskResponse toDto(Task task) {
        UserResponse creatorDto = userMapper.toDto(task.getCreator());
        Set<UserResponse> executorsDto = task.getExecutors().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());
        Set<String> tagsDto = task.getTags().stream()
                .map(t -> t.getId().getTag())
                .collect(Collectors.toSet());

        TaskResponse taskDto = modelMapper.map(task, TaskResponse.class);
        taskDto.setCreator(creatorDto);
        taskDto.setExecutors(executorsDto);
        taskDto.setTags(tagsDto);

        return taskDto;
    }
    public Task toEntity(TaskResponse taskResponse) {
        User creator = userMapper.toEntity(taskResponse.getCreator());
        Set<User> executors = taskResponse.getExecutors().stream()
                .map(userMapper::toEntity)
                .collect(Collectors.toSet());

        Task task = modelMapper.map(taskResponse, Task.class);
        task.setCreator(creator);
        task.setExecutors(executors);

        return task;
    }
    public Task toEntity(TaskRequest taskDto) {
        return modelMapper.map(taskDto, Task.class);
    }
    public void mergeDtoIntoEntity(TaskRequest taskDto, Task task) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(taskDto, task);
    }
}

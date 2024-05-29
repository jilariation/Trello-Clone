package ru.jilly.trelloclone.service;

import ru.jilly.trelloclone.entity.Task;

public interface TagService {
    void deleteTagsByTask(Task task);
}

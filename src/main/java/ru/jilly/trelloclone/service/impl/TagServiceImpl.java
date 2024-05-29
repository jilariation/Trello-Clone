package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.repo.TagRepo;
import ru.jilly.trelloclone.service.TagService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepo tagRepo;

    @Override
    @Transactional
    public void deleteTagsByTask(Task task) {
        tagRepo.deleteAllByTask(task);
    }
}

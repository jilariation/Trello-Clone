package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.Tag;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.id.TaskTagKey;

@Repository
public interface TagRepo extends JpaRepository<Tag, TaskTagKey> {
    void deleteAllByTask(Task task);
}

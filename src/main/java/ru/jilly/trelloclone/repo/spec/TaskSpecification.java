package ru.jilly.trelloclone.repo.spec;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.enums.Priority;
import ru.jilly.trelloclone.entity.enums.Status;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class TaskSpecification {
    public static Specification<Task> filterTasks(Priority priority, Status status, Set<String> tags,
                                                  UUID creatorId, Date endDate, UUID boardId) {
        Specification<Task> spec = Specification.where(null);
        if(priority != null) {
            spec = spec.and(byPriority(priority));
        }
        if(status != null) {
            spec = spec.and(byStatus(status));
        }
        if(tags != null && !tags.isEmpty()) {
            spec = spec.and(tagsIn(tags));
        }
        if(creatorId != null) {
            spec = spec.and(byCreatorId(creatorId));
        }
        if(boardId != null) {
            spec = spec.and(byCreatorId(creatorId));
        }
        if(endDate != null) {
            spec = spec.and(byEndDate(endDate));
        }
        return spec;
    }

    private static Specification<Task> tagsIn(Set<String> tags) {
        return (((root, query, criteriaBuilder) -> {
            Expression<String> taskTags = root.join("tags", JoinType.LEFT).get("id").get("tag");
            Predicate tagsPredicate = taskTags.in(tags);
            return criteriaBuilder.and(tagsPredicate);
        }));
    }
    private static Specification<Task> byCreatorId(UUID creatorId) {
        return (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("creator").get("id"), creatorId)));
    }
    private static Specification<Task> byBoardId(UUID boardId) {
        return (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("board").get("id"), boardId)));
    }
    private static Specification<Task> byPriority(Priority priority) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority));
    }
    private static Specification<Task> byStatus(Status status) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
    }
    private static Specification<Task> byEndDate(Date endDate) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("endDate"), endDate));
    }
}

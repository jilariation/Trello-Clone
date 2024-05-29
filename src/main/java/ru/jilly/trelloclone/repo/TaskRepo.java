package ru.jilly.trelloclone.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-entity-graph")
    List<Task> findAll();
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-entity-graph")
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-entity-graph")
    Optional<Task> findById(UUID id);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-entity-graph")
    List<Task> findByBoardId(UUID id);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-entity-graph")
    List<Task> findByCreatorId(UUID id);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "task-entity-graph")
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.executors ex WHERE ex.id = :id ")
    List<Task> findByExecutorId(UUID id);
}

package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.Checklist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ChecklistRepo extends JpaRepository<Checklist, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "checklist-entity-graph")
    List<Checklist> findByTaskId(UUID taskId);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "checklist-entity-graph")
    Optional<Checklist> findById(UUID id);

}

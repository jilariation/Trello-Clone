package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.Comment;

import java.util.List;
import java.util.UUID;
@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "comment-entity-graph")
    List<Comment> findByTaskId(UUID taskId);
}

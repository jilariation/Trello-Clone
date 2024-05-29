package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.Board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardRepo extends JpaRepository<Board, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "board-entity-graph")
    List<Board> findAll();
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "board-entity-graph")
    Optional<Board> findById(UUID id);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "board-entity-graph")
    List<Board> findByCreatorId(UUID id);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "board-entity-graph")
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.members m WHERE m.id = :id ")
    List<Board> findByMemberId(UUID id);
}

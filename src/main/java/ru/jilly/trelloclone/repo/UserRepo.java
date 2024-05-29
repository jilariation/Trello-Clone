package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jilly.trelloclone.entity.User;

import java.util.*;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Set<User> findByIdIn(Set<UUID> ids);
    @EntityGraph(attributePaths = {"assignedTasks"})
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.assignedTasks at WHERE at.endDate >= :startOfDay AND at.endDate < :endOfDay " +
            "AND at.status != 'DONE'")
    Set<User> findUsersWithTasksDueToday(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.memberedBoards mb WHERE mb.id = :id ")
    List<User> findMembersOfBoardByBoardId(UUID id);
}

package ru.jilly.trelloclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jilly.trelloclone.entity.CheckItem;

import java.util.UUID;

public interface CheckItemRepo extends JpaRepository<CheckItem, UUID> {
}

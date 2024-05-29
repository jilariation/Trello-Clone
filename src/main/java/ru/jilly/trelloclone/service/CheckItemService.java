package ru.jilly.trelloclone.service;

import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;
import ru.jilly.trelloclone.dto.checkitem.CheckItemResponse;
import ru.jilly.trelloclone.entity.User;

import java.util.UUID;

public interface CheckItemService {
    CheckItemResponse updateCheckItemById(UUID checklistId, UUID checkItemId,
                                          CheckItemRequest checkItemDto, User currentUser);
    void deleteCheckItemById(UUID checklistId, UUID checkItemId, User currentUser);

    CheckItemResponse updateCheckItemStatus(UUID id, UUID checkItemId, User user);
}

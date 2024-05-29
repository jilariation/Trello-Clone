package ru.jilly.trelloclone.service;

import ru.jilly.trelloclone.entity.User;

import java.util.concurrent.CompletableFuture;

public interface KafkaMessagingService {
    void sendSignUpUser(User user);

    CompletableFuture<Void> sendUserHotTask(User user);
}

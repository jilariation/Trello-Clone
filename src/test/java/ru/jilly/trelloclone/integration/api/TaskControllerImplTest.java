package ru.jilly.trelloclone.integration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.jilly.trelloclone.entity.Task;
import ru.jilly.trelloclone.entity.enums.Priority;
import ru.jilly.trelloclone.entity.enums.Status;
import ru.jilly.trelloclone.repo.BoardRepo;
import ru.jilly.trelloclone.repo.TaskRepo;
import ru.jilly.trelloclone.repo.UserRepo;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.dto.task.TaskRequest;
import ru.jilly.trelloclone.entity.Board;
import ru.jilly.trelloclone.entity.User;

import java.util.Date;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class TaskControllerImplTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BoardRepo boardRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserPrincipal userPrincipal;
    private Task task;
    private Board board;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        taskRepo.deleteAll();
        boardRepo.deleteAll();

        user = new User();
        user.setEmail("user@mail.com");
        user.setUsername("user");
        user.setPassword("password");

        User savedUser = userRepo.save(user);
        user.setId(savedUser.getId());

        userPrincipal = new UserPrincipal(user);

        board = new Board();
        board.setName("board name");

        Board savedBoard = boardRepo.save(board);
        board.setId(savedBoard.getId());

        task = new Task();
        task.setName("testTask");
        task.setCreator(user);
        task.setExecutors(Set.of(user));
        task.setStatus(Status.TODO);
        task.setPriority(Priority.LOW);
        task.setCreatedAt(new Date());
        task.setBoard(board);

        Task savedTask = taskRepo.save(task);
        task.setId(savedTask.getId());
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    public void shouldGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(task.getId().toString()));
    }

    @Test
    public void shouldGetTaskById() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{taskId}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId().toString()))
                .andExpect(jsonPath("$.creator.id").value(user.getId().toString()));
    }

    @Test
    public void shouldUpdateTask() throws Exception {
        TaskRequest taskRequest = TaskRequest.builder()
                .name("updatedTask")
                .executorIds(Set.of(user.getId()))
                .priority(Priority.HIGH)
                .build();

        mockMvc.perform(put("/api/v1/tasks/{taskId}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new TestingAuthenticationToken(userPrincipal, null, "ROLE_USER")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedTask"))
                .andExpect(jsonPath("$.creator.id").value(userPrincipal.getUser().getId().toString()))
                .andExpect(jsonPath("$.executors[0].id").value(user.getId().toString()))
                .andExpect(jsonPath("$.priority").value(Priority.HIGH.toString()));
    }

    @Test
    public void shouldDeleteTaskById() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{taskId}", task.getId())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new TestingAuthenticationToken(userPrincipal, null, "ROLE_USER")
                        )))
                .andExpect(status().isNoContent());
    }
}

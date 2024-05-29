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
import ru.jilly.trelloclone.repo.UserRepo;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.dto.user.UserRequest;
import ru.jilly.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class UserControllerImplTest {
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
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();

        user = new User();
        user.setEmail("user@mail.com");
        user.setUsername("user");
        user.setPassword("password");

        User savedUser = userRepo.save(user);
        user.setId(savedUser.getId());

        userPrincipal = new UserPrincipal(user);

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
    public void shouldGetAllUsers() throws Exception {
        User user1 = User.builder().id(UUID.randomUUID()).email("user1@mail.ru").username("user1").password("user1").build();
        User user2 = User.builder().id(UUID.randomUUID()).email("user2@mail.ru").username("user2").password("user2").build();

        userRepo.saveAll(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value(user1.getUsername()))
                .andExpect(jsonPath("$[1].username").value(user2.getUsername()));
    }

    @Test
    public void shouldGetUserInfoById() throws Exception {
        User user1 = User.builder().id(UUID.randomUUID()).email("user1@mail.ru").username("user1").password("user1").build();

        User savedUser = userRepo.save(user1);

        mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.username").value(user1.getUsername()));
    }

    @Test
    public void shouldUpdateUserById() throws Exception {
        UserRequest userDto = UserRequest.builder().name("newName").lastName("newLastName").build();

        mockMvc.perform(patch("/api/v1/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                    .with(SecurityMockMvcRequestPostProcessors.authentication(
                            new TestingAuthenticationToken(userPrincipal, null, "ROLE_USER")
                    )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.last_name").value("newLastName"));
    }
}

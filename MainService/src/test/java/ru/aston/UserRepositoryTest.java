package ru.aston;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    NotificationProducer notificationProducer;

    UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, notificationProducer);
    }

    @Test
    public void connectionCheck() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    public void findById_saveAndFindUser_ShouldBeCreated() {
        Users user = createUser();
        CreateUserRequest createUserRequest = new CreateUserRequest(user.getAge(),
                user.getEmail(),
                user.getName());
        int id = userService.addUser(createUserRequest).getId();
        UserResponse foundUser = userService.findUser(id);

        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getName(), foundUser.getName());
    }

    @Test
    public void updateUser__shouldBeDifferent() {
        Users user = createUser();
        CreateUserRequest createUserRequest = new CreateUserRequest(user.getAge(),
                user.getEmail(),
                user.getName());
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("NeOleg",
                user.getEmail(),
                user.getAge());

        int id = userService.addUser(createUserRequest).getId();
        UserResponse notUpdatedUser = userService.findUser(id);
        id = userService.updateUser(id, updateUserRequest).getId();
        UserResponse updatedUser = userService.findUser(id);

        assertEquals(notUpdatedUser.getId(), updatedUser.getId());
        assertNotEquals(notUpdatedUser.getName(), updatedUser.getName());
        assertEquals(notUpdatedUser.getEmail(), updatedUser.getEmail());
        assertEquals(notUpdatedUser.getAge(), updatedUser.getAge());
    }

    @Test
    @Rollback
    public void removeUser() {
        Users user = createUser();
        CreateUserRequest createUserRequest = new CreateUserRequest(user.getAge(),
                user.getEmail(),
                user.getName());

        int id = userService.addUser(createUserRequest).getId();
        userService.removeUser(id);

        UserResponse removedUser = userService.findUser(id);

        assertNull(removedUser);
    }

    private Users createUser() {
        return new Users(
                "Oleg",
                "Oleg@mail.ru",
                12,
                LocalDateTime.now()
        );
    }
}

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityManagerDaoTest {

    public PostgreSQLContainer<?> container;

    UserEntityManagerDao userEntityManagerDao;

    HibernateFactory hibernateFactory;

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        container = new PostgreSQLContainer<>("postgres:16-alpine");
        container.start();
        userEntityManagerDao = new UserEntityManagerDao();
        hibernateFactory = new HibernateFactory();

        String jdbcUrl = container.getJdbcUrl();
        String username = container.getUsername();
        String password = container.getPassword();

        Map<String, String> configOverrides = new HashMap<>();
        configOverrides.put("hibernate.connection.username", username);
        configOverrides.put("hibernate.connection.password", password);
        configOverrides.put("hibernate.connection.url", jdbcUrl);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateWithoutSpring", configOverrides);
        EntityManager entityManager = emf.createEntityManager();

        Field field = ReflectionUtils.findFields(HibernateFactory.class, f -> f.getName().equals("entityManager"),
                        ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP)
                .getFirst();
        field.setAccessible(true);
        field.set(hibernateFactory, entityManager);
    }

    @Test
    public void findUser_findCreatedUser_equals() {
        Users expectedUser = createUser();

        Users foundedUser = userEntityManagerDao.findById(expectedUser.getId());

        assertEquals(expectedUser, foundedUser);
    }

    @Test
    public void addUser_createSameUser_notEquals() {
        Users firstUser = createUser();
        Users secondUser = createUser();

        assertNotEquals(firstUser, secondUser);
    }

    @Test
    public void updateUser_updateExistingUser_notEquals() {
        Users expectedUser = createUser();

        Users updatingUser = new Users();
        updatingUser.setId(expectedUser.getId());
        updatingUser.setName(expectedUser.getName());
        updatingUser.setAge(expectedUser.getAge() + 1);
        updatingUser.setEmail("12345@mail.ru");

        Users updatedUser = userEntityManagerDao.update(updatingUser);

        assertNotEquals(expectedUser, updatedUser);
        assertEquals(expectedUser.getId(), updatedUser.getId());
        assertEquals(expectedUser.getName(), updatedUser.getName());
        assertNotEquals(expectedUser.getAge(), updatedUser.getAge());
        assertNotEquals(expectedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void updateUser_updateNotExistingUser_throwsException() {
        Users updatingUser = new Users();
        updatingUser.setId(1);
        updatingUser.setAge(12);
        updatingUser.setName("Oleg");
        updatingUser.setEmail("oleg@mail.ru");

        assertThrows(OptimisticLockException.class, () -> userEntityManagerDao.update(updatingUser));
    }

    @Test
    public void removeUser_removeExistingUser_Null() {
        Users expectedUser = createUser();

        userEntityManagerDao.delete(expectedUser);
        Users foundedUser = userEntityManagerDao.findById(expectedUser.getId());

        assertNull(foundedUser);
    }

    public Users createUser() {
        Users user = new Users();
        user.setAge(12);
        user.setName("Oleg");
        user.setEmail("oleg@mail.ru");

        return userEntityManagerDao.save(user);
    }
}

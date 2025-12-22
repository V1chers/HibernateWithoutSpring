import jakarta.persistence.OptimisticLockException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class UserSessionDaoTest {

    public PostgreSQLContainer<?> container;

    UserSessionDao userSessionDao;

    HibernateFactory hibernateFactory;

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        container = new PostgreSQLContainer<>("postgres:16-alpine");
        container.start();
        userSessionDao = new UserSessionDao();
        hibernateFactory = new HibernateFactory();

        String jdbcUrl = container.getJdbcUrl();
        String username = container.getUsername();
        String password = container.getPassword();

        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Users.class);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.connection.url", jdbcUrl);
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());

        Field field = ReflectionUtils.findFields(HibernateFactory.class, f -> f.getName().equals("sessionFactory"),
                        ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP)
                .getFirst();
        field.setAccessible(true);
        field.set(hibernateFactory, sessionFactory);
    }

    @Test
    public void findUser_findCreatedUser_equals() {
        Users expectedUser = createUser();

        Users foundedUser = userSessionDao.findById(expectedUser.getId());

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

        Users updatedUser = userSessionDao.update(updatingUser);

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

        assertThrows(OptimisticLockException.class, () -> userSessionDao.update(updatingUser));
    }

    @Test
    public void removeUser_removeExistingUser_Null() {
        Users expectedUser = createUser();

        userSessionDao.delete(expectedUser);
        Users foundedUser = userSessionDao.findById(expectedUser.getId());

        assertNull(foundedUser);
    }

    public Users createUser() {
        Users user = new Users();
        user.setAge(12);
        user.setName("Oleg");
        user.setEmail("oleg@mail.ru");

        return userSessionDao.save(user);
    }
}

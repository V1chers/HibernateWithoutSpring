import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    UserDao userDao;

    Service service;

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        userDao = mock(UserDao.class);
        service = new Service(UserDaoType.of(1));

        Field field = ReflectionUtils.findFields(Service.class, f -> f.getName().equals("userDao"),
                ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP)
                .getFirst();

        field.setAccessible(true);
        field.set(service, userDao);
    }

    @Test
    public void findUser_AnyNumber_Equals() {
        when(userDao.findById(anyInt())).thenReturn(new Users());

        Users user = service.findUser(1);

        assertEquals(new Users(), user);
    }

    @Test
    public void addUser_AnyUser_Equals() {
        Users returnedUser = new Users();
        returnedUser.setId(1);

        when(userDao.save(any(Users.class)))
                .thenReturn(returnedUser);

        Users user = service.addUser(new Users());

        assertEquals(returnedUser, user);
    }

    @Test
    public void updateUser_findByIdNull_EqualsNull() {
        when(userDao.findById(anyInt())).thenReturn(null);

        Users users = new Users();
        users.setId(1);

        Users user = service.updateUser(users);

        assertNull(user);
    }

    @Test
    public void updateUser_updateExistingUser_Equals() {
        Users oldUser = new Users();
        oldUser.setId(1);
        oldUser.setName("Олег");
        oldUser.setEmail("oleg@mail.ru");
        oldUser.setAge(12);

        Users userToChange = new Users();
        oldUser.setEmail("12345@mail.ru");
        oldUser.setAge(13);

        Users expectedUser = new Users();
        expectedUser.setId(1);
        expectedUser.setName("Олег");
        expectedUser.setEmail("12345@mail.ru");
        expectedUser.setAge(13);

        when(userDao.update(expectedUser)).thenReturn(expectedUser);
        when(userDao.findById(anyInt())).thenReturn(oldUser);

        Users returnedUser = service.updateUser(userToChange);

        assertEquals(expectedUser, returnedUser);
    }
}

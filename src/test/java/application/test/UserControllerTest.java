package application.test;

import application.UserController;
import application.UserMapper;
import application.UserService;
import application.Users;
import application.dto.CreateUserRequest;
import application.dto.UpdateUserRequest;
import application.dto.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService service;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void createUser_pathAndMethodTest_shouldReturnUser() throws Exception {
        Users user = createUser();
        CreateUserRequest createUserRequest = new CreateUserRequest(user.getAge(),
                user.getEmail(),
                user.getName());
        UserResponse userResponse = UserMapper.toUserResponse(user);

        given(service.addUser(eq(createUserRequest))).willReturn(userResponse);

        String userJson = mapper.writeValueAsString(createUserRequest);

        mvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(user.getId())))
                .andExpect(jsonPath("name", is(user.getName())))
                .andExpect(jsonPath("email", is(user.getEmail())))
                .andExpect(jsonPath("age", is(user.getAge())));

    }

    @Test
    public void createUser_testValidation_400() throws Exception {
        Users user = createUser();
        CreateUserRequest createUserRequest = new CreateUserRequest(user.getAge(),
                user.getEmail(),
                "  ");
        String userJson = mapper.writeValueAsString(createUserRequest);

        mvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findById_pathAndMethodTest_shouldReturnUser() throws Exception {
        Users user = createUser();
        UserResponse userResponse = UserMapper.toUserResponse(user);

        given(service.findUser(anyInt())).willReturn(userResponse);

        mvc.perform(get("/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(user.getId())))
                .andExpect(jsonPath("name", is(user.getName())))
                .andExpect(jsonPath("email", is(user.getEmail())))
                .andExpect(jsonPath("age", is(user.getAge())));
    }

    @Test
    public void updateUser_pathAndMethodTest_shouldReturnUser() throws Exception {
        Users user = createUser();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(user.getName(),
                user.getEmail(),
                user.getAge());
        UserResponse userResponse = UserMapper.toUserResponse(user);

        given(service.updateUser(anyInt(), eq(updateUserRequest))).willReturn(userResponse);

        String userJson = mapper.writeValueAsString(updateUserRequest);

        mvc.perform(patch("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(user.getId())))
                .andExpect(jsonPath("name", is(user.getName())))
                .andExpect(jsonPath("email", is(user.getEmail())))
                .andExpect(jsonPath("age", is(user.getAge())));
    }

    @Test
    public void deleteUser_pathAndMethodTest_200() throws Exception {
        mvc.perform(delete("/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

package ru.aston;

import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UserResponse;

import java.time.LocalDateTime;

public class UserMapper {

    public static Users fromCreateUserRequest(CreateUserRequest createUserRequest) {
        return new Users(
                createUserRequest.getName(),
                createUserRequest.getEmail(),
                createUserRequest.getAge(),
                LocalDateTime.now()
        );
    }

    public static UserResponse toUserResponse(Users users) {
        if (users == null) {
            return null;
        }

        return new UserResponse(
                users.getId(),
                users.getName(),
                users.getEmail(),
                users.getAge(),
                users.getCreatedAt()
        );
    }
}

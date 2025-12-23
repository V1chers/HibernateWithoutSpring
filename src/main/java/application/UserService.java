package application;

import application.dto.CreateUserRequest;
import application.dto.UpdateUserRequest;
import application.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse addUser(CreateUserRequest createUserRequest) {
        Users user = UserMapper.fromCreateUserRequest(createUserRequest);
        user = userRepository.save(user);
        return UserMapper.toUserResponse(user);
    }

    public UserResponse findUser(Integer id) {
        Users user = userRepository.findById(id).orElse(null);
        return UserMapper.toUserResponse(user);
    }

    public UserResponse updateUser(Integer id, UpdateUserRequest user) {
        Users updatingUsers = userRepository.findById(id).orElse(null);

        if (updatingUsers == null) {
            System.out.println("Пользователь с таким id не существует");
            return null;
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            updatingUsers.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updatingUsers.setEmail(user.getEmail());
        }
        if (user.getAge() != null) {
            updatingUsers.setAge(user.getAge());
        }

        updatingUsers = userRepository.save(updatingUsers);

        return UserMapper.toUserResponse(updatingUsers);
    }

    public void removeUser(Integer id) {
        userRepository.deleteById(id);
    }
}

package ru.aston;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.event.NotificationEvent;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final NotificationProducer notificationProducer;

    @Autowired
    public UserService(UserRepository userRepository, NotificationProducer notificationProducer) {
        this.userRepository = userRepository;
        this.notificationProducer = notificationProducer;
    }

    public UserResponse addUser(CreateUserRequest createUserRequest) {
        Users user = UserMapper.fromCreateUserRequest(createUserRequest);
        user = userRepository.save(user);
        notificationProducer.sendNotification(user, NotificationEvent.Action.CREATED);
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
        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            notificationProducer.sendNotification(user.get(), NotificationEvent.Action.DELETED);
        }

        userRepository.deleteById(id);
    }
}

package ru.aston;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Tag(name = "user")
    @PostMapping(produces = {"application/hal+json"})
    @SimpleControllerResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь создан",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))})})
    @CircuitBreaker(name = "UserController", fallbackMethod = "generalFallback")
    public UserResponse createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры пользователя", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserRequest.class)))
            @Valid @RequestBody CreateUserRequest createUserRequest) {
        UserResponse userResponse = userService.addUser(createUserRequest);

        return createLinks(userResponse);
    }

    @Tag(name = "user")
    @GetMapping(value = "/{id}", produces = {"application/hal+json"})
    @SimpleControllerResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))})})
    @CircuitBreaker(name = "UserController", fallbackMethod = "generalFallback")
    public UserResponse findById(@PathVariable Integer id) {
        UserResponse userResponse = userService.findUser(id);

        return createLinks(userResponse);
    }

    @Tag(name = "user")
    @PatchMapping(value = "/{id}", produces = {"application/hal+json"})
    @SimpleControllerResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя обновлены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))})})
    @CircuitBreaker(name = "UserController", fallbackMethod = "generalFallback")
    public UserResponse updateUser(@PathVariable Integer id,
                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                           description = "Параметры пользователя", required = true,
                                           content = @Content(mediaType = "application/json",
                                                   schema = @Schema(implementation = UpdateUserRequest.class),
                                                   examples = @ExampleObject(value = "{ \"name\": \"name\", \"email\": \"email@mail.ru\", \"age\": 30 }")))
                                   @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.updateUser(id, updateUserRequest);

        return createLinks(userResponse);
    }

    @Tag(name = "user")
    @DeleteMapping("/{id}")
    @SimpleControllerResponses
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Пользователь удален",
            content = @Content)})
    @CircuitBreaker(name = "UserController", fallbackMethod = "generalFallback")
    public void deleteUser(@PathVariable Integer id) {
        userService.removeUser(id);
    }

    private UserResponse createLinks(UserResponse userResponse) {
        Link selfLink = linkTo(UserController.class).slash(userResponse.getId()).withSelfRel();
        userResponse.add(selfLink);

        Link createUserLink = linkTo(UserController.class).withRel("createUser");
        userResponse.add(createUserLink);

        return userResponse;
    }

    public UserResponse generalFallback(Throwable throwable) {
        throw new CircuitBreakerException("Endpoint currently unavailable");
    }
}

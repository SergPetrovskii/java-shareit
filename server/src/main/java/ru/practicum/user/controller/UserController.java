package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = userService.createUser(UserMapper.toUser(userDto));
        log.info("POST request for create user");
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User user = userService.updateUser(userId, UserMapper.toUser(userDto));
        log.info("PATCH request for update user with id = " + userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("GET request for find all users");
        return userService.findAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto findUser(@PathVariable Long userId) {
        log.info("GET request for find user with id = " + userId);
        return UserMapper.toUserDto(userService.findUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("DELETE request for delete user with id = " + userId);
    }
}
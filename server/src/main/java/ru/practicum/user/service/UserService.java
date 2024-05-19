package ru.practicum.user.service;

import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User findUserById(Long id);

    User updateUser(Long userId, User user);

    void deleteUser(Long id);

    List<User> findAllUsers();
}

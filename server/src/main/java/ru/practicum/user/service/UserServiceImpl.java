package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public User updateUser(Long userId, User user) {
        User userOld = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String email = user.getEmail();
        String name = user.getName();

        if (name != null && !name.isBlank()) {
            userOld.setName(name);
        }
        if (email != null && !email.isBlank()) {
            userOld.setEmail(email);
        }
        return userOld;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
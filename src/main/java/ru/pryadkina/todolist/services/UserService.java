package ru.pryadkina.todolist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pryadkina.todolist.models.User;
import ru.pryadkina.todolist.repositories.UserRepository;
import ru.pryadkina.todolist.util.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll(Boolean onlyActive) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return null;
        } else if (onlyActive != null && onlyActive) {
            return users.stream().filter(user -> user.getStatus() != 3).collect(Collectors.toList());
        }
        return users;
    }

    public User findOne(int id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(int id, User user) {
        User userForUpdate = this.findOne(id);
        userForUpdate.setName(user.getName());
        userForUpdate.setStatus(user.getStatus());
        return userRepository.save(userForUpdate);
    }

    @Transactional
    public void delete(int id) {
        User user = this.findOne(id);
        user.setStatus(3);
        userRepository.save(user);
    }
}

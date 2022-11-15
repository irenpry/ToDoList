package ru.pryadkina.todolist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pryadkina.todolist.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
}

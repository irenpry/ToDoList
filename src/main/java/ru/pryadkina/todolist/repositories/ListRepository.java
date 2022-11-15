package ru.pryadkina.todolist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pryadkina.todolist.models.Task;
import ru.pryadkina.todolist.models.TasksList;
import ru.pryadkina.todolist.models.User;

import java.util.List;

@Repository
public interface ListRepository extends JpaRepository<TasksList, Integer> {

    List<TasksList> findByOwners(User owner);

    List<TasksList> findByParticipants(User participant);

}

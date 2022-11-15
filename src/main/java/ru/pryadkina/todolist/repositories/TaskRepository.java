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
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByAuthor(User author);

    List<Task> findByExecutor(User executor);

    List<Task> findByParentList(TasksList tasksList);

    @Modifying
    @Query("select t from Task t where parentList is null")
    List<Task> findTasksWithDefaultList();

}

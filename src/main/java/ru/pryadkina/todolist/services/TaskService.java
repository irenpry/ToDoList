package ru.pryadkina.todolist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pryadkina.todolist.models.Task;
import ru.pryadkina.todolist.models.TasksList;
import ru.pryadkina.todolist.models.User;
import ru.pryadkina.todolist.repositories.TaskRepository;
import ru.pryadkina.todolist.util.TaskNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ListService listService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, ListService listService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.listService = listService;
    }

    public List<Task> findAll(Integer author, Integer executor, Integer listId, Boolean defaultList, Set<Integer> statuses) {

        List<Task> tasks = new ArrayList<>();
        List<Task> result;

        if (author != null) {
            User user = userService.findOne(author);
            tasks.addAll(taskRepository.findByAuthor(user));
        }

        if (executor != null) {
            User user = userService.findOne(executor);
            tasks.addAll(taskRepository.findByExecutor(user));
        }

        result = tasks;

        if (defaultList != null) {
            if (tasks.size() == 0) {
                tasks.addAll(taskRepository.findTasksWithDefaultList());
            } else {
                if (tasks.stream().filter(task -> task.getParentList() == null).count() > 0) {
                    result = tasks.stream().filter(task -> task.getParentList() == null).distinct().collect(Collectors.toList());
                }
            }
        }

        if (listId != null) {
            TasksList tasksList = listService.findOne(listId);
            if (tasks.size() == 0) {
                tasks.addAll(taskRepository.findByParentList(tasksList));
            } else {
                if (tasks.stream().filter(task -> task.getParentList() != null && task.getParentList().getId() == listId).count() > 0) {
                    result = tasks.stream().filter(task -> task.getParentList() != null && task.getParentList().getId() == listId).distinct().collect(Collectors.toList());
                }
            }
        }


        if (statuses != null) {
            return result.stream().filter(task -> statuses.contains(task.getStatus())).collect(Collectors.toList());
        }

        return result.stream().distinct().collect(Collectors.toList());
    }

    public Task findOne(int id) {
        return taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
    }

    @Transactional
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateFull(int id, Task task) {
        Task taskForUpdate = this.findOne(id);
        taskForUpdate.setText(task.getText());
        taskForUpdate.setStatus(task.getStatus());
        taskForUpdate.setExecutor(task.getExecutor());
        taskForUpdate.setParentList(task.getParentList());
        taskForUpdate.setImportant(task.getImportant());
        return taskRepository.save(taskForUpdate);
    }

    @Transactional
    public Task updatePart(int id, HashMap<String, Optional> params) {
        Task taskForUpdate = this.findOne(id);
        for (String s : params.keySet()) {
            switch (s) {
                case "text":
                    taskForUpdate.setText(String.valueOf((params.get(s).get())));
                    break;
                case "status":
                    taskForUpdate.setStatus(Integer.valueOf(String.valueOf(params.get(s).get())));
                    break;
                case "executor":
                    if (params.get(s).isPresent()) {
                        taskForUpdate.setExecutor(userService.findOne(Integer.valueOf(params.get(s).get().toString())));
                    } else {
                        taskForUpdate.setExecutor(null);
                    }
                    break;
                case "author":
                    taskForUpdate.setAuthor(userService.findOne(Integer.valueOf(String.valueOf(params.get(s).get()))));
                    break;
                case "parentList":
                    if (params.get(s).isPresent()) {
                        taskForUpdate.setParentList(listService.findOne(Integer.valueOf(String.valueOf(params.get(s).get()))));
                    } else {
                        taskForUpdate.setExecutor(null);
                    }
                    break;
                case "important":
                    taskForUpdate.setImportant(Boolean.valueOf(params.get(s).get().toString()));
                    break;
            }
        }
        return taskRepository.save(taskForUpdate);
    }

    @Transactional
    public void delete(int id) {
        Task task = this.findOne(id);
        task.setStatus(-1);
        taskRepository.save(task);
    }
}

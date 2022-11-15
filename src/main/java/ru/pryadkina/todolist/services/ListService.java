package ru.pryadkina.todolist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pryadkina.todolist.models.TasksList;
import ru.pryadkina.todolist.models.User;
import ru.pryadkina.todolist.repositories.ListRepository;
import ru.pryadkina.todolist.util.ListNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ListService {

    private final ListRepository listRepository;
    private final UserService userService;

    @Autowired
    public ListService(ListRepository listRepository, UserService userService) {
        this.listRepository = listRepository;
        this.userService = userService;
    }

    public List<TasksList> findAll(Boolean onlyActive, Integer userId) {
        List<TasksList> lists = new ArrayList<>();
        if (userId != null) {
            User user = userService.findOne(userId);
            lists.addAll(listRepository.findByOwners(user));
            lists.addAll(listRepository.findByParticipants(user));
        }
        if (!lists.isEmpty() && onlyActive != null && onlyActive) {
            return lists.stream().filter(user -> user.getStatus() != 3).collect(Collectors.toList());
        }

        return lists;
    }

    public TasksList findOne(int id) {
        return listRepository.findById(id).orElseThrow(ListNotFoundException::new);
    }

    @Transactional
    public TasksList create(TasksList tasksList) {
        return listRepository.save(tasksList);
    }

    @Transactional
    public TasksList updateFull(int id, TasksList tasksList) {
        TasksList tasksListForUpdate = this.findOne(id);
        tasksListForUpdate.setName(tasksList.getName());
        tasksListForUpdate.setOwners(tasksList.getOwners());
        tasksListForUpdate.setParticipants(tasksList.getParticipants());
        return listRepository.save(tasksListForUpdate);
    }

//    @Transactional
//    public TasksList updatePart(int id, TasksList tasksList) {
//        TasksList tasksListForUpdate = this.findOne(id);
//        if (tasksList.getName() != null) tasksListForUpdate.setName(tasksList.getName());
//        if (tasksList.getOwners() != null) tasksListForUpdate.setOwners(tasksList.getOwners());
//        if (tasksList.getParticipants() != null) tasksListForUpdate.setParticipants(tasksList.getParticipants());
//        return listRepository.save(tasksListForUpdate);
//    }

    @Transactional
    public TasksList updatePartNew(int id, HashMap<String, Optional> params) {
        TasksList tasksListForUpdate = this.findOne(id);
        for (String s : params.keySet()) {
            switch (s) {
                case "name":
                    tasksListForUpdate.setName((String) (params.get(s).get()));
                    break;
                case "status":
                    tasksListForUpdate.setStatus((Integer) params.get(s).get());
                    break;
                case "owners":
                    if (params.get(s).isPresent()) {
                        List<Integer> userIds = (List<Integer>) params.get(s).get();
                        List<User> users = new ArrayList<>();
                        for (Integer userId : userIds) {
                            users.add(userService.findOne(userId));
                        }
                        tasksListForUpdate.setOwners(users);
                    } else {
                        tasksListForUpdate.setOwners(null);
                    }
                    break;
                case "participants":
                    if (params.get(s).isPresent()) {
                        List<Integer> userIds = (List<Integer>) params.get(s).get();
                        List<User> users = new ArrayList<>();
                        for (Integer userId : userIds) {
                            users.add(userService.findOne(userId));
                        }
                        tasksListForUpdate.setParticipants(users);
                    } else {
                        tasksListForUpdate.setParticipants(null);
                    }
                    break;
            }
        }
        return listRepository.save(tasksListForUpdate);
    }

    @Transactional
    public void delete(int id) {
        TasksList tasksList = this.findOne(id);
        tasksList.setStatus(3);
        listRepository.save(tasksList);
    }


}

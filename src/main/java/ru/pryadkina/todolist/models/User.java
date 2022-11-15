package ru.pryadkina.todolist.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "status")
    @NotNull
    private Integer status;

    @ManyToMany(mappedBy = "owners")
    private List<TasksList> tasksListsOwner;

    @ManyToMany(mappedBy = "participants")
    private List<TasksList> tasksListsParticipant;

    @OneToMany(mappedBy = "author")
    private List<Task> createdTasks;

    @OneToMany(mappedBy = "executor")
    private List<Task> tasksToExecute;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<TasksList> getTasksListsOwner() {
        return tasksListsOwner;
    }

    public void setTasksListsOwner(List<TasksList> tasksListsOwner) {
        this.tasksListsOwner = tasksListsOwner;
    }

    public List<TasksList> getTasksListsParticipant() {
        return tasksListsParticipant;
    }

    public void setTasksListsParticipant(List<TasksList> tasksListsParticipant) {
        this.tasksListsParticipant = tasksListsParticipant;
    }

    public List<Task> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<Task> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public List<Task> getTasksToExecute() {
        return tasksToExecute;
    }

    public void setTasksToExecute(List<Task> tasksToExecute) {
        this.tasksToExecute = tasksToExecute;
    }
}

package ru.pryadkina.todolist.models;

import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks_list")
public class TasksList {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "status")
    private int status;

    @ManyToMany
    @SQLInsert(sql = "insert into user_list (list_id, user_id, type) values (?, ?, 1)")
    @JoinTable(name = "user_list", joinColumns = @JoinColumn(name = "list_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @WhereJoinTable(clause = "type = 1")
    private List<User> owners;

    @ManyToMany
    @SQLInsert(sql = "insert into user_list (list_id, user_id, type) values (?, ?, 2)")
    @JoinTable(name = "user_list", joinColumns = @JoinColumn(name = "list_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @WhereJoinTable(clause = "type = 2")
    private List<User> participants;

    @OneToMany(mappedBy = "parentList")
    private List<Task> tasks;

    public TasksList() {}

    public TasksList(String name) {
        this.name = name;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<User> getOwners() {
        return owners;
    }

    public void setOwners(List<User> owners) {
        this.owners = owners;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void addOwner(User owner) {
        if (this.getOwners() == null) {
            this.setOwners(new ArrayList<User>());
        }
        this.getOwners().add(owner);
    }

    public void addParticipant(User participant) {
        if (this.getParticipants() == null) {
            this.setParticipants(new ArrayList<User>());
        }
        this.getParticipants().add(participant);
    }

}

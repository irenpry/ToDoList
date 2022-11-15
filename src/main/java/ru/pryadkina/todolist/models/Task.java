package ru.pryadkina.todolist.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text")
    @NotNull
    private String text;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "status")
    @NotNull
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "executor", referencedColumnName = "id")
    private User executor;

    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    @NotNull
    private User author;

    @ManyToOne
    @JoinColumn(name = "list_id", referencedColumnName = "id")
    private TasksList parentList;

    public Task() {}

    public Task(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getImportant() {
        return isImportant;
    }

    public void setImportant(Boolean isImportant) {
        isImportant = isImportant;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public TasksList getParentList() {
        return parentList;
    }

    public void setParentList(TasksList parentList) {
        this.parentList = parentList;
    }
}

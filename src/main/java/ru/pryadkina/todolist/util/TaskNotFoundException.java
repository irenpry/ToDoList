package ru.pryadkina.todolist.util;

public class TaskNotFoundException extends RuntimeException {

    private final String message = "Задача по указанным условиям не найдена";

    @Override
    public String getMessage() {
        return message;
    }

}

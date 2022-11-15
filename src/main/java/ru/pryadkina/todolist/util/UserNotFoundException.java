package ru.pryadkina.todolist.util;

public class UserNotFoundException extends RuntimeException {

    private final String message = "Пользователь по указанным условиям не найден";

    @Override
    public String getMessage() {
        return message;
    }
}

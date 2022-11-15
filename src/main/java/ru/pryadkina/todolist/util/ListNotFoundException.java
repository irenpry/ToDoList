package ru.pryadkina.todolist.util;

public class ListNotFoundException extends RuntimeException {

    private final String message = "Лист по указанным условиям не найден";

    @Override
    public String getMessage() {
        return message;
    }

}

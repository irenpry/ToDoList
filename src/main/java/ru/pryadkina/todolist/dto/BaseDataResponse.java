package ru.pryadkina.todolist.dto;

public class BaseDataResponse<T> extends BaseResponse{

    private T data;

    public BaseDataResponse(String message, int code, T data) {
        super(message, code);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

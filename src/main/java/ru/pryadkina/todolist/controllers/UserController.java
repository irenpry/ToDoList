package ru.pryadkina.todolist.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.pryadkina.todolist.dto.BaseDataResponse;
import ru.pryadkina.todolist.dto.BaseResponse;
import ru.pryadkina.todolist.dto.UserDTO;
import ru.pryadkina.todolist.models.User;
import ru.pryadkina.todolist.services.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public BaseResponse findAll(@RequestParam(value = "onlyActive", required = false) Boolean onlyActive) {
        List<UserDTO> users = new ArrayList<>();
        for (User user : userService.findAll(onlyActive)) {
            users.add(convertToUserDTO(user));
        }
        return new BaseDataResponse<List<UserDTO>>("ok", 0, users);
    }

    @GetMapping("/{id}")
    public BaseResponse findOne(@PathVariable("id") int id) {
        return new BaseDataResponse<UserDTO>("ok", 0,
                convertToUserDTO(userService.findOne(id)));
    }

    @PostMapping
    public BaseResponse create(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                message = message.append(error.getField())
                        .append(" ").
                        append(error.getDefaultMessage())
                        .append("; ");
            }
            return new BaseResponse(message.toString(), 1);
        }
        return new BaseDataResponse<UserDTO>("Пользователь успешно создан", 0,
                convertToUserDTO(userService.save(convertToUser(userDTO))));
    }

    @PutMapping("/{id}")
    public BaseResponse update(@PathVariable("id") int id,
                               @RequestBody @Valid UserDTO userDTO,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                message = message.append(error.getField())
                        .append(" ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            return new BaseResponse(message.toString(), 1);
        }
        return new BaseDataResponse<UserDTO>("Пользователь успешно обновлен", 0,
                convertToUserDTO(userService.update(id, convertToUser(userDTO))));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id") int id) {
        userService.delete(id);
        return new BaseResponse("Пользователь успешно удален", 0);
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO,User.class);
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user,UserDTO.class);
    }

    @ExceptionHandler
    private BaseResponse handleException(Exception e) {
        return new BaseResponse(e.getMessage(), 1);
    }

}

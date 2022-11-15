package ru.pryadkina.todolist.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.pryadkina.todolist.dto.BaseDataResponse;
import ru.pryadkina.todolist.dto.BaseResponse;
import ru.pryadkina.todolist.dto.TasksListDTO;
import ru.pryadkina.todolist.models.TasksList;
import ru.pryadkina.todolist.services.ListService;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;
    private final ModelMapper modelMapper;
    static final HashSet<String> patchParams = new HashSet<>();

    {
        patchParams.add("name");
        patchParams.add("status");
        patchParams.add("owners");
        patchParams.add("participants");
    }

    @Autowired
    public ListController(ListService listService, ModelMapper modelMapper) {
        this.listService = listService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public BaseResponse findAll(@RequestParam(value = "onlyActive", required = false) Boolean onlyActive,
                                @RequestParam(value = "userId", required = true) Integer userId) {
        List<TasksListDTO> lists = new ArrayList<>();
        for (TasksList tasksList : listService.findAll(onlyActive, userId)) {
            lists.add(convertToTasksListDTO(tasksList));
        }
        return new BaseDataResponse<List<TasksListDTO>>("ok", 0, lists);
    }

    @GetMapping("/{id}")
    public BaseResponse findOne(@PathVariable("id") int id) {
        return new BaseDataResponse<TasksListDTO>("ok", 0,
                convertToTasksListDTO(listService.findOne(id)));
    }

    @PostMapping()
    public BaseResponse create(@RequestBody @Valid TasksListDTO tasksListDTO,
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
        return new BaseDataResponse<TasksListDTO>("Лист успешно создан", 0,
                convertToTasksListDTO(listService.create(convertToTasksList(tasksListDTO))));
    }

    @PutMapping("/{id}")
    public BaseResponse updateFull(@PathVariable("id") int id,
                                   @RequestBody @Valid TasksListDTO tasksListDTO,
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
        return new BaseDataResponse<TasksListDTO>("Лист успешно обновлен", 0,
                convertToTasksListDTO(listService.updateFull(id, convertToTasksList(tasksListDTO))));
    }

    @PatchMapping("/{id}")
    public BaseResponse updatePart(@PathVariable("id") int id,
                                   @RequestBody HashMap<String, Optional> bodyParams) {
        HashMap<String, Optional> validParam = new HashMap<>();
        if (!bodyParams.isEmpty()) {
            for (String s : bodyParams.keySet()) {
                if (patchParams.contains(s)) {
                    validParam.put(s, bodyParams.get(s));
                }
            }
        }
        if (validParam.isEmpty()) {
            return new BaseResponse("Укажите хотя бы один из обязательных параметров: " + patchParams, 0);
        }
        return new BaseDataResponse<TasksListDTO>("Лист успешно обновлен", 0,
                convertToTasksListDTO(listService.updatePartNew(id, validParam)));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id") int id) {
        listService.delete(id);
        return new BaseResponse("Лист успешно удален", 0);
    }

    private TasksList convertToTasksList(TasksListDTO tasksListDTO) {
        return modelMapper.map(tasksListDTO, TasksList.class);
    }

    private TasksListDTO convertToTasksListDTO(TasksList tasksList) {
        return modelMapper.map(tasksList, TasksListDTO.class);
    }

    @ExceptionHandler
    private BaseResponse handleException(Exception e) {
        return new BaseResponse(e.getMessage(), 1);
    }
}

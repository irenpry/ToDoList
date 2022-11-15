package ru.pryadkina.todolist.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.pryadkina.todolist.dto.*;
import ru.pryadkina.todolist.models.Task;
import ru.pryadkina.todolist.services.TaskService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ModelMapper modelMapper;
    static final HashSet<String> patchParams = new HashSet<>();

    {
        patchParams.add("text");
        patchParams.add("status");
        patchParams.add("executor");
        patchParams.add("author");
        patchParams.add("parentList");
        patchParams.add("important");
    }

    @Autowired
    public TaskController(TaskService taskService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public BaseResponse findAll(@RequestParam(value = "author", required = false) Integer author,
                                @RequestParam(value = "executor", required = false) Integer executor,
                                @RequestParam(value = "listId", required = false) Integer listId,
                                @RequestParam(value = "defaultList", required = false) Boolean defaultList,
                                @RequestParam(value = "statuses", required = false) String statuses) {
        if (author == null && executor == null && listId == null && defaultList == null) {
            return new BaseResponse("Укажите параметр для поиска", 1);
        }

        if (listId != null && defaultList != null && defaultList) {
            return new BaseResponse("Укажите или listId, или defaultList=true", 1);
        }

        if (statuses != null) {
            String[] statusesArray = statuses.split(",");
            Set<Integer> statusesSet = new HashSet<>();
            for (String s : statusesArray) {
                try {
                    statusesSet.add(Integer.valueOf(s));
                } catch (RuntimeException e) {
                    // пропускаем такое значение
                }
            }
            return new BaseDataResponse<List<TaskDTO>>("ok", 0,
                    convertToTaskDTOList(taskService.findAll(author, executor, listId, defaultList, statusesSet)));
        }

        return new BaseDataResponse<List<TaskDTO>>("ok", 0,
                convertToTaskDTOList(taskService.findAll(author, executor, listId, defaultList, null)));
    }

    @GetMapping("/{id}")
    public BaseResponse findOne(@PathVariable("id") int id) {
        return new BaseDataResponse<TaskDTO>("ok", 0,
                convertToTaskDTO(taskService.findOne(id)));
    }

    @PostMapping
    public BaseResponse create(@RequestBody @Valid TaskDTO taskDTO,
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
        return new BaseDataResponse<TaskDTO>("Задача успешно создана", 0,
                convertToTaskDTO(taskService.create(convertToTask(taskDTO))));
    }

    @PutMapping("/{id}")
    public BaseResponse updateFull(@PathVariable("id") int id,
                                   @RequestBody @Valid TaskDTO taskDTO,
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
        return new BaseDataResponse<TaskDTO>("Задача успешно обновлена", 0,
                convertToTaskDTO(taskService.updateFull(id, convertToTask(taskDTO))));
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
        return new BaseDataResponse<TaskDTO>("Задача успешно обновлена", 0,
                convertToTaskDTO(taskService.updatePart(id, validParam)));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id") int id) {
        taskService.delete(id);
        return new BaseResponse("Задача успешно удалена", 0);
    }

    private Task convertToTask(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    private TaskDTO convertToTaskDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }

    private List<TaskDTO> convertToTaskDTOList(List<Task> tasks) {
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (Task task : tasks) {
            taskDTOs.add(convertToTaskDTO(task));
        }
        return taskDTOs;
    }

    @ExceptionHandler
    private BaseResponse handleException(Exception e) {
        return new BaseResponse(e.getMessage(), 1);
    }

}

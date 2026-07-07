package com.task_management.Task.Management.controllers;

import com.task_management.Task.Management.dtos.RegisterTaskRequest;
import com.task_management.Task.Management.dtos.TaskDto;
import com.task_management.Task.Management.dtos.TaskPageResponse;
import com.task_management.Task.Management.dtos.UpdateTaskRequest;
import com.task_management.Task.Management.enums.TaskStatus;
import com.task_management.Task.Management.mappers.TaskMapper;
import com.task_management.Task.Management.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@RestController
@RequestMapping("/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody RegisterTaskRequest request , UriComponentsBuilder uriBuilder){
        var response  = taskService.createTask(request);
        var uri       = uriBuilder.path("/task/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/all-tasks")
    public ResponseEntity<?> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate,desc") String sort,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) LocalDate DueDate,
            @RequestParam(required = false) Long userId
    ){
        var leavesPage = taskService.getAllTasks(page , size , sort , status ,DueDate , userId);
        var data = leavesPage.stream().map(taskMapper::toDto).toList();
        var response = new TaskPageResponse<>(
                data,
                leavesPage.getNumber(),
                leavesPage.getSize(),
                leavesPage.getTotalPages(),
                leavesPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Iterable<TaskDto> getMyTasks(){
        return taskService.getMyTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getLeaveById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskRequest request , @PathVariable(name = "id") Long id){
        return ResponseEntity.ok(taskService.updateTask(request , id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable(name = "id") Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


}

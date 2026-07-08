package com.task_management.Task.Management.services;

import com.task_management.Task.Management.dtos.RegisterTaskRequest;
import com.task_management.Task.Management.dtos.TaskDto;
import com.task_management.Task.Management.dtos.TaskUpdateEventDto;
import com.task_management.Task.Management.dtos.UpdateTaskRequest;
import com.task_management.Task.Management.entities.Task;
import com.task_management.Task.Management.enums.Role;
import com.task_management.Task.Management.enums.TaskStatus;
import com.task_management.Task.Management.exceptions.NotEnoughPrevilagesException;
import com.task_management.Task.Management.exceptions.TaskDueDayCantBeforeTodayException;
import com.task_management.Task.Management.exceptions.TaskNotFoundException;
import com.task_management.Task.Management.exceptions.UserNotFound;
import com.task_management.Task.Management.mappers.TaskMapper;
import com.task_management.Task.Management.repositories.TaskRepository;
import com.task_management.Task.Management.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskEventPublisher taskEventPublisher;
    public TaskDto createTask(RegisterTaskRequest request){
        Task task = new Task();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        if(userId == null){
            throw new UserNotFound();
        }
        var user = userRepository.findById(userId).orElse(null);
        task.setOwner(user);
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.PENDING);
        task.setTitle(request.getTitle());
        if(request.getDueDate().isBefore(LocalDate.now())){
            throw new TaskDueDayCantBeforeTodayException("Due-Date must not be older date");
        }
        task.setDueDate(request.getDueDate());
        var saved = taskRepository.save(task);
        taskEventPublisher.send(new TaskUpdateEventDto("CREATED" , taskMapper.toDto(saved)));
        return taskMapper.toDto(saved);
    }

    public Page<Task> getAllTasks(
            int page,
            int size,
            String sort,
            TaskStatus status,
            LocalDate DueDate,
            Long userId
    ){
        PageRequest pageRequest = PageRequest.of(page , size);
        Page<Task> taskList = taskRepository.findWithFilters(status , DueDate , pageRequest , userId);
        return taskList;
    }

    public Page<Task> getMyTasks(
            int page,
            int size,
            String sort,
            TaskStatus status,
            LocalDate DueDate,
            Long userId
    ){
        PageRequest pageRequest = PageRequest.of(page , size);
        Page<Task> tasks = taskRepository.findMyTasksWithFilter(status , DueDate , pageRequest , userId);
        return tasks;
    }

    public TaskDto getTaskById(Long id){
        var task = taskRepository.findById(id).orElse(null);
        if(task == null){
            throw new TaskNotFoundException("Task Not Found");
        }
        return taskMapper.toDto(task);
    }

    public TaskDto updateTask(UpdateTaskRequest request , Long id){
        var task = taskRepository.findById(id).orElse(null);
        if(task == null){
            throw new TaskNotFoundException("Task Not Found");
        }
        if(!isUserIsOwnerOrAdmin(id)){
            throw new NotEnoughPrevilagesException("You Don't Have Enough Privileges");
        }

        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setTitle(request.getTitle());
        if(request.getDueDate().isBefore(LocalDate.now())){
            throw new TaskDueDayCantBeforeTodayException("Due-Date must not be older date");
        }
        task.setDueDate(request.getDueDate());
        var saved = taskRepository.save(task);
        taskEventPublisher.send(new TaskUpdateEventDto("UPDATED" , taskMapper.toDto(saved)));
        return taskMapper.toDto(saved);

    }

    public void deleteTask(Long id){
        var task = taskRepository.findById(id).orElse(null);
        if(!isUserIsOwnerOrAdmin(id)){
            throw new NotEnoughPrevilagesException("You Don't Have Enough Privileges");
        }
        if(task == null){
            throw new TaskNotFoundException("Task Not Found");
        }
        var taskDto = taskMapper.toDto(task);
        taskRepository.delete(task);
        taskEventPublisher.send(new TaskUpdateEventDto("DELETED" , taskDto));
    }

    private boolean isUserIsOwnerOrAdmin(Long taskId){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if(Role.ADMIN.name().equals(role.replace("ROLE_", ""))) {
            return true;
        }
            return taskRepository.findIfOwner(userId , taskId);
    }
}

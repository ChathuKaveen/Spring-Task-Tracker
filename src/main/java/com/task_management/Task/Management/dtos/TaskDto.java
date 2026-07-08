package com.task_management.Task.Management.dtos;

import com.task_management.Task.Management.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status = TaskStatus.PENDING;
    private LocalDate dueDate;
    private TaskResponseDto owner;

}

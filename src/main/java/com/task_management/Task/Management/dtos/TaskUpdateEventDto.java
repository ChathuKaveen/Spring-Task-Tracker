package com.task_management.Task.Management.dtos;

import com.task_management.Task.Management.entities.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskUpdateEventDto {
    private String action; // "CREATED", "UPDATED", "DELETED"
    private TaskDto task;
}

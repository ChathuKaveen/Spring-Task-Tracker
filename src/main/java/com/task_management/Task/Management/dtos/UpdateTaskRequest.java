package com.task_management.Task.Management.dtos;

import com.task_management.Task.Management.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    @NotNull(message = "Status is required (PENDING , COMPLETED , IN_PROGRESS)")
    private TaskStatus status = TaskStatus.PENDING;
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;
}

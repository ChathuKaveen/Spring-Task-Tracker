package com.task_management.Task.Management.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "E-Mail is required")
    @Email(message = "Must be valid email")
    private String email;
}

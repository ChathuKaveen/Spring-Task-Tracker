package com.task_management.Task.Management.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Add a email")
    @Email(message = "Must be valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

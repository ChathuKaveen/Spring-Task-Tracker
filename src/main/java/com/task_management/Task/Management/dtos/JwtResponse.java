package com.task_management.Task.Management.dtos;

import com.task_management.Task.Management.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private Role role;
    private String email;
    private String token;
}

package com.task_management.Task.Management.controllers;

import com.task_management.Task.Management.dtos.RegisterUserRequest;
import com.task_management.Task.Management.dtos.UpdateUserRequest;
import com.task_management.Task.Management.dtos.UserDto;
import com.task_management.Task.Management.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("v1/api/user")
@CrossOrigin(origins = "http://localhost:5174")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUserRequest request , UriComponentsBuilder uriBuilder){
        var userDto = userService.createUser(request);
        var uri = uriBuilder.path("/user/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest request , @PathVariable(name="id") Long id){
        return ResponseEntity.ok(userService.updateUser(request , id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name="id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}

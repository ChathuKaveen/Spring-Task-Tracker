package com.task_management.Task.Management.services;

import com.task_management.Task.Management.dtos.RegisterUserRequest;
import com.task_management.Task.Management.dtos.UpdateUserRequest;
import com.task_management.Task.Management.dtos.UserDto;
import com.task_management.Task.Management.enums.Role;
import com.task_management.Task.Management.exceptions.NotEnoughPrevilagesException;
import com.task_management.Task.Management.exceptions.UserAlreadyExisist;
import com.task_management.Task.Management.exceptions.UserNotFound;
import com.task_management.Task.Management.mappers.UserMapper;
import com.task_management.Task.Management.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(RegisterUserRequest request){
        var obj = userMapper.toEntity(request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExisist("User Already Exist");
        }
        obj.setPassword(passwordEncoder.encode(request.getPassword()));
        obj.setRole(Role.USER);
        userRepository.save(obj);
        return userMapper.toDto(obj);
    }

    public UserDto getUserById(Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserNotFound();
        }

        return userMapper.toDto(user);
    }

    public void deleteUser(Long id){
        var u  = userRepository.findById(id).orElse(null);
        if(u == null){
            throw  new UserNotFound();
        }

        userRepository.delete(u);
    }

    public UserDto updateUser(UpdateUserRequest request , Long id){
        var u  = userRepository.findById(id).orElse(null);
        if(u == null){
            throw  new UserNotFound();
        }
        if(!isUserIsOwnerOrAdmin(id)){
            throw new NotEnoughPrevilagesException("You Don't Have Enough Privileges");
        }
        u.setName(request.getName());
        u.setEmail(request.getEmail());
        userRepository.save(u);

        return userMapper.toDto(u);
    }

    private boolean isUserIsOwnerOrAdmin(Long argUserId){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if(Role.ADMIN.name().equals(role.replace("ROLE_", ""))) {
            return true;
        }
        return userId.equals(argUserId);
    }
}

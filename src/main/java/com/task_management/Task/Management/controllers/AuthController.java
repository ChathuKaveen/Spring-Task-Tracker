package com.task_management.Task.Management.controllers;

import com.task_management.Task.Management.config.JwtConfig;
import com.task_management.Task.Management.dtos.JwtResponse;
import com.task_management.Task.Management.dtos.LoginRequest;
import com.task_management.Task.Management.dtos.UserDto;
import com.task_management.Task.Management.mappers.UserMapper;
import com.task_management.Task.Management.repositories.UserRepository;
import com.task_management.Task.Management.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/auth")
@CrossOrigin(origins = "http://localhost:5174")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request , HttpServletResponse response){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var userObj = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateAccessToken(userObj);
        var role = jwtService.getRoleFromToken(token);
        var refreshToken = jwtService.generateRefreshToken(userObj);

        var cookie = new Cookie("refreshToken" , refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new JwtResponse(role , request.getEmail(), token));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @PostMapping("/validate")
//    public boolean validate(@RequestHeader("Authorization") String authHeader){
//        var token = authHeader.replace("Bearer " ,"");
//        return jwtService.validateToken(token);
//    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken){
        if(!jwtService.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var role = jwtService.getRoleFromToken(accessToken);
        var email = jwtService.getEmailFromToken(accessToken);
        return ResponseEntity.ok(new JwtResponse(role ,email, accessToken));

    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        var user = userRepository.findById(userId).orElse(null);

        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(userMapper.toDto(user));
    }


}

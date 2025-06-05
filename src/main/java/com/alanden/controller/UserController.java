package com.alanden.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alanden.dto.UserDTO;
import com.alanden.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/userQuery")
    public ResponseEntity<List<UserDTO>> getCurrentUser(@RequestParam("q") String username) {        
        List<UserDTO> data = userService.query(username);
        return ResponseEntity.ok(data);
    }
}
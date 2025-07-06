package com.seha.abhomes.controller;

import com.seha.abhomes.dto.UserRequestDTO;
import com.seha.abhomes.dto.UserResponseDTO;
import com.seha.abhomes.mapper.UserMapper;
import com.seha.abhomes.model.User;
import com.seha.abhomes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(userMapper.toResponseDTO(newUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userMapper.toResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(userMapper.toResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(userMapper.toResponseDTO(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<UserResponseDTO> promoteToAdmin(@PathVariable Long id) {
        try {
            User promotedUser = userService.promoteToAdmin(id);
            return ResponseEntity.ok(userMapper.toResponseDTO(promotedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
package com.omerfbuber.controllers;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.services.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        return userService.save(request);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UpdateUserRequest request) {
        return userService.update(request);
    }

    @PutMapping("/change_password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return userService.delete(id);
    }


}

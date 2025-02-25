package com.omerfbuber.controllers;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.extensions.ResponseEntityExtension;
import com.omerfbuber.services.users.UserService;
import jakarta.validation.Valid;
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
        return ResponseEntityExtension.okOrProblem(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable long id) {
        return ResponseEntityExtension.okOrProblem(userService.getById(id));
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getNames() {
        return ResponseEntityExtension.okOrProblem(userService.getAllFullNames());
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntityExtension.createdOrProblem(userService.save(request));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntityExtension.noContentOrProblem(userService.update(request));
    }

    @PutMapping("/change_password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return ResponseEntityExtension.noContentOrProblem(userService.changePassword(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return ResponseEntityExtension.noContentOrProblem(userService.delete(id));
    }


}

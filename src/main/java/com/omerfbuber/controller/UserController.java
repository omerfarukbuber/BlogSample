package com.omerfbuber.controller;

import com.omerfbuber.dto.user.ChangePasswordRequest;
import com.omerfbuber.dto.user.CreateUserRequest;
import com.omerfbuber.dto.user.UpdateUserRequest;
import com.omerfbuber.dto.user.UserResponse;
import com.omerfbuber.extension.ResponseEntityExtension;
import com.omerfbuber.service.shared.CustomUserDetails;
import com.omerfbuber.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
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
    public ResponseEntity<Void> delete(@PathVariable long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntityExtension.noContentOrProblem(userService.delete(id, customUserDetails));
    }


}

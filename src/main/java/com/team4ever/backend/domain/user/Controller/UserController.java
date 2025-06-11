// src/main/java/com/team4ever/backend/user/controller/UserController.java
package com.team4ever.backend.domain.user.Controller;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    // 신규 회원 생성
    @PostMapping
    public ResponseEntity<Long> createUser(
            @Valid @RequestBody CreateUserRequest req
    ) {
        Long id = svc.createUser(req);
        return ResponseEntity.ok(id);
    }

    // userId로 회원 정보 조회
    @GetMapping
    public ResponseEntity<UserResponse> getUser(
            @RequestParam("userId") String userId
    ) {
        UserResponse dto = svc.getUserByUserId(userId);
        return ResponseEntity.ok(dto);
    }
}

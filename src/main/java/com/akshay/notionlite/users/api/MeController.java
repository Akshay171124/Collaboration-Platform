package com.akshay.notionlite.users.api;

import com.akshay.notionlite.common.ApiException;
import com.akshay.notionlite.security.SecurityUtil;
import com.akshay.notionlite.users.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserRepository userRepo;

    public MeController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    record MeRes(UUID id, String email, String fullName, String createdAt) {}

    @GetMapping
    public MeRes me() {
        UUID me = SecurityUtil.currentUserId();
        var u = userRepo.findById(me).orElseThrow(() -> ApiException.unauthorized("User not found"));
        return new MeRes(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt().toString());
    }
}

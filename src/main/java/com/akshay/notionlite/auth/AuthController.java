package com.akshay.notionlite.auth;

import com.akshay.notionlite.security.JwtTokenProvider;
import com.akshay.notionlite.users.model.UserEntity;
import com.akshay.notionlite.users.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

record RegisterReq(String email, String password, String fullName) {}
record LoginReq(String email, String password) {}
record AuthRes(String token, UserRes user) {}
record UserRes(String id, String email, String fullName) {}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder, JwtTokenProvider jwt) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public AuthRes register(@RequestBody RegisterReq req) {
        if (userRepo.existsByEmail(req.email())) throw new IllegalArgumentException("Email already used");

        UserEntity u = new UserEntity();
        u.setEmail(req.email());
        u.setFullName(req.fullName());
        u.setPasswordHash(encoder.encode(req.password()));
        u = userRepo.save(u);

        String token = jwt.generate(u.getId(), u.getEmail());
        return new AuthRes(token, new UserRes(u.getId().toString(), u.getEmail(), u.getFullName()));
    }

    @PostMapping("/login")
    public AuthRes login(@RequestBody LoginReq req) {
        UserEntity u = userRepo.findByEmail(req.email()).orElseThrow(() -> new IllegalArgumentException("Invalid creds"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) throw new IllegalArgumentException("Invalid creds");

        String token = jwt.generate(u.getId(), u.getEmail());
        return new AuthRes(token, new UserRes(u.getId().toString(), u.getEmail(), u.getFullName()));
    }
}

package com.studencollabfin.server.controller;

import com.studencollabfin.server.config.JwtUtil;
import com.studencollabfin.server.dto.AuthenticationRequest;
import com.studencollabfin.server.dto.AuthenticationResponse;
import com.studencollabfin.server.model.User;
import com.studencollabfin.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            User user = userService.authenticate(request.getEmail(), request.getPassword());
            final String jwt = jwtUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(new AuthenticationResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isProfileCompleted()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.getUsernameFromToken(jwt);
            User user = userService.findByEmail(email);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

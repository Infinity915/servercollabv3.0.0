package com.studencollabfin.server.controller;

import com.studencollabfin.server.model.User;
import com.studencollabfin.server.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SecurityController {

    @Autowired
    private UserService userService;

    @GetMapping("/login/success")
    public void loginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
        if (principal != null) {
            String id = principal.getName(); // The unique ID from LinkedIn
            String name = principal.getAttribute("name");
            String pictureUrl = principal.getAttribute("picture");
            String email = principal.getAttribute("email");

            User user = userService.findOrCreateUserByOauth(id, name, pictureUrl, email);

            // Redirect to a frontend route that handles profile setup
            // We pass the user's database ID so the frontend knows who to update
            response.sendRedirect("http://localhost:5173/profile-setup?userId=" + user.getId());
        } else {
            response.sendRedirect("http://localhost:5173/login-failed");
        }
    }
}
package com.example.springbootjwtauthentication.controller.auth;

import com.example.springbootjwtauthentication.model.auth.UserModel;
import com.example.springbootjwtauthentication.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;


    @GetMapping("/users")
    public List<UserModel> users() {
        return authService.getUsers();
    }
    @GetMapping("/index")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Hello World");
    }
}

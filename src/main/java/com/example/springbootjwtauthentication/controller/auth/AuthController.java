package com.example.springbootjwtauthentication.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/index")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Hello World");
    }
}
